package de.nulide.shiftcal.logic.object;

import com.prolificinteractive.materialcalendarview.CalendarDay;

public class CalendarDate {

    private int year;
    private int month;
    private int day;

    public CalendarDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public CalendarDate(CalendarDay day) {
        this.year = day.getYear();
        this.month = day.getMonth();
        this.day = day.getDay();
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void addMonth(int month){
        this.month+= month;
        if(this.month > 11){
            this.month = this.month-12;
            this.year+=1;
        }
        if(this.month < 0){
            this.year-=1;
            this.month = 12+this.month;
        }
    }

    public int getCompareableNumber(){
        return getYear()*10000000 + getMonth()*1000 + getDay()*10;
    }

    public boolean inRange(CalendarDate min, CalendarDate max){
        if(this.getCompareableNumber() > min.getCompareableNumber()){
            if(this.getCompareableNumber() < max.getCompareableNumber()){
                return true;
            }
        }
        return false;
    }
}
