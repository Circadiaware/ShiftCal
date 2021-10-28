package de.nulide.shiftcal.logic.io.object;

public class JSONWorkDay {

    private JSONCalendarDate date;
    private int shift;

    public JSONWorkDay(JSONCalendarDate date, int shift) {
        this.date = date;
        this.shift = shift;
    }

    public JSONWorkDay(){

    }

    public JSONCalendarDate getDate() {
        return date;
    }

    public void setDate(JSONCalendarDate date) {
        this.date = date;
    }

    public int getShift() {
        return shift;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

}
