package de.nulide.shiftcal.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class DarkModeDecorator implements DayViewDecorator {

    Context context;

    public DarkModeDecorator(Activity context){
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        int i = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if(i == Configuration.UI_MODE_NIGHT_YES) {
            view.addSpan(new ForegroundColorSpan(Color.WHITE));
        }else{
            view.addSpan(new ForegroundColorSpan(Color.BLACK));
        }
    }
}
