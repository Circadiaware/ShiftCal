package de.nulide.shiftcal.logic.object;

import android.graphics.Color;

import de.nulide.shiftcal.ui.ShiftDayViewDecorator;

public class Shift {
    private String name;
    private String short_name;
    private int id;
    private ShiftTime startTime;
    private ShiftTime endTime;
    private int color;
    private ShiftDayViewDecorator decorator;
    private boolean toAlarm;

    public Shift(String name, String short_name, int id, ShiftTime startTime, ShiftTime endTime, int color, boolean toAlarm) {
        this.name = name;
        this.short_name = short_name;
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.color = color;
        this.toAlarm = toAlarm;
    }

    public Shift() {
        this.name = "Error";
        this.short_name = "err";
        startTime = new ShiftTime(0, 0);
        endTime = new ShiftTime(0, 0);
        this.color = Color.BLACK;
        this.toAlarm = true;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ShiftTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ShiftTime startTime) {
        this.startTime = startTime;
    }

    public ShiftTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ShiftTime endTime) {
        this.endTime = endTime;
    }

    public ShiftDayViewDecorator getDecorator() {
        return decorator;
    }

    public void setDecorator(ShiftDayViewDecorator decorator) {
        this.decorator = decorator;
    }

    public boolean isToAlarm() {
        return toAlarm;
    }

    public void setToAlarm(boolean toAlarm) {
        this.toAlarm = toAlarm;
    }
}

