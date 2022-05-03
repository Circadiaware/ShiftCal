package de.nulide.shiftcal.logic.object;

import java.util.Calendar;

public class TimeFactory {

    public static CDateTime convertCalendarDateToCDateTime(CalendarDate date){
        return new CDateTime(date.getYear(), date.getMonth()-1, date.getDay());
    }

    public static CDateTime convertCalendarToCDateTime(Calendar cal){
        CDateTime res = new CDateTime(0,0,0);
        res.setNewDate(cal);
        return  res;
    }

    public static CDateTime combineCDateTimeWithShiftTime(CDateTime dateTime, ShiftTime time){
        return new CDateTime(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(), time.getHour(), time.getMinute());
    }
}
