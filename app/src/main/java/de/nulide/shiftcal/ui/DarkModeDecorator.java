package de.nulide.shiftcal.ui;

import android.content.res.Configuration;
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import androidx.appcompat.app.AppCompatDelegate;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class DarkModeDecorator implements DayViewDecorator {

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        switch(AppCompatDelegate.getDefaultNightMode()){
            case AppCompatDelegate.MODE_NIGHT_YES:
                view.addSpan(new ForegroundColorSpan(Color.WHITE));
                break;
            default:
                view.addSpan(new ForegroundColorSpan(Color.BLACK));
                break;
        }
    }
}
