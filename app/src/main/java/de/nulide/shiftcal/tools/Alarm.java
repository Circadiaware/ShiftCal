package de.nulide.shiftcal.tools;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.io.File;
import java.util.Calendar;

import de.nulide.shiftcal.receiver.AlarmReceiver;
import de.nulide.shiftcal.logic.io.CalendarIO;
import de.nulide.shiftcal.logic.io.SettingsIO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.logic.object.ShiftCalendar;
import de.nulide.shiftcal.logic.object.WorkDay;

public class Alarm {

    public static final String EXT_SHIFT = "EXTRA_SHIFT_ID";
    File f;
    private Settings settings;

    public Alarm(File f) {
        this.f = f;
    }

    public void setAlarm(Context t) {
        settings = SettingsIO.readSettings(f);
        try {
            if (settings.isAvailable(Settings.SET_ALARM_ON_OFF) &&settings.isAvailable(Settings.SET_ALARM_MINUTES)) {
                if(new Boolean(settings.getSetting(Settings.SET_ALARM_ON_OFF))) {
                    Calendar today = Calendar.getInstance();
                    Calendar nearest = null;
                    int id = 0;
                    ShiftCalendar sc = CalendarIO.readShiftCal(f);
                    AlarmManager mgr = (AlarmManager) t.getSystemService(Context.ALARM_SERVICE);
                    int minutes = Integer.parseInt(settings.getSetting(Settings.SET_ALARM_MINUTES));
                    for (int i = 0; i < sc.getCalendarSize(); i++) {
                        WorkDay d = sc.getWdayByIndex(i);
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
    }

    public void removeAll(Context t) {
        settings = SettingsIO.readSettings(f);
        try {
            ShiftCalendar sc = CalendarIO.readShiftCal(f);
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
        } catch (Exception e) {
            e.printStackTrace();
            settings.setSetting(Settings.SET_ALARM_MINUTES, "0");
        }
    }
}
