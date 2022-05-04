package de.nulide.shiftcal.logic.object;

import java.util.Calendar;

public class CDateTime {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    public CDateTime(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public CDateTime(int year, int month, int day, int hour, int minute){
        this(year, month, day);
        this.hour = hour;
        this.minute = minute;
    }

    private Calendar getCalendar(){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute, 0);
        return cal;
    }
    protected void setNewDate(Calendar cal){
        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH);
        this.day = cal.get(Calendar.DAY_OF_MONTH);
        this.hour = cal.get(Calendar.HOUR_OF_DAY);
        this.minute = cal.get(Calendar.MINUTE);
    }

    public int getYear() {
        return year;
    }

    public void addYear(int year) {
        this.year += year;
    }

    public int getMonth() {
        return month;
    }

    public void addMonth(int month) {
        this.month += month;
        if(month >= 11){
            month = 0;
            year += 1;
        }
    }

    public int getDay() {
        return day;
    }

    public void addDay(int day) {
        Calendar cal = getCalendar();
        cal.add(Calendar.DAY_OF_MONTH, day);
        setNewDate(cal);
    }

    public int getHour() {
        return hour;
    }

    public void addHour(int hour) {
        Calendar cal = getCalendar();
        cal.add(Calendar.HOUR, hour);
        setNewDate(cal);
    }

    public int getMinute() {
        return minute;
    }

    public void addMinute(int minute) {
        Calendar cal = getCalendar();
        cal.add(Calendar.MINUTE, minute);
        setNewDate(cal);
    }

    public boolean newerThan(CDateTime toCheck){
        if(this.year > toCheck.year){
            return true;
        }else{
            if(this.year == toCheck.year){
                if (this.month > toCheck.month) {
                    return true;
                } else {
                    if (this.month == toCheck.month) {
                        if (this.day > toCheck.day) {
                            return true;
                        } else {
                            if (this.day == toCheck.day) {
                                if (this.hour > toCheck.hour) {
                                    return true;
                                } else {
                                    if (this.hour == toCheck.hour) {
                                        return this.minute > toCheck.minute;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isSameDay(CDateTime toCheck){
        if(this.year == toCheck.year && this.month == toCheck.month && this.day == toCheck.day){
            return true;
        }
        return false;
    }
}
