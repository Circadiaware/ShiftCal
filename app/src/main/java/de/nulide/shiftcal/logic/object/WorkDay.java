package de.nulide.shiftcal.logic.object;

import com.prolificinteractive.materialcalendarview.CalendarDay;

public class WorkDay {
    private CalendarDate date;
    private int shift;
    private long evId;

    public WorkDay(CalendarDate date, int shift) {
        this.date = date;
        this.shift = shift;
        this.evId = -1;
    }

    public WorkDay(CalendarDay date, int shift) {
        CalendarDate day = new CalendarDate(date.getYear(), date.getMonth(), date.getDay());
        this.date = day;
        this.shift = shift;
        this.evId = -1;
    }

    public WorkDay(CalendarDate date, int shift, long evId) {
        this.date = date;
        this.shift = shift;
        this.evId = evId;
    }

    public WorkDay(CalendarDay date, int shift, long evId) {
        CalendarDate day = new CalendarDate(date.getYear(), date.getMonth(), date.getDay());
        this.date = day;
        this.shift = shift;
        this.evId = evId;
    }

    public CalendarDate getDate() {
        return date;
    }

    public void setDate(CalendarDate date) {
        this.date = date;
    }

    public int getShift() {
        return shift;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public boolean checkDate(CalendarDay d) {
        if (this.date.getYear() == d.getYear()) {
            if (this.date.getMonth() == d.getMonth()) {
                return this.date.getDay() == d.getDay();
            }
        }
        return false;
    }

    public long getEvId() {
        return evId;
    }

    public void setEvId(long evId) {
        this.evId = evId;
    }
}
