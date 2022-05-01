package de.nulide.shiftcal.logic.io.object;

public class JSONShift {

    private String name;
    private String short_name;
    private int id;
    private JSONShiftTime startTime;
    private JSONShiftTime endTime;
    private int color;
    private boolean toAlarm;
    private boolean archieved;

    public JSONShift() {
    }

    public JSONShift(String name, String short_name, int id, JSONShiftTime startTime, JSONShiftTime endTime, int color, boolean toAlarm, boolean archieved) {
        this.name = name;
        this.short_name = short_name;
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.color = color;
        this.toAlarm = toAlarm;
        this.archieved = archieved;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public JSONShiftTime getStartTime() {
        return startTime;
    }

    public void setStartTime(JSONShiftTime startTime) {
        this.startTime = startTime;
    }

    public JSONShiftTime getEndTime() {
        return endTime;
    }

    public void setEndTime(JSONShiftTime endTime) {
        this.endTime = endTime;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isToAlarm() {
        return toAlarm;
    }

    public void setToAlarm(boolean toAlarm) {
        this.toAlarm = toAlarm;
    }

    public boolean isArchieved() {
        return archieved;
    }

    public void setArchieved(boolean archieved) {
        this.archieved = archieved;
    }
}
