package de.nulide.shiftcal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.nulide.shiftcal.logic.io.IO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.logic.object.Shift;
import de.nulide.shiftcal.logic.object.ShiftCalendar;
import de.nulide.shiftcal.logic.object.WorkDay;
import de.nulide.shiftcal.settings.SettingsActivity;
import de.nulide.shiftcal.settings.ThemeActivity;
import de.nulide.shiftcal.sync.SyncHandler;
import de.nulide.shiftcal.tools.ColorHelper;
import de.nulide.shiftcal.tools.PermissionHandler;
import de.nulide.shiftcal.ui.DarkModeDecorator;
import de.nulide.shiftcal.ui.ShiftAdapter;
import de.nulide.shiftcal.ui.ShiftDayFormatter;
import de.nulide.shiftcal.ui.ShiftDayViewDecorator;
import de.nulide.shiftcal.ui.TodayDayViewDecorator;

public class CalendarActivity extends AppCompatActivity implements View.OnClickListener, OnDateSelectedListener, AdapterView.OnItemClickListener, View.OnTouchListener, PopupMenu.OnMenuItemClickListener, PopupMenu.OnDismissListener, SlideDatePickerDialogCallback {

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

    private static FloatingActionButton fabShiftSelector;
    private static TextView tvFabShiftSelector;
    private static FloatingActionButton fabEdit;
    private static boolean toEdit = false;
    private int shiftID = -1;


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
        if(settings.isAvailable(Settings.SET_START_OF_WEEK)) {
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
        tvName = findViewById(R.id.cTextViewName);
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        tvST = findViewById(R.id.cTextViewST);
        tvET = findViewById(R.id.cTextViewET);
        tvWN = findViewById(R.id.cTextViewWN);
        fl = findViewById(R.id.CalendarTopLayer);

        sc = IO.readShiftCal(getFilesDir());
        if ((PermissionHandler.checkCalendar(this) && new Boolean(settings.getSetting(Settings.SET_SYNC)))) {
            sc.setCr(getContentResolver());
        }
        updateCalendar();
        updateTextView();
    }

    public void updateCalendar() {
        calendar.removeDecorators();
        calendar.addDecorator(new DarkModeDecorator(this));
        for (int i = 0; i < sc.getShiftsSize(); i++) {
            ShiftDayViewDecorator decorator = new ShiftDayViewDecorator(sc.getShiftByIndex(i), sc);
            calendar.addDecorator(decorator);
        }
        calendar.addDecorator(new TodayDayViewDecorator());
        shiftFormatter = new ShiftDayFormatter(sc);
        calendar.setDayFormatter(shiftFormatter);
    }

    public void updateSpecificCalendar(int shiftIndex){
        if(shiftIndex != -1) {
            Shift shiftToUpdate = sc.getShiftByIndex(shiftIndex);
            calendar.removeDecorator(shiftToUpdate.getDecorator());
            ShiftDayViewDecorator decorator = new ShiftDayViewDecorator(shiftToUpdate, sc);
            calendar.addDecorator(decorator);
            shiftFormatter = new ShiftDayFormatter(sc);
            calendar.setDayFormatter(shiftFormatter);
        }
    }

    public void updateTextView() {
        CalendarDay selectedDate = calendar.getSelectedDate();
        Shift sel = sc.getShiftByDate(selectedDate);
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
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(selectedDate.getYear(), selectedDate.getMonth()-1, selectedDate.getDay());
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
        } else if(view == fabShiftSelector) {
            if(toEdit) {
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
            }
        }else if (view == btnPopup) {
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


        int shiftIndexToUpdate = -1;
        if (toEdit) {
            if(shiftID != -1) {
                if (shiftID < sc.getShiftsSize()) {
                    if (!sc.hasWork(calendar.getSelectedDate())) {
                        sc.addWday(new WorkDay(calendar.getSelectedDate(), sc.getShiftByIndex(shiftID).getId()));
                    } else {
                        sc.deleteWday(calendar.getSelectedDate());
                        sc.addWday(new WorkDay(calendar.getSelectedDate(), sc.getShiftByIndex(shiftID).getId()));
                    }
                    shiftIndexToUpdate = shiftID;
                } else {
                    if (sc.hasWork(calendar.getSelectedDate())) {
                        shiftIndexToUpdate = sc.getShiftIndexByDate(date);
                        sc.deleteWday(calendar.getSelectedDate());
                    }
                }
                updateSpecificCalendar(shiftIndexToUpdate);
                updateTextView();
            }
            } else{
                updateTextView();
            }
        }


    @SuppressLint("RestrictedApi")
    @Override
    protected void onResume() {
        super.onResume();
        sc = IO.readShiftCal(getFilesDir());
        if ((PermissionHandler.checkCalendar(this) && new Boolean(settings.getSetting(Settings.SET_SYNC)))) {
            sc.setCr(getContentResolver());
        }
        updateCalendar();
        updateTextView();
        fabEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit));
        toEdit = false;
        fabShiftSelector.setVisibility(View.INVISIBLE);
        tvFabShiftSelector.setVisibility(View.INVISIBLE);
        int color = getResources().getColor(R.color.colorPrimary);
        settings  = IO.readSettings(getFilesDir());
        if(settings.isAvailable(Settings.SET_COLOR)){
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
        shiftID = i;
        if (i < sc.getShiftsSize()) {
            Shift s = sc.getShiftByIndex(i);
            fabShiftSelector.setBackgroundTintList(ColorStateList.valueOf(s.getColor()));
            fabShiftSelector.setBackgroundColor(s.getColor());
            tvFabShiftSelector.setText(s.getShort_name());
        } else {
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
}
