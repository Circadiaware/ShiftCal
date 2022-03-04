package de.nulide.shiftcal.ui;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import de.nulide.shiftcal.logic.object.Shift;
import de.nulide.shiftcal.logic.object.ShiftCalendar;

public class ShiftDayViewDecorator implements DayViewDecorator {

    private Shift shift;
    private ShiftCalendar sc;
    private boolean top;

    public ShiftDayViewDecorator(Shift shift, ShiftCalendar sc, boolean top) {
        this.shift = shift;
        this.sc = sc;
        this.top = top;
        shift.setDecorator(this, top);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return sc.checkIfShift(day, shift, top);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ColorFillSpan(shift.getColor(), top));
        int[] rgb = { Color.red(shift.getColor()), Color.green(shift.getColor()), Color.blue(shift.getColor()) };
        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1]
                * rgb[1] * .691 + rgb[2] * rgb[2] * .068);

        if (brightness >= 200) {
            view.addSpan(new ForegroundColorSpan(Color.BLACK));
        } else {
            view.addSpan(new ForegroundColorSpan(Color.WHITE));
        }
    }
}
