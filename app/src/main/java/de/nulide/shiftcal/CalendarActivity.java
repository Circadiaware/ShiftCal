package de.nulide.shiftcal;

import android.annotation.SuppressLint;
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
import com.niwattep.materialslidedatepicker.SlideDatePickerDialog;
import com.niwattep.materialslidedatepicker.SlideDatePickerDialogCallback;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.threeten.bp.DayOfWeek;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.nulide.shiftcal.logic.io.IO;
import de.nulide.shiftcal.logic.object.CalendarDate;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.logic.object.Shift;
import de.nulide.shiftcal.logic.object.ShiftCalendar;
import de.nulide.shiftcal.logic.object.WorkDay;
import de.nulide.shiftcal.settings.SettingsActivity;
import de.nulide.shiftcal.settings.ThemeActivity;
import de.nulide.shiftcal.sync.SyncHandler;
import de.nulide.shiftcal.tools.Alarm;
import de.nulide.shiftcal.tools.ColorHelper;
import de.nulide.shiftcal.ui.DarkModeDecorator;
import de.nulide.shiftcal.ui.ShiftAdapter;
import de.nulide.shiftcal.ui.ShiftDayFormatter;
import de.nulide.shiftcal.ui.ShiftDayViewDecorator;
import de.nulide.shiftcal.ui.TodayDayViewDecorator;

public class CalendarActivity extends AppCompatActivity implements View.OnClickListener, OnDateSelectedListener, AdapterView.OnItemClickListener, View.OnTouchListener, PopupMenu.OnMenuItemClickListener, PopupMenu.OnDismissListener, SlideDatePickerDialogCallback, OnMonthChangedListener {

    private static ShiftCalendar sc;
    private static Settings settings;
    private static TextView tvName;
    private static TextView tvST;
    private static TextView tvET;
    private static TextView tvWN;
    private static FrameLayout fl;
    private static AlertDialog dialog;
    private static ImageButton btnPopup;
    private static PopupMenu popup;

    private static MaterialCalendarView calendar;
    private static ShiftDayFormatter shiftFormatter;
    private static CalendarDate day;                        // The current year, to know for faster loading. See updateCalendar()

    private static FloatingActionButton fabShiftSelector;
    private static TextView tvFabShiftSelector;
    private static FloatingActionButton fabEdit;
    private static boolean toEdit = false;
    private ArrayList<Shift> shifts;
    private int shiftID = -1;


    public static Context con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        con = this;

        settings = IO.readSettings(getFilesDir());
        int color = ColorHelper.changeActivityColors(this, toolbar, settings);
        if (settings.isAvailable(Settings.SET_DARK_MODE)) {
            ThemeActivity.setDarkMode(Integer.parseInt(settings.getSetting(Settings.SET_DARK_MODE)));
        }

        btnPopup = findViewById(R.id.btnPopup);
        btnPopup.setOnClickListener(this);
        popup = new PopupMenu(this, btnPopup);
        popup.setOnMenuItemClickListener(this);
        popup.setOnDismissListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_calendar, popup.getMenu());


        fabShiftSelector = findViewById(R.id.fabShiftSelector);
        fabShiftSelector.setBackgroundTintList(ColorStateList.valueOf(color));
        fabShiftSelector.setBackgroundColor(color);
        fabShiftSelector.setOnClickListener(this);
        tvFabShiftSelector = findViewById(R.id.tvFabShiftSelector);

        fabEdit = findViewById(R.id.fabEdit);
        fabEdit.setBackgroundTintList(ColorStateList.valueOf(color));
        fabEdit.setBackgroundColor(color);
        fabEdit.setOnClickListener(this);

        calendar = findViewById(R.id.calendarView);
        if (settings.isAvailable(Settings.SET_START_OF_WEEK)) {
            switch (Integer.parseInt(settings.getSetting(Settings.SET_START_OF_WEEK))) {
                case 0:
                    calendar.state().edit()
                            .setFirstDayOfWeek(DayOfWeek.SUNDAY)
                            .commit();
                    break;
                case 1:
                    calendar.state().edit()
                            .setFirstDayOfWeek(DayOfWeek.MONDAY)
                            .commit();
                    break;

                case 2:
                    calendar.state().edit()
                            .setFirstDayOfWeek(DayOfWeek.TUESDAY)
                            .commit();
                    break;

                case 3:
                    calendar.state().edit()
                            .setFirstDayOfWeek(DayOfWeek.WEDNESDAY)
                            .commit();
                    break;

                case 4:
                    calendar.state().edit()
                            .setFirstDayOfWeek(DayOfWeek.THURSDAY)
                            .commit();
                    break;

                case 5:
                    calendar.state().edit()
                            .setFirstDayOfWeek(DayOfWeek.FRIDAY)
                            .commit();
                    break;

                case 6:
                    calendar.state().edit()
                            .setFirstDayOfWeek(DayOfWeek.SATURDAY)
                            .commit();
                    break;
            }
        }

        calendar.setDateSelected(CalendarDay.today(), true);
        calendar.setOnDateChangedListener(this);
        calendar.setSelectionColor(color);
        calendar.setOnMonthChangedListener(this);
        tvName = findViewById(R.id.cTextViewName);
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        tvST = findViewById(R.id.cTextViewST);
        tvET = findViewById(R.id.cTextViewET);
        tvWN = findViewById(R.id.cTextViewWN);
        fl = findViewById(R.id.CalendarTopLayer);

        sc = IO.readShiftCal(getFilesDir());
        updateCalendar();
        updateTextView();

        Alarm alarm = new Alarm(getFilesDir());
        alarm.setAlarm(this);
        alarm.setDNDAlarm(this);
    }

    public void updateCalendar() {
        calendar.removeDecorators();
        calendar.addDecorator(new DarkModeDecorator(this));
        ShiftCalendar sortedCalendar = sc.getSTimeFrame(calendar.getSelectedDate());
        for (int i = 0; i < sortedCalendar.getShiftsSize(); i++) {
            ShiftDayViewDecorator decoratorTop = new ShiftDayViewDecorator(sortedCalendar.getShiftByIndex(i), sortedCalendar, true);
            ShiftDayViewDecorator decoratorBottom = new ShiftDayViewDecorator(sortedCalendar.getShiftByIndex(i), sortedCalendar, false);
            calendar.addDecorator(decoratorTop);
            calendar.addDecorator(decoratorBottom);
        }
        calendar.addDecorator(new TodayDayViewDecorator());
        shiftFormatter = new ShiftDayFormatter(sortedCalendar);
        calendar.setDayFormatter(shiftFormatter);
        day = new CalendarDate(calendar.getSelectedDate());
    }

    public void updateSpecificCalendar(int sid) {
        if (sid != -1) {
            Shift shiftToUpdate = sc.getShiftById(sid);
            for (int top = 0; top < 2; ++top) {
                calendar.removeDecorator(shiftToUpdate.getDecorator(top == 1));
                ShiftDayViewDecorator decorator = new ShiftDayViewDecorator(shiftToUpdate, sc, top == 1);
                calendar.addDecorator(decorator);
            }
            shiftFormatter = new ShiftDayFormatter(sc);
            calendar.setDayFormatter(shiftFormatter);
        }
    }

    public void updateTextView() {
        CalendarDay selectedDate = calendar.getSelectedDate();
        List<Shift> sel = sc.getShiftsByDate(selectedDate);
        if (sel.size() == 1) {
            Shift shift = sel.get(0);
            tvName.setTextColor(shift.getColor());
            tvName.setText(shift.getName());
            tvST.setText("Start Time: " + shift.getStartTime().toString());
            tvET.setText("End Time: " + shift.getEndTime().toString());
        } else if (sel.size() == 2) {
            Shift shift1 = sel.get(0);
            Shift shift2 = sel.get(1);
            tvName.setTextColor(shift1.getColor());
            tvName.setText(shift1.getName() + " / " + shift2.getName());
            tvST.setText("Start Time: " + shift1.getStartTime().toString() + " / " + shift2.getStartTime().toString());
            tvET.setText("End Time: " + shift1.getEndTime().toString() + " / " + shift2.getEndTime().toString());
        } else {
            tvName.setText("");
            tvST.setText("");
            tvET.setText("");
        }
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(selectedDate.getYear(), selectedDate.getMonth() - 1, selectedDate.getDay());
        Integer weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        tvWN.setText(weekOfYear.toString());
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View view) {
        if (view == fabEdit) {
            if (toEdit) {
                toEdit = false;
                fabEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit));
                fabShiftSelector.setVisibility(View.INVISIBLE);
                tvFabShiftSelector.setVisibility(View.INVISIBLE);
                IO.writeShiftCal(getFilesDir(), this, sc);
                SyncHandler.sync(this);
            } else {
                toEdit = true;
                fabEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_done));
                fabShiftSelector.setVisibility(View.VISIBLE);
                tvFabShiftSelector.setVisibility(View.VISIBLE);
            }
        } else if (view == fabShiftSelector) {
            if (toEdit) {
                LayoutInflater inflater = getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.dialog_shift_selector, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                ListView listViewShifts = (ListView) dialoglayout;
                shifts = new ArrayList<Shift>(sc.getShiftList());
                shifts.add(new Shift("Delete", "D", -2, null, null, Color.RED, true, false));
                ShiftAdapter adapter = new ShiftAdapter(this, shifts);
                listViewShifts.setAdapter(adapter);
                listViewShifts.setOnItemClickListener(this);
                builder.setView(dialoglayout);
                dialog = builder.create();
                dialog.show();
            }
        } else if (view == btnPopup) {
            if (settings.isAvailable(Settings.SET_DARK_MODE)) {
                int dm = Integer.parseInt(settings.getSetting(Settings.SET_DARK_MODE));
                if (dm == ThemeActivity.DARK_MODE_ON) {
                    fl.setBackgroundColor(Color.argb(200, 0, 0, 0));

                } else {
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
            if (shiftID != -1) {
                CalendarDay csd = calendar.getSelectedDate();
                if (shiftID != -2) {
                    List<Shift> existingShifts = sc.getShiftsByDate(csd);
                    if (existingShifts.size() == 0) {
                        sc.addWday(new WorkDay(csd, shiftID));
                    } else if (existingShifts.size() == 1) {
                        if (existingShifts.get(0).getId() == shiftID) {
                            sc.deleteWday(csd);
                        }
                        sc.addWday(new WorkDay(csd, shiftID));
                    } else {
                        // for simplicity, just delete all existing shifts and add the selected one.
                        sc.deleteAllWday(csd);
                        sc.addWday(new WorkDay(csd, shiftID));
                    }
                } else {
                    if (sc.hasWork(csd)) {
                        sc.deleteAllWday(csd);
                    }
                }
                updateSpecificCalendar(shiftID);
                updateTextView();
            }
        } else {
            updateTextView();
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    protected void onResume() {
        super.onResume();
        sc = IO.readShiftCal(getFilesDir());
        updateCalendar();
        updateTextView();
        fabEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit));
        toEdit = false;
        fabShiftSelector.setVisibility(View.INVISIBLE);
        tvFabShiftSelector.setVisibility(View.INVISIBLE);
        int color = getResources().getColor(R.color.colorPrimary);
        settings = IO.readSettings(getFilesDir());
        if (settings.isAvailable(Settings.SET_COLOR)) {
            color = Integer.parseInt(settings.getSetting(Settings.SET_COLOR));
        }
        fabShiftSelector.setBackgroundTintList(ColorStateList.valueOf(color));
        fabShiftSelector.setBackgroundColor(color);
        tvFabShiftSelector.setText("S");
        shiftID = -1;
        popup.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        shiftID = shifts.get(i).getId();
        if (i != -2) {
            Shift s = shifts.get(i);
            shiftID = s.getId();
            fabShiftSelector.setBackgroundTintList(ColorStateList.valueOf(s.getColor()));
            fabShiftSelector.setBackgroundColor(s.getColor());
            tvFabShiftSelector.setText(s.getShort_name());
        } else {
            shiftID = -2;
            fabShiftSelector.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            fabShiftSelector.setBackgroundColor(Color.RED);
            tvFabShiftSelector.setText("D");
        }
        dialog.cancel();
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
            case R.id.iGoTo:
                Calendar endDate = Calendar.getInstance();
                endDate.add(Calendar.YEAR, 10);
                new SlideDatePickerDialog.Builder().setShowYear(true)
                        .setEndDate(endDate)
                        .build().show(getSupportFragmentManager(), "TAG");
        }

        return false;
    }

    @Override
    public void onDismiss(PopupMenu menu) {
        fl.setBackgroundColor(Color.TRANSPARENT);
        fl.setOnTouchListener(null);
    }

    @Override
    public void onPositiveClick(int day, int month, int year, Calendar calendar) {
        this.calendar.setCurrentDate(CalendarDay.from(year, month, day));
        this.calendar.setSelectedDate(CalendarDay.from(year, month, day));
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        CalendarDate min = new CalendarDate(date);
        min.addMonth(-5);
        CalendarDate max = new CalendarDate(date);
        max.addMonth(5);
        if (!day.inRange(min, max)) {
            day = new CalendarDate(date);
            calendar.setSelectedDate(date);
            updateCalendar();
        }
    }

}