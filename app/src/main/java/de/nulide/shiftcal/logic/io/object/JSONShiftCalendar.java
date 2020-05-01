package de.nulide.shiftcal.logic.io.object;

import java.util.LinkedList;

public class JSONShiftCalendar {

    private LinkedList<JSONWorkDay> calendar;
    private LinkedList<JSONShift> shifts;

    public JSONShiftCalendar() {
        calendar = new LinkedList<>();
        shifts = new LinkedList<>();
    }

    public JSONShiftCalendar(LinkedList<JSONWorkDay> calendar, LinkedList<JSONShift> shifts) {
        this.calendar = calendar;
        this.shifts = shifts;
    }

    public LinkedList<JSONWorkDay> getCalendar() {
        return calendar;
    }

    public void setCalendar(LinkedList<JSONWorkDay> calendar) {
        this.calendar = calendar;
    }

    public LinkedList<JSONShift> getShifts() {
        return shifts;
    }

    public void setShifts(LinkedList<JSONShift> shifts) {
        this.shifts = shifts;
    }
}
