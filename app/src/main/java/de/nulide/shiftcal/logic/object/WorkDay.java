package de.nulide.shiftcal.logic.object;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.io.Serializable;
import java.util.Date;

public class WorkDay implements Serializable {
    private CalendarDate date;
    private Shift shift;

    public WorkDay(CalendarDate date, Shift shift) {
        this.date = date;
        this.shift = shift;
    }

    public CalendarDate getDate() {
        return date;
    }

    public void setDate(CalendarDate date) {
        this.date = date;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public boolean checkDate(CalendarDay d){
        if(this.date.getYear() == d.getYear()){
            if(this.date.getMonth() == d.getMonth()){
                if(this.date.getDay() == d.getDay()) {
                    return true;
                }
            }
        }
        return false;
    }
}
