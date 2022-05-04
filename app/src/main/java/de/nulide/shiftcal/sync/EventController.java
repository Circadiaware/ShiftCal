package de.nulide.shiftcal.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;

import de.nulide.shiftcal.logic.object.Shift;
import de.nulide.shiftcal.logic.object.ShiftCalendar;
import de.nulide.shiftcal.logic.object.WorkDay;

public class EventController {

    private ContentResolver cr;
    private long calId;
    private ShiftCalendar sc;

    public EventController(ContentResolver cr, long calId, ShiftCalendar sc) {
        this.cr = cr;
        this.calId = calId;
        this.sc = sc;
    }

    public EventController(){

    }

    public long createEvent(WorkDay day) {
        if (cr != null) {
            if (calId == -1) {
                return -1;
            }
            Shift s = sc.getShiftById(day.getShift());
            Calendar startCal = Calendar.getInstance();
            startCal.set(day.getDate().getYear(), day.getDate().getMonth() - 1, day.getDate().getDay());
            startCal.set(Calendar.HOUR_OF_DAY, s.getStartTime().getHour());
            startCal.set(Calendar.MINUTE, s.getStartTime().getMinute());
            Calendar endCal = Calendar.getInstance();
            endCal.set(day.getDate().getYear(), day.getDate().getMonth() - 1, day.getDate().getDay());
            if (s.getStartTime().getHour() > s.getEndTime().getHour()) {
                endCal.add(Calendar.DAY_OF_MONTH, 1);
            }
            endCal.set(Calendar.HOUR_OF_DAY, s.getEndTime().getHour());
            endCal.set(Calendar.MINUTE, s.getEndTime().getMinute());
            long start = startCal.getTimeInMillis();
            long end = endCal.getTimeInMillis();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, start);
            values.put(CalendarContract.Events.DTEND, end);
            values.put(CalendarContract.Events.TITLE, s.getShort_name() + " - " + s.getName());
            values.put(CalendarContract.Events.CALENDAR_ID, calId);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Berlin");
            values.put(CalendarContract.Events.EVENT_COLOR, s.getColor());
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            return new Long(uri.getLastPathSegment());
        }
        return -1;
    }

    //not working
    public void deleteEvent(long evId) {
        if (cr != null) {
            String[] args = new String[]{Long.toString(evId)};
            cr.delete(CalendarContract.Events.CONTENT_URI, CalendarContract.Events._SYNC_ID+ " =? ", args);
        }
    }



}
