package de.nulide.shiftcal.logic.object;

import androidx.annotation.NonNull;

public class ShiftTime {

    private int hour;
    private int minute;

    public ShiftTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int toInt() {
        return hour * 60 + minute;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int hourLength = (int) (Math.log10(hour) + 1);
        if (hour == 0) {
            hourLength = 1;
        }
        int minuteLength = (int) (Math.log10(minute) + 1);
        if (minute == 0) {
            minuteLength = 1;
        }
        if (hourLength == 1) {
            sb.append(0);
        }
        sb.append(hour).append(":");
        if (minuteLength == 1) {
            sb.append(0);
        }
        sb.append(minute);
        return sb.toString();
    }
}
