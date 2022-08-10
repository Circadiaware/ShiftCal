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
import de.nulide.shiftcal.logic.object.Shift;
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
    private int DND_ID_START = 45;
    private int DND_ID_STOP = 46;

    private int TYPE_ALARM = 30;
    private int TYPE_DND = 40;

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
                    AlarmManager.AlarmClockInfo ac =
                            new AlarmManager.AlarmClockInfo(nearestCalendar.getTimeInMillis(),
                                        pi);
                    mgr.setAlarmClock(ac, pi);

                }else{
                    removeAll(t, TYPE_ALARM);
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
                WorkDay toDND = sc.getRunningShift(TimeFactory.convertCalendarToCDateTime(today));

                if (toDND == null) {
                    toDND = sc.getUpcomingShift(TimeFactory.convertCalendarToCDateTime(today), false, 0);
                    if (toDND == null) {
                        return;
                    }
                }
                Shift shiftTDND = sc.getShiftById(toDND.getShift());
                Calendar calStart = Calendar.getInstance();
                calStart.set(toDND.getDate().getYear(), toDND.getDate().getMonth() - 1, toDND.getDate().getDay(), shiftTDND.getStartTime().getHour(), shiftTDND.getStartTime().getMinute(), 0);

                Calendar calStop = Calendar.getInstance();
                calStop.set(toDND.getDate().getYear(), toDND.getDate().getMonth() - 1, toDND.getDate().getDay(), shiftTDND.getEndTime().getHour(), shiftTDND.getEndTime().getMinute(), 0);

                if(shiftTDND.getStartTime().getHour() > shiftTDND.getEndTime().getHour()){
                    calStop.add(Calendar.DAY_OF_MONTH, 1);
                }

                AlarmManager mgr = (AlarmManager) t.getSystemService(Context.ALARM_SERVICE);

                Intent intentStart = new Intent(t, DNDReceiver.class);
                intentStart.putExtra(DNDReceiver.DND_START_STOP, DNDReceiver.START);
                PendingIntent pisa = PendingIntent.getBroadcast(t, DNDReceiver.DND_ID_START, intentStart, PendingIntent.FLAG_UPDATE_CURRENT);
                createSilentAlarm(pisa, intentStart, mgr, calStart);

                Intent intentStop = new Intent(t, DNDReceiver.class);
                intentStop.putExtra(DNDReceiver.DND_START_STOP, DNDReceiver.STOP);
                PendingIntent piso = PendingIntent.getBroadcast(t, DNDReceiver.DND_ID_STOP, intentStop, PendingIntent.FLAG_UPDATE_CURRENT);
                createSilentAlarm(piso, intentStop, mgr, calStop);
            }else{
                removeAll(t, TYPE_DND);
            }
        }
    }

    public static void createSilentAlarm(PendingIntent pi, Intent i, AlarmManager mgr, Calendar date){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), pi);
        } else {
                mgr.setExact(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), pi);
        }
    }

    private void removeAll(Context t, int type) {
        settings = IO.readSettings(f);
        try {
            ShiftCalendar sc = IO.readShiftCal(f);
            AlarmManager mgr = (AlarmManager) t.getSystemService(Context.ALARM_SERVICE);
            if(type == TYPE_ALARM) {
                Intent intent = new Intent(t, AlarmReceiver.class);
                PendingIntent pi = PendingIntent.getBroadcast(t, ALARM_ID, intent, 0);
                mgr.cancel(pi);
            }else if(type == TYPE_DND) {
                Intent intent = new Intent(t, DNDReceiver.class);
                PendingIntent pi = PendingIntent.getBroadcast(t, DNDReceiver.DND_ID_START, intent, PendingIntent.FLAG_ONE_SHOT);
                mgr.cancel(pi);
                pi = PendingIntent.getBroadcast(t, DNDReceiver.DND_ID_STOP, intent, PendingIntent.FLAG_ONE_SHOT);
                mgr.cancel(pi);
            }
        } catch (Exception e) {
            e.printStackTrace();
            settings.setSetting(Settings.SET_ALARM_MINUTES, "0");
        }
    }
}
