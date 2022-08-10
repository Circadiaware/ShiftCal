package de.nulide.shiftcal.settings;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.nulide.shiftcal.R;
import de.nulide.shiftcal.logic.io.IO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.tools.Alarm;
import de.nulide.shiftcal.tools.ColorHelper;

public class DNDActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Settings settings;
    private Switch swDND;
    private Alarm alarm;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d_n_d);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        settings  = IO.readSettings(getFilesDir());
        int color = ColorHelper.changeActivityColors(this, toolbar, settings);

        alarm = new Alarm(getFilesDir());

        swDND = findViewById(R.id.swDND);
        if(settings.isAvailable(Settings.SET_DND)){
            if(new Boolean(settings.getSetting(Settings.SET_DND))){
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (mNotificationManager.isNotificationPolicyAccessGranted()) {
                    swDND.setChecked(true);
                }else{
                    swDND.setChecked(false);
                    settings.setSetting(Settings.SET_DND, new Boolean(false).toString());
                    IO.writeSettings(getFilesDir(), this, settings);
                }
            }
        }
        swDND.setOnCheckedChangeListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume(){
        super.onResume();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager.isNotificationPolicyAccessGranted() && new Boolean(settings.getSetting(Settings.SET_DND))) {
            swDND.setChecked(true);
            alarm.setAlarm(this);
        }else{
            swDND.setChecked(false);
            settings.setSetting(Settings.SET_DND, new Boolean(false).toString());
            IO.writeSettings(getFilesDir(), this, settings);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == swDND) {
            if (isChecked) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(intent);
                }else{
                    settings.setSetting(Settings.SET_DND, new Boolean(isChecked).toString());
                    IO.writeSettings(getFilesDir(), this, settings);
                }
            }
        }
    }
}
