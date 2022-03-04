package de.nulide.shiftcal.ui;

import androidx.annotation.NonNull;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.format.DayFormatter;

import java.util.List;

import de.nulide.shiftcal.logic.object.Shift;
import de.nulide.shiftcal.logic.object.ShiftCalendar;

public class ShiftDayFormatter implements DayFormatter {

    private ShiftCalendar sc;

    public ShiftDayFormatter(ShiftCalendar sc) {
        this.sc = sc;
    }

    @NonNull
    @Override
    public String format(@NonNull CalendarDay day) {
        String format = "" + day.getDay();
        List<Shift> s = sc.getShiftsByDate(day);
        if (s.size() > 0) {
            format += "\n" + s.get(0).getShort_name();
            if (s.size() > 1) {
                format += "\n" + s.get(1).getShort_name();
            }
        }
        return format;
    }
}
