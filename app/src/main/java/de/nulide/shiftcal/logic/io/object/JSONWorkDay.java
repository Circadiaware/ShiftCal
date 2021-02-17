package de.nulide.shiftcal.logic.io.object;

public class JSONWorkDay {

    private JSONCalendarDate date;
    private int shift;
    private long evId;

    public JSONWorkDay() {
        this.evId = -1;
    }

    public JSONWorkDay(JSONCalendarDate date, int shift, long evId) {
        this.date = date;
        this.shift = shift;
        this.evId = evId;
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

    public long getEvId() {
        return evId;
    }

    public void setEvId(long evId) {
        this.evId = evId;
    }
}
