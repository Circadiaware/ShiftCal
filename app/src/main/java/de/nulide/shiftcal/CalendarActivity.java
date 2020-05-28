package de.nulide.shiftcal;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;

import de.nulide.shiftcal.logic.io.IO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.logic.object.Shift;
import de.nulide.shiftcal.logic.object.ShiftCalendar;
import de.nulide.shiftcal.logic.object.WorkDay;
import de.nulide.shiftcal.settings.SettingsActivity;
import de.nulide.shiftcal.settings.ThemeActivity;
import de.nulide.shiftcal.tools.ColorHelper;
import de.nulide.shiftcal.ui.DarkModeDecorator;
import de.nulide.shiftcal.ui.ShiftAdapter;
import de.nulide.shiftcal.ui.ShiftDayFormatter;
import de.nulide.shiftcal.ui.ShiftDayViewDecorator;
import de.nulide.shiftcal.ui.TodayDayViewDecorator;

public class CalendarActivity extends AppCompatActivity implements View.OnClickListener, OnDateSelectedListener, AdapterView.OnItemClickListener, View.OnTouchListener, PopupMenu.OnMenuItemClickListener, PopupMenu.OnDismissListener {

    private static ShiftCalendar sc;
    private static Settings settings;
    private static TextView tvName;
    private static TextView tvST;
    private static TextView tvET;
    private static FrameLayout fl;
    private static AlertDialog dialog;
    private static ImageButton btnPopup;
    private static PopupMenu popup;

    private static MaterialCalendarView calendar;

    private static FloatingActionButton fabEdit;
    private static boolean toEdit = false;

    public static Context con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        con = this;

        int color = getResources().getColor(R.color.colorPrimary);
        settings  = IO.readSettings(getFilesDir());
        if(settings.isAvailable(Settings.SET_COLOR)){
            color = Integer.parseInt(settings.getSetting(Settings.SET_COLOR));
        }
        ColorHelper.changeActivityColors(this, color);
        if(settings.isAvailable(Settings.SET_DARK_MODE)){
            ThemeActivity.setDarkMode(Integer.parseInt(settings.getSetting(Settings.SET_DARK_MODE)));
        }

        btnPopup = findViewById(R.id.btnPopup);
        btnPopup.setOnClickListener(this);
        popup = new PopupMenu(this, btnPopup);
        popup.setOnMenuItemClickListener(this);
        popup.setOnDismissListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_calendar, popup.getMenu());


        fabEdit = findViewById(R.id.fabEdit);
        fabEdit.setBackgroundTintList(ColorStateList.valueOf(color));
        fabEdit.setBackgroundColor(color);
        fabEdit.setOnClickListener(this);

        calendar = findViewById(R.id.calendarView);
        calendar.setDateSelected(CalendarDay.today(), true);
        calendar.setOnDateChangedListener(this);
        calendar.setSelectionColor(color);
        tvName = findViewById(R.id.cTextViewName);
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        tvST = findViewById(R.id.cTextViewST);
        tvET = findViewById(R.id.cTextViewET);
        fl = findViewById(R.id.CalendarTopLayer);


        updateCalendar();
        updateTextView();
    }

    public void updateCalendar() {
        sc = IO.readShiftCal(getFilesDir());
        calendar.removeDecorators();
        calendar.addDecorator(new DarkModeDecorator());
        for (int i = 0; i < sc.getShiftsSize(); i++) {
            calendar.addDecorator(new ShiftDayViewDecorator(sc.getShiftByIndex(i), sc));
        }
        calendar.addDecorator(new TodayDayViewDecorator());
        calendar.setDayFormatter(new ShiftDayFormatter(sc));

    }

    public void updateTextView() {
        Shift sel = sc.getShiftByDate(calendar.getSelectedDate());
        if (sel != null) {
            tvName.setTextColor(sel.getColor());
            tvName.setText(sel.getName());
            tvST.setText("Start Time: " + sel.getStartTime().toString());
            tvET.setText("End Time: " + sel.getEndTime().toString());
        } else {
            tvName.setText("");
            tvST.setText("");
            tvET.setText("");
        }
    }

    @Override
    public void onClick(View view) {
        if (view == fabEdit) {
            if (toEdit) {
                toEdit = false;
                fabEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit));
            } else {
                toEdit = true;
                fabEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_done));
            }
        } else if (view == btnPopup) {
            if(settings.isAvailable(Settings.SET_DARK_MODE)){
                int dm = Integer.parseInt(settings.getSetting(Settings.SET_DARK_MODE));
                if(dm == ThemeActivity.DARK_MODE_ON){
                    fl.setBackgroundColor(Color.argb(200, 0, 0, 0));

                }else{
                    fl.setBackgroundColor(Color.argb(200, 255, 255, 255));
                }
            }
            fl.setOnTouchListener(this);
            popup.show();

        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        if (toEdit) {
            LayoutInflater inflater = getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.dialog_shift_selector, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ListView listViewShifts = (ListView) dialoglayout;
            ShiftAdapter adapter = new ShiftAdapter(this, new ArrayList<Shift>(sc.getShiftList()));
            listViewShifts.setAdapter(adapter);
            adapter.add(new Shift("Delete", "D", -1, null, null, Color.RED));
            listViewShifts.setOnItemClickListener(this);
            builder.setView(dialoglayout);
            dialog = builder.create();
            dialog.show();

        } else {
            updateTextView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCalendar();
        updateTextView();
        fabEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit));
        toEdit = false;
        popup.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i < sc.getShiftsSize()) {
            if (!sc.hasWork(calendar.getSelectedDate())) {
                sc.addWday(new WorkDay(calendar.getSelectedDate(), sc.getShiftByIndex(i).getId()));
            }else{
                sc.deleteWday(calendar.getSelectedDate());
                sc.addWday(new WorkDay(calendar.getSelectedDate(), sc.getShiftByIndex(i).getId()));
            }
        } else {
            if (sc.hasWork(calendar.getSelectedDate())) {
                sc.deleteWday(calendar.getSelectedDate());
            }
        }
        dialog.cancel();
        IO.writeShiftCal(getFilesDir(), this, sc);
        updateCalendar();
        updateTextView();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        popup.dismiss();
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.iSettings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.iShifts:
                intent = new Intent(this, ShiftsActivity.class);
                startActivity(intent);
                return true;
        }

        return false;
    }

    @Override
    public void onDismiss(PopupMenu menu) {
        fl.setBackgroundColor(Color.TRANSPARENT);
        fl.setOnTouchListener(null);
    }
}
