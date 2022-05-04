package de.nulide.shiftcal.sync;

import android.content.Context;

import de.nulide.shiftcal.R;
import de.nulide.shiftcal.logic.io.IO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.logic.object.ShiftCalendar;
import de.nulide.shiftcal.logic.object.WorkDay;
import de.nulide.shiftcal.tools.PermissionHandler;

public class SyncHandler implements Runnable {

    private final Context c;

    public SyncHandler(Context c) {
        this.c = c;
    }

    public static void sync(Context c) {
        if (!PermissionHandler.checkCalendar(c)) {
            return;
        }
        Settings settings  = IO.readSettings(c.getFilesDir());
        if(settings.isAvailable(Settings.SET_SYNC)){
            if(!new Boolean(settings.getSetting(Settings.SET_SYNC))){
                return;
            }
        }

        CalendarController.deleteCalendar(c.getContentResolver());
        new Thread(new SyncHandler(c)).start();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ShiftCalendar sc = IO.readShiftCal(c.getFilesDir());
        Settings settings = IO.readSettings(c.getFilesDir());
        long calId = CalendarController.getCalendarId(c.getContentResolver());
        if (calId == -1) {
            int color = c.getResources().getColor(R.color.colorPrimary);
            if (settings.isAvailable(Settings.SET_COLOR)) {
                color = Integer.parseInt(settings.getSetting(Settings.SET_COLOR));
            }
            CalendarController.addShiftCalCalendar(c, color);
            calId = CalendarController.getCalendarId(c.getContentResolver());
        }
        EventController ec = new EventController(c.getContentResolver(), calId, sc);
        for (int i = 0; i < sc.getCalendarSize(); i++) {
            WorkDay wd = sc.getWdayByIndex(i);
            ec.createEvent(sc.getWdayByIndex(i));
        }
    }
}
