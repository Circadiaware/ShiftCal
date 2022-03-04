package de.nulide.shiftcal.logic.io.object;

import java.util.ArrayList;
import java.util.List;

public class JSONShiftCalendar {

    private List<JSONWorkDay> calendar;
    private List<JSONShift> shifts;

    public JSONShiftCalendar() {
        calendar = new ArrayList<>();
        shifts = new ArrayList<>();
    }

    public JSONShiftCalendar(List<JSONWorkDay> calendar, List<JSONShift> shifts) {
        this.calendar = calendar;
        this.shifts = shifts;
    }

    public List<JSONWorkDay> getCalendar() {
        return calendar;
    }

    public void setCalendar(List<JSONWorkDay> calendar) {
        this.calendar = calendar;
    }

    public List<JSONShift> getShifts() {
        return shifts;
    }

    public void setShifts(List<JSONShift> shifts) {
        this.shifts = shifts;
    }
}
