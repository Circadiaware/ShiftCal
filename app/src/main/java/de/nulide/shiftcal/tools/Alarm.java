package de.nulide.shiftcal.tools;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.util.Calendar;

import de.nulide.shiftcal.logic.io.IO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.logic.object.ShiftCalendar;
import de.nulide.shiftcal.logic.object.TimeFactory;
import de.nulide.shiftcal.logic.object.WorkDay;
import de.nulide.shiftcal.receiver.AlarmReceiver;
import de.nulide.shiftcal.receiver.DNDReceiver;

public class Alarm {

    public static final String EXT_SHIFT = "EXTRA_SHIFT_ID";
    File f;
    private Settings settings;

    private int ALARM_ID = 35;
    private int DND_ID = 45;

    public Alarm(File f) {
        this.f = f;
    }

    public void setAlarm(Context t) {
        settings = IO.readSettings(f);
        try {
            if (settings.isAvailable(Settings.SET_ALARM_ON_OFF) &&settings.isAvailable(Settings.SET_ALARM_MINUTES)) {
                if(new Boolean(settings.getSetting(Settings.SET_ALARM_ON_OFF))) {
                    Calendar today = Calendar.getInstance();
                    ShiftCalendar sc = IO.readShiftCal(f);
                    AlarmManager mgr = (AlarmManager) t.getSystemService(Context.ALARM_SERVICE);
                    Integer minutes = Integer.parseInt(settings.getSetting(Settings.SET_ALARM_MINUTES));
                    WorkDay nearest = sc.getUpcomingShift(TimeFactory.convertCalendarToCDateTime(today), true, minutes);
                    if(nearest == null){
                        return;
                    }
                    Calendar nearestCalendar = Calendar.getInstance();
                    nearestCalendar.set(nearest.getDate().getYear(), nearest.getDate().getMonth()-1, nearest.getDate().getDay(), sc.getShiftById(nearest.getShift()).getStartTime().getHour(), sc.getShiftById(nearest.getShift()).getStartTime().getMinute(), 0);
                    nearestCalendar.add(Calendar.MINUTE, -minutes);

                    Intent intent = new Intent(t, AlarmReceiver.class);
                    intent.putExtra(EXT_SHIFT, nearest.getShift());
                    PendingIntent pi = PendingIntent.getBroadcast(t, ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        AlarmManager.AlarmClockInfo ac =
                                new AlarmManager.AlarmClockInfo(nearestCalendar.getTimeInMillis(),
                                        pi);
                        mgr.setAlarmClock(ac, pi);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            mgr.setExact(AlarmManager.RTC_WAKEUP, nearestCalendar.getTimeInMillis(), pi);
                        } else {
                            mgr.set(AlarmManager.RTC_WAKEUP, nearestCalendar.getTimeInMillis(), pi);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            settings.setSetting(Settings.SET_ALARM_MINUTES, "0");
        }
    }

    public void setDNDAlarm(Context t){
        settings = IO.readSettings(f);
        if(settings.isAvailable(Settings.SET_DND)){
            if(new Boolean(settings.getSetting(Settings.SET_DND))) {
                ShiftCalendar sc = IO.readShiftCal(f);
                Calendar today = Calendar.getInstance();
                WorkDay running = sc.getRunningShift(TimeFactory.convertCalendarToCDateTime(today));
                Intent intent = new Intent(t, DNDReceiver.class);
                AlarmManager mgr = (AlarmManager) t.getSystemService(Context.ALARM_SERVICE);

                if (running == null) {
                    WorkDay nearest = sc.getUpcomingShift(TimeFactory.convertCalendarToCDateTime(today), false, 0);
                    if (nearest == null) {
                        return;
                    } else {
                        Calendar nearestCal = Calendar.getInstance();
                        nearestCal.set(running.getDate().getYear(), running.getDate().getMonth() - 1, running.getDate().getDay(), sc.getShiftById(running.getShift()).getStartTime().getHour(), sc.getShiftById(running.getShift()).getStartTime().getMinute(), 0);

                        intent.putExtra(DNDReceiver.DND_START_STOP, DNDReceiver.START);
                        PendingIntent pi = PendingIntent.getBroadcast(t, DNDReceiver.DND_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        createSilentAlarm(pi, intent, mgr, nearestCal);
                    }
                } else {
                    Calendar runningCal = Calendar.getInstance();
                    runningCal.set(running.getDate().getYear(), running.getDate().getMonth() - 1, running.getDate().getDay(), sc.getShiftById(running.getShift()).getEndTime().getHour(), sc.getShiftById(running.getShift()).getEndTime().getMinute() + 1, 0);

                    intent.putExtra(DNDReceiver.DND_START_STOP, DNDReceiver.STOP);
                    PendingIntent pi = PendingIntent.getBroadcast(t, DNDReceiver.DND_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    createSilentAlarm(pi, intent, mgr, runningCal);
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
