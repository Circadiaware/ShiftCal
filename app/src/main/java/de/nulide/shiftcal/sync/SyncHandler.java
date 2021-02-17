package de.nulide.shiftcal.sync;

import android.content.Context;

import de.nulide.shiftcal.R;
import de.nulide.shiftcal.logic.io.IO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.logic.object.ShiftCalendar;
import de.nulide.shiftcal.logic.object.WorkDay;

public class SyncHandler {

    public static void sync(Context c){
        ShiftCalendar sc = IO.readShiftCal(c.getFilesDir());
        Settings settings = IO.readSettings(c.getFilesDir());
        long calId = CalendarController.getCalendarId(c.getContentResolver());
        if(calId == -1){
            int color = c.getResources().getColor(R.color.colorPrimary);
            if(settings.isAvailable(Settings.SET_COLOR)){
                color = Integer.parseInt(settings.getSetting(Settings.SET_COLOR));
            }
            CalendarController.addShiftCalCalendar(c, color);
            calId = CalendarController.getCalendarId(c.getContentResolver());
        }
        EventController ec = new EventController(c.getContentResolver(), calId, sc);
        for(int i = 0; i<sc.getCalendarSize(); i++){
            WorkDay wd = sc.getWdayByIndex(i);
            if(wd.getEvId() == -1){
                wd.setEvId(ec.createEvent(sc.getWdayByIndex(i)));
            }
        }
        IO.writeShiftCal(c.getFilesDir(), c, sc);
    }


}
