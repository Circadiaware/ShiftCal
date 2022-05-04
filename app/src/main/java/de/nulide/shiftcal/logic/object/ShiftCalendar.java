package de.nulide.shiftcal.logic.object;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

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

    public WorkDay getUpcomingShift(CDateTime now, Boolean respectToAlarm, int preMinutes) {
        WorkDay nearest = null;
        for (WorkDay wday : calendar) {
            CDateTime date = TimeFactory.convertCalendarDateToCDateTime(wday.getDate());
            Shift shift = getShiftById(wday.getShift());
            CDateTime dateTime = TimeFactory.combineCDateTimeWithShiftTime(date, shift.getStartTime());
            dateTime.addMinute(-preMinutes);
            //Check if day is after or today

            //Check if only shifts that can alarm shall be searched
            if (!respectToAlarm || shift.isToAlarm()) {

                if (dateTime.newerThan(now)) {
                    if (nearest == null) {
                        nearest = wday;
                    } else {
                        Shift nshift = getShiftById(nearest.getShift());
                        CDateTime nDate = TimeFactory.combineCDateTimeWithShiftTime(TimeFactory.convertCalendarDateToCDateTime(nearest.getDate()), nshift.getStartTime());
                        nDate.addMinute(-preMinutes);
                        if (nDate.newerThan(dateTime)) {
                            nearest = wday;
                        }
                    }
                }

            }
        }
        return nearest;
    }

    public WorkDay getRunningShift(CDateTime now) {
        for (WorkDay wday : calendar) {
            CDateTime date = TimeFactory.convertCalendarDateToCDateTime(wday.getDate());
            if (date.isSameDay(now)) {
                Shift shift = getShiftById(wday.getShift());
                CDateTime sdateTime = TimeFactory.combineCDateTimeWithShiftTime(date, shift.getStartTime());
                CDateTime edateTime = TimeFactory.combineCDateTimeWithShiftTime(date, shift.getEndTime());
                if(shift.getStartTime().getHour() > shift.getEndTime().getHour()){
                    edateTime.addDay(1);
                }
                if (now.newerThan(sdateTime) && edateTime.newerThan(now)) {
                    return wday;
                }
            }
        }
        return null;
    }
}
