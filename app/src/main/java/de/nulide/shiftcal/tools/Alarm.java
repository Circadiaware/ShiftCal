package de.nulide.shiftcal.tools;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.io.File;
import java.util.Calendar;

import de.nulide.shiftcal.logic.io.IO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.logic.object.ShiftCalendar;
import de.nulide.shiftcal.logic.object.WorkDay;
import de.nulide.shiftcal.receiver.AlarmReceiver;
import de.nulide.shiftcal.receiver.DNDReceiver;

public class Alarm {

    public static final String EXT_SHIFT = "EXTRA_SHIFT_ID";
    File f;
    private Settings settings;

    public Alarm(File f) {
        this.f = f;
    }

    public void setAlarm(Context t) {
        settings = IO.readSettings(f);
        try {
            if (settings.isAvailable(Settings.SET_ALARM_ON_OFF) &&settings.isAvailable(Settings.SET_ALARM_MINUTES)) {
                if(new Boolean(settings.getSetting(Settings.SET_ALARM_ON_OFF))) {
                    Calendar today = Calendar.getInstance();
                    Calendar nearest = null;
                    int id = 0;
                    ShiftCalendar sc = IO.readShiftCal(f);
                    AlarmManager mgr = (AlarmManager) t.getSystemService(Context.ALARM_SERVICE);
                    int minutes = Integer.parseInt(settings.getSetting(Settings.SET_ALARM_MINUTES));
                    for (int i = 0; i < sc.getCalendarSize(); i++) {
                        WorkDay d = sc.getWdayByIndex(i);
                        if (sc.getShiftById(d.getShift()).isToAlarm()) {
                            Calendar cal = Calendar.getInstance();
                            cal.set(d.getDate().getYear(), d.getDate().getMonth() - 1, d.getDate().getDay(), sc.getShiftById(d.getShift()).getStartTime().getHour(), sc.getShiftById(d.getShift()).getStartTime().getMinute(), 0);
                            cal.add(Calendar.MINUTE, -minutes);
                            if (nearest == null) {
                                if (today.getTimeInMillis() < cal.getTimeInMillis()) {
                                    nearest = cal;
                                    id = i;
                                }
                            } else if (nearest.getTimeInMillis() > cal.getTimeInMillis() && cal.getTimeInMillis() > today.getTimeInMillis()) {
                                nearest = cal;
                                id = i;
                            }
                            Intent intent = new Intent(t, AlarmReceiver.class);
                            PendingIntent pi = PendingIntent.getBroadcast(t, i, intent, 0);
                            mgr.cancel(pi);
                        }
                    }
                    if(nearest == null){
                        return;
                    }
                    Intent intent = new Intent(t, AlarmReceiver.class);
                    intent.putExtra(EXT_SHIFT, sc.getWdayByIndex(id).getShift());
                    PendingIntent pi = PendingIntent.getBroadcast(t, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        AlarmManager.AlarmClockInfo ac =
                                new AlarmManager.AlarmClockInfo(nearest.getTimeInMillis(),
                                        pi);
                        mgr.setAlarmClock(ac, pi);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            mgr.setExact(AlarmManager.RTC_WAKEUP, nearest.getTimeInMillis(), pi);
                        } else {
                            mgr.set(AlarmManager.RTC_WAKEUP, nearest.getTimeInMillis(), pi);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            settings.setSetting(Settings.SET_ALARM_MINUTES, "0");
        }
        setDNDAlarm(t);
    }

    public void setDNDAlarm(Context t){
        settings = IO.readSettings(f);
        if(settings.isAvailable(Settings.SET_DND)){
            if(new Boolean(settings.getSetting(Settings.SET_DND))){
                Calendar today = Calendar.getInstance();
                Calendar nearest = null;
                Calendar running = null;
                int idn = -1;
                int idr = -1;
                ShiftCalendar sc = IO.readShiftCal(f);
                for (int i = 0; i < sc.getCalendarSize(); i++) {
                    WorkDay d = sc.getWdayByIndex(i);
                    if (sc.getShiftById(d.getShift()).isToAlarm()) {
                        Calendar caln = Calendar.getInstance();
                        Calendar calr = Calendar.getInstance();
                        caln.set(d.getDate().getYear(), d.getDate().getMonth() - 1, d.getDate().getDay(), sc.getShiftById(d.getShift()).getStartTime().getHour(), sc.getShiftById(d.getShift()).getStartTime().getMinute(), 0);
                        calr.set(d.getDate().getYear(), d.getDate().getMonth() - 1, d.getDate().getDay(), sc.getShiftById(d.getShift()).getEndTime().getHour(), sc.getShiftById(d.getShift()).getEndTime().getMinute(), 0);
                        if (nearest == null) {
                            if (today.getTimeInMillis() < caln.getTimeInMillis()) {
                                nearest = caln;
                                idn = i;
                            }
                        } else if (nearest.getTimeInMillis() > caln.getTimeInMillis() && caln.getTimeInMillis() > today.getTimeInMillis()) {
                            nearest = caln;
                            idn = i;
                        }
                        if (running == null) {
                            if (today.getTimeInMillis() < calr.getTimeInMillis() && caln.getTimeInMillis() < today.getTimeInMillis()) {
                                running = calr;
                                idr = i;
                            }
                        }
                    }
                }
                Intent intent = new Intent(t, DNDReceiver.class);
                AlarmManager mgr = (AlarmManager) t.getSystemService(Context.ALARM_SERVICE);

                if(idr != -1){
                    Intent oldIntent = new Intent(t, DNDReceiver.class);
                    oldIntent.putExtra(DNDReceiver.DND_START_STOP, DNDReceiver.START);
                    PendingIntent oldpi = PendingIntent.getBroadcast(t, DNDReceiver.DND_ID, oldIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mgr.cancel(oldpi);
                    intent.putExtra(DNDReceiver.DND_START_STOP, DNDReceiver.STOP);
                    PendingIntent pi = PendingIntent.getBroadcast(t, DNDReceiver.DND_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    createSilentAlarm(pi, intent, mgr, running);
                }else {
                    intent.putExtra(DNDReceiver.DND_START_STOP, DNDReceiver.START);
                    PendingIntent pi = PendingIntent.getBroadcast(t, DNDReceiver.DND_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    createSilentAlarm(pi, intent, mgr, nearest);
                }
            }
        }
    }

    public static void createSilentAlarm(PendingIntent pi, Intent i, AlarmManager mgr, Calendar date){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), pi);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mgr.setExact(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), pi);
            } else {
                mgr.set(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), pi);
            }
        }
    }

    public void removeAll(Context t) {
        settings = IO.readSettings(f);
        try {
            ShiftCalendar sc = IO.readShiftCal(f);
            AlarmManager mgr = (AlarmManager) t.getSystemService(Context.ALARM_SERVICE);
            int minutes = Integer.parseInt(settings.getSetting(Settings.SET_ALARM_MINUTES));
            for (int i = 0; i < sc.getCalendarSize(); i++) {
                WorkDay d = sc.getWdayByIndex(i);
                Calendar cal = Calendar.getInstance();
                cal.set(d.getDate().getYear(), d.getDate().getMonth() - 1, d.getDate().getDay(), sc.getShiftById(d.getShift()).getStartTime().getHour(), sc.getShiftById(d.getShift()).getStartTime().getMinute(), 0);
                cal.add(Calendar.MINUTE, -minutes);
                Intent intent = new Intent(t, AlarmReceiver.class);
                intent.putExtra(EXT_SHIFT, sc.getWdayByIndex(i).getShift());
                PendingIntent pi = PendingIntent.getBroadcast(t, i, intent, 0);
                mgr.cancel(pi);
            }
            Intent intent = new Intent(t, DNDReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(t, DNDReceiver.DND_ID, intent, PendingIntent.FLAG_ONE_SHOT);
            mgr.cancel(pi);
        } catch (Exception e) {
            e.printStackTrace();
            settings.setSetting(Settings.SET_ALARM_MINUTES, "0");
        }
    }
}
