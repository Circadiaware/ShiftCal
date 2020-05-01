package de.nulide.shiftcal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

import de.nulide.shiftcal.logic.io.IO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.logic.object.Shift;
import de.nulide.shiftcal.logic.object.ShiftCalendar;
import de.nulide.shiftcal.tools.Alarm;
import de.nulide.shiftcal.tools.ColorHelper;

public class ExecutedAlarmActivity extends AppCompatActivity implements View.OnClickListener {

    private Ringtone ringtone;

    private TextView tvClock;
    private TextView tvShiftN;
    private TextView tvShiftA;
    private FloatingActionButton btnEnd;

    private PowerManager powerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_executed_alarm);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int color = getResources().getColor(R.color.colorPrimary);
        Settings settings  = IO.readSettings(getFilesDir());
        if(settings.isAvailable(Settings.SET_COLOR)){
            color = Integer.parseInt(settings.getSetting(Settings.SET_COLOR));
        }
        ColorHelper.changeActivityColors(this, color);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = 1;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAGS_CHANGED);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        Alarm a = new Alarm(getFilesDir());
        a.setAlarm(this);
        Calendar cal = Calendar.getInstance();
        ShiftCalendar sc = IO.readShiftCal(getFilesDir());
        Bundle bundle = getIntent().getExtras();
        Shift s = sc.getShiftById(bundle.getInt(Alarm.EXT_SHIFT));
        tvClock = findViewById(R.id.EAClock);
        String clockTime = cal.getTime().getHours() + ":";
        if (String.valueOf(cal.getTime().getMinutes()).length() > 1) {
            clockTime += cal.getTime().getMinutes();
        } else {
            clockTime += "0" + cal.getTime().getMinutes();
        }
        tvClock.setText(clockTime);
        tvShiftN = findViewById(R.id.EAShift);
        tvShiftN.setText(s.getName());
        tvShiftN.setTextColor(s.getColor());
        tvShiftA = findViewById(R.id.EAShiftA);
        tvShiftA.setText(s.getShort_name());
        tvShiftA.setTextColor(s.getColor());
        btnEnd = findViewById(R.id.btnEndAlarm);
        btnEnd.setBackgroundTintList(ColorStateList.valueOf(color));
        btnEnd.setOnClickListener(this);

        Uri uri = Uri.parse(settings.getSetting(Settings.SET_ALARM_TONE));
        ringtone = RingtoneManager.getRingtone(this, uri);
        ringtone.play();
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean isScreenAwake = powerManager.isInteractive();
        if (isScreenAwake) {
            ringtone.stop();
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        ringtone.stop();
        finish();
    }
}
