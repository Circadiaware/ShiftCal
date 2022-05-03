package de.nulide.shiftcal.logic.object;

import android.content.ContentResolver;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.nulide.shiftcal.sync.CalendarController;
import de.nulide.shiftcal.sync.EventController;

public class ShiftCalendar {

    private List<WorkDay> calendar;
    private List<Shift> shifts;
    private int nextShiftId;

    public ShiftCalendar() {
        calendar = new ArrayList<>();
        shifts = new ArrayList<>();
        nextShiftId = 0;
    }

    public List<Shift> getShiftList() {
        List<Shift> shifts = new LinkedList<Shift>();
        for( Shift s : this.shifts){
            if(!s.isArchieved()){
                shifts.add(s);
            }
        }
        return shifts;
    }

    public void addWday(WorkDay wd) {
        this.calendar.add(wd);
    }

    public void deleteWorkDaysWithShift(int id) {
        for (int i = calendar.size() - 1; i >= 0; i--) {
            if (calendar.get(i).getShift() == id) {
                calendar.remove(i);
            }
        }
    }

    public Shift getShiftById(int id) {
        Shift s = new Shift();
        for (int i = 0; i < shifts.size(); i++) {
            if (shifts.get(i).getId() == id) {
                return shifts.get(i);
            }
        }
        return s;
    }

    public Shift getShiftByIndex(int i) {
        return shifts.get(i);
    }

    public Shift getShiftByDate(CalendarDay day) {
        for (int i = 0; i < this.calendar.size(); i++) {
            if (this.calendar.get(i).checkDate(day)) {
                return getShiftById(this.calendar.get(i).getShift());
            }
        }

        return null;
    }

    public List<Shift> getShiftsByDate(CalendarDay day) {
        List<Shift> shifts = new ArrayList<>();
        for (int i = 0; i < this.calendar.size(); i++) {
            if (this.calendar.get(i).checkDate(day)) {
                shifts.add(getShiftById(this.calendar.get(i).getShift()));
            }
        }
        sortShifts(shifts);
        return shifts;
    }

    public void deleteShift(int id) {
        for (int i = 0; i < shifts.size(); i++) {
            if (shifts.get(i).getId() == id) {
                shifts.remove(i);
                return;
            }
        }
    }

    public void deleteShiftByIndex(int i) {
        shifts.remove(i);
    }

    public void setShift(int id, Shift s) {
        for (int i = 0; i < shifts.size(); i++) {
            if (shifts.get(i).getId() == id) {
                shifts.set(i, s);
                return;
            }
        }
    }

    public void addShift(Shift s) {
        shifts.add(s);
        if (s.getId() >= nextShiftId) {
            nextShiftId = s.getId() +1;
        }
    }

    public int getShiftsSize() {
        return shifts.size();
    }

    public int getNextShiftId() {
        return nextShiftId;
    }

    public boolean checkIfShift(CalendarDay day, Shift s, boolean first) {
        List<Shift> dayShifts = new ArrayList<>();
        for (int i = 0; i < this.calendar.size(); i++) {
            if (this.calendar.get(i).checkDate(day)) {
                dayShifts.add(getShiftById(this.calendar.get(i).getShift()));
            }
        }
        sortShifts(dayShifts);

        if (dayShifts.size() == 0) return false;
        if (dayShifts.size() == 1) return dayShifts.get(0).getId() == s.getId();
        return dayShifts.get(first ? 0 : 1).getId() == s.getId();
    }

    public int getCalendarSize() {
        return calendar.size();
    }

    public WorkDay getWdayByIndex(int i) {
        return calendar.get(i);
    }

    public int getWdayIndexByDate(CalendarDay date) {
        for (int i = 0; i < calendar.size(); i++) {
            if (calendar.get(i).checkDate(date)) {
                return i;
            }
        }
        return -1;
    }

    public void deleteWday(CalendarDay date) {
        WorkDay wd = getWdayByIndex(getWdayIndexByDate(date));
        calendar.remove(wd);
    }

    public void deleteAllWday(CalendarDay date) {
        for (int i = calendar.size() - 1; i >= 0; --i) {
            WorkDay wd = calendar.get(i);
            if (wd.checkDate(date)) {
                calendar.remove(wd);
            }
        }
    }

    public boolean hasWork(CalendarDay date) {
        for (int i = 0; i < calendar.size(); i++) {
            if (calendar.get(i).checkDate(date)) {
                return true;
            }
        }
        return false;
    }

    public int getShiftIndexByDate(CalendarDay day) {
        Shift s = getShiftByDate(day);
        for (int i = 0; i < shifts.size(); i++){
            if(shifts.get(i).getId() == s.getId()){
                return i;
            }
        }
        return -1;
    }

    public ShiftCalendar getSTimeFrame(CalendarDay day){
        ShiftCalendar sortedCalendar = new ShiftCalendar();
        for(Shift s : shifts){
            sortedCalendar.addShift(s);
        }
        CalendarDate min = new CalendarDate(day);
        min.addMonth(-5);
        CalendarDate max = new CalendarDate(day);
        max.addMonth(5);
        for(WorkDay wday : calendar){
            if(wday.getDate().inRange(min, max)){
                sortedCalendar.addWday(wday);
            }
        }
        return sortedCalendar;
    }

    private void sortShifts(List<Shift> shifts) {
        Collections.sort(shifts, new Comparator<Shift>() {
            @Override
            public int compare(Shift s1, Shift s2) {
                return s1.getStartTime().toInt() - s2.getStartTime().toInt();
            }
        });
    }

    public WorkDay getUpcomingShift(Date nowDate, Boolean respectToAlarm){
        Calendar now = Calendar.getInstance();
        now.setTime(nowDate);

        WorkDay nearest = null;
        for(WorkDay wday : calendar){
            if(wday.getDate().getYear() > now.get(Calendar.YEAR) || (wday.getDate().getYear() == now.get(Calendar.YEAR) && wday.getDate().getMonth() > now.get(Calendar.MONTH)) || (wday.getDate().getYear() == now.get(Calendar.YEAR) && wday.getDate().getMonth() == now.get(Calendar.MONTH) && wday.getDate().getDay() >= now.get(Calendar.DAY_OF_MONTH))){
                Shift shift = getShiftById(wday.getShift());
                Boolean pass = true;
                if(respectToAlarm){
                    if(!shift.isToAlarm()){
                        pass = false;
                    }
                }
                if(pass && shift.getStartTime().getHour() > now.get(Calendar.HOUR) ||
                        (shift.getStartTime().getHour() == now.get(Calendar.HOUR) && shift.getStartTime().getMinute() > now.get(Calendar.MINUTE))){
                    if(nearest == null){
                        nearest = wday;
                    }else{
                        if(wday.getDate().getYear() < nearest.getDate().getYear() || (wday.getDate().getYear() == nearest.getDate().getYear() && wday.getDate().getMonth() < nearest.getDate().getMonth()) || (wday.getDate().getYear() == nearest.getDate().getYear() && wday.getDate().getMonth() == nearest.getDate().getMonth() && wday.getDate().getDay() < nearest.getDate().getDay())) {
                            nearest = wday;
                        }else if((wday.getDate().getYear() == nearest.getDate().getYear() && wday.getDate().getMonth() == nearest.getDate().getMonth() && wday.getDate().getDay() < nearest.getDate().getDay())){
                            Shift nshift = getShiftById(nearest.getShift());
                            if(shift.getStartTime().getHour() < nshift.getStartTime().getHour() ||
                                    (shift.getStartTime().getHour() == nshift.getStartTime().getHour() && shift.getStartTime().getMinute() < nshift.getStartTime().getMinute())){
                                nearest = wday;
                            }
                        }
                    }
                }
            }
        }
        return nearest;
    }

    public WorkDay getRunningShift(Date nowDate){
        Calendar now = Calendar.getInstance();
        now.setTime(nowDate);
        for(WorkDay wday: calendar){
            if(wday.getDate().getYear() == now.get(Calendar.YEAR) && wday.getDate().getMonth() == now.get(Calendar.MONTH) && wday.getDate().getDay() == now.get(Calendar.DAY_OF_MONTH)){
                Shift shift = getShiftById(wday.getShift());
                if((shift.getStartTime().getHour() == now.get(Calendar.HOUR) && shift.getStartTime().getMinute() < now.get(Calendar.MINUTE))
                    || (shift.getStartTime().getHour() < now.get(Calendar.HOUR))){
                    if(shift.getEndTime().getHour() > now.get(Calendar.HOUR) ||
                            (shift.getEndTime().getHour() == now.get(Calendar.HOUR) && shift.getEndTime().getMinute() > now.get(Calendar.MINUTE))){
                        return wday;
                    }
                }
            }
        }
        return null;
    }
}
