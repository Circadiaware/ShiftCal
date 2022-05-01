package de.nulide.shiftcal;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import de.nulide.shiftcal.logic.io.IO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.logic.object.Shift;
import de.nulide.shiftcal.logic.object.ShiftCalendar;
import de.nulide.shiftcal.logic.object.ShiftTime;
import de.nulide.shiftcal.tools.ColorHelper;

public class ShiftCreatorActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    private ShiftCalendar sc;
    private int toEditShift = -1;
    private ShiftTime stStart;
    private ShiftTime stEnd;
    private boolean setStartTime = true;

    private FloatingActionButton fabDoneShift;
    private EditText etViewName;
    private EditText etViewSName;
    private Button btnStartTime;
    private Button btnEndTime;
    private Button btnCP;
    private Switch swAlarmForShift;
    private TimePickerDialog timePickerST;
    private TimePickerDialog timePickerET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_creator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int color = getResources().getColor(R.color.colorPrimary);
        Settings settings  = IO.readSettings(getFilesDir());
        if(settings.isAvailable(Settings.SET_COLOR)){
            color = Integer.parseInt(settings.getSetting(Settings.SET_COLOR));
        }
        ColorHelper.changeActivityColors(this, color);

        Bundle bundle = getIntent().getExtras();
        toEditShift = bundle.getInt("toedit");

        sc = IO.readShiftCal(getFilesDir());
        stStart = new ShiftTime(0, 0);
        stEnd = new ShiftTime(0,0);


        fabDoneShift = findViewById(R.id.fabDoneShift);
        fabDoneShift.setOnClickListener(this);
        fabDoneShift.setBackgroundTintList(ColorStateList.valueOf(color));
        etViewName = findViewById(R.id.scEditTextName);
        etViewSName = findViewById(R.id.scEditTextSName);
        btnStartTime = findViewById(R.id.btnStartTime);
        btnStartTime.setOnClickListener(this);
        btnEndTime = findViewById(R.id.btnEndTime);
        btnEndTime.setOnClickListener(this);
        btnCP = findViewById(R.id.colorPickerBtn);
        btnCP.setOnClickListener(this);
        swAlarmForShift = findViewById(R.id.swAlarmForShift);
        swAlarmForShift.setChecked(true);
        swAlarmForShift.setEnabled(false);
        if(settings.isAvailable(Settings.SET_ALARM_ON_OFF)){
            if(new Boolean(settings.getSetting(Settings.SET_ALARM_ON_OFF))){
                swAlarmForShift.setEnabled(true);
            }
        }

        updateButtonColors(color);

        if (toEditShift != -1) {
            etViewName.setText(sc.getShiftById(toEditShift).getName());
            etViewSName.setText(sc.getShiftById(toEditShift).getShort_name());
            stStart = sc.getShiftById(toEditShift).getStartTime();
            stEnd = sc.getShiftById(toEditShift).getEndTime();
            swAlarmForShift.setChecked(sc.getShiftById(toEditShift).isToAlarm());
            updateButtonColors(sc.getShiftById(toEditShift).getColor());
            updateTime();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == fabDoneShift) {
            String name = etViewName.getText().toString();
            String sname = etViewSName.getText().toString();

            if (!name.isEmpty() && !sname.isEmpty()) {
                Shift nS;
                if(swAlarmForShift.isEnabled()) {
                    nS = new Shift(name, sname, sc.getNextShiftId(), stStart, stEnd, ((ColorDrawable) btnCP.getBackground()).getColor(), swAlarmForShift.isChecked(), false);
                }else{
                    nS = new Shift(name, sname, sc.getNextShiftId(), stStart, stEnd, ((ColorDrawable) btnCP.getBackground()).getColor(), true, false);
                }
                if (toEditShift != -1) {
                    nS.setId(sc.getShiftById(toEditShift).getId());
                    sc.setShift(toEditShift, nS);
                } else {
                    sc.addShift(nS);
                }
                IO.writeShiftCal(getFilesDir(), this, sc);
                this.finish();
            } else {
                Snackbar.make(view, "Error: Not enough Information!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } else if (view == btnStartTime) {
            setStartTime = true;
            if (toEditShift != -1) {
                timePickerST = new TimePickerDialog(this, this, sc.getShiftById(toEditShift).getStartTime().getHour(), sc.getShiftById(toEditShift).getStartTime().getMinute(), true);
            } else {
                timePickerST = new TimePickerDialog(this, this, 12, 0, true);
            }
            timePickerST.show();
        } else if (view == btnEndTime) {
            setStartTime = false;
            if (toEditShift != -1) {
                timePickerET = new TimePickerDialog(this, this, sc.getShiftById(toEditShift).getEndTime().getHour(), sc.getShiftById(toEditShift).getEndTime().getMinute(), true);
            } else {
                timePickerET = new TimePickerDialog(this, this, 12, 0, true);
            }
            timePickerET.show();
        } else {
            ColorPickerDialogBuilder
                    .with(this)
                    .setTitle("Choose color")
                    .initialColor(((ColorDrawable)btnCP.getBackground()).getColor())
                    .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                    .density(12)
                    .setPositiveButton("ok", new ColorPickerClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors) {
                            updateButtonColors(lastSelectedColor);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .build()
                    .show();
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        if (setStartTime) {
            stStart = new ShiftTime(hour, minute);
        } else {
            stEnd = new ShiftTime(hour, minute);
        }
        updateTime();
    }

    public void updateTime(){
        btnStartTime.setText(stStart.toString());
        btnEndTime.setText(stEnd.toString());
    }

    public void updateButtonColors(int color){
        btnStartTime.setBackgroundColor(color);
        btnEndTime.setBackgroundColor(color);
        btnCP.setBackgroundColor(color);
        btnCP.setText(String.format("#%06X", (0xFFFFFF & color)));


        int[] rgb = { Color.red(color), Color.green(color), Color.blue(color) };
        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1]
                * rgb[1] * .691 + rgb[2] * rgb[2] * .068);

        if (brightness >= 200) {
            btnCP.setTextColor(Color.BLACK);
            btnStartTime.setTextColor(Color.BLACK);
            btnEndTime.setTextColor(Color.BLACK);
        }else{
            btnCP.setTextColor(Color.WHITE);
            btnStartTime.setTextColor(Color.WHITE);
            btnEndTime.setTextColor(Color.WHITE);
        }
    }
}
