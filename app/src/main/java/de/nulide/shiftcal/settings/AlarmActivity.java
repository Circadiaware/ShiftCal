package de.nulide.shiftcal.settings;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.nulide.shiftcal.R;
import de.nulide.shiftcal.logic.io.IO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.tools.Alarm;
import de.nulide.shiftcal.tools.ColorHelper;
import de.nulide.shiftcal.tools.PermissionHandler;

public class AlarmActivity extends AppCompatActivity implements OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener {

    private Switch switchAlarm;
    private EditText etMinutesAlarm;
    private Button btnTone;
    private Settings settings;
    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        settings  = IO.readSettings(getFilesDir());
        int color = ColorHelper.changeActivityColors(this, toolbar, settings);
        alarm = new Alarm(getFilesDir());

        switchAlarm = findViewById(R.id.switchAlarm);
        switchAlarm.setOnCheckedChangeListener(this);

        etMinutesAlarm = findViewById(R.id.editTextAlarmMinutes);

        btnTone = findViewById(R.id.btnAlarmSound);
        btnTone.setOnClickListener(this);

        if (settings.isAvailable(Settings.SET_ALARM_ON_OFF)) {
            if (new Boolean(settings.getSetting(Settings.SET_ALARM_ON_OFF))) {
                if (settings.isAvailable(Settings.SET_ALARM_MINUTES)) {
                    etMinutesAlarm.setText(settings.getSetting(Settings.SET_ALARM_MINUTES));
                }
                switchAlarm.setChecked(true);
                etMinutesAlarm.setEnabled(true);
                btnTone.setEnabled(true);
            }
        }
        if (settings.isAvailable(Settings.SET_ALARM_TONE)) {
            Ringtone tone = RingtoneManager.getRingtone(this, Uri.parse(settings.getSetting(Settings.SET_ALARM_TONE)));
            btnTone.setText(tone.getTitle(this));
        } else {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone tone = RingtoneManager.getRingtone(this, uri);
            settings.setSetting(Settings.SET_ALARM_TONE, uri.toString());
            IO.writeSettings(getFilesDir(), this, settings);
            btnTone.setText(tone.getTitle(this));
        }
        etMinutesAlarm.addTextChangedListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == btnTone) {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(settings.getSetting(Settings.SET_ALARM_TONE)));
            this.startActivityForResult(intent, 5);
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(!etMinutesAlarm.getText().toString().isEmpty()) {
            settings.setSetting(Settings.SET_ALARM_MINUTES, etMinutesAlarm.getText().toString());
            IO.writeSettings(getFilesDir(), this, settings);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode,resultCode,intent);
        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            settings.setSetting(Settings.SET_ALARM_TONE, uri.toString());
            IO.writeSettings(getFilesDir(), this, settings);
            Ringtone tone = RingtoneManager.getRingtone(this, uri);
            btnTone.setText(tone.getTitle(this));

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == switchAlarm) {
            if (isChecked) {
                PermissionHandler.RequestOverlayPermission(this);
                if(!PermissionHandler.CheckForOverlayPermission(this)){
                    return;
                }
                etMinutesAlarm.setEnabled(true);
                btnTone.setEnabled(true);
                settings.setSetting(Settings.SET_ALARM_ON_OFF, new Boolean(true).toString());
                settings.setSetting(Settings.SET_ALARM_MINUTES, etMinutesAlarm.getText().toString());
                IO.writeSettings(getFilesDir(), this, settings);
            } else {
                etMinutesAlarm.setEnabled(false);
                btnTone.setEnabled(false);
                settings.setSetting(Settings.SET_ALARM_ON_OFF, new Boolean(false).toString());
                IO.writeSettings(getFilesDir(), this, settings);
            }
        }
    }
}
