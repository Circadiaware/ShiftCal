package de.nulide.shiftcal.settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import de.nulide.shiftcal.R;
import de.nulide.shiftcal.logic.io.IO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.sync.CalendarController;
import de.nulide.shiftcal.sync.EventController;
import de.nulide.shiftcal.sync.SyncHandler;
import de.nulide.shiftcal.tools.ColorHelper;
import de.nulide.shiftcal.tools.PermissionHandler;

public class CalendarSyncActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Settings settings;
    private Switch swCalendarSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_sync);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int color = getResources().getColor(R.color.colorPrimary);
        settings  = IO.readSettings(getFilesDir());
        if(settings.isAvailable(Settings.SET_COLOR)){
            color = Integer.parseInt(settings.getSetting(Settings.SET_COLOR));
        }
        ColorHelper.changeActivityColors(this, color);

        swCalendarSync = findViewById(R.id.swCalendarSync);
        if(settings.isAvailable(Settings.SET_SYNC)){
            if(new Boolean(settings.getSetting(Settings.SET_SYNC))){

            }
        }
        swCalendarSync.setOnCheckedChangeListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume(){
        super.onResume();
        if(settings.isAvailable(Settings.SET_SYNC)) {
            if ((PermissionHandler.checkCalendar(this) && new Boolean(settings.getSetting(Settings.SET_SYNC)))) {
                swCalendarSync.setChecked(true);
            } else {
                swCalendarSync.setChecked(false);
                settings.setSetting(Settings.SET_SYNC, new Boolean(false).toString());
                IO.writeSettings(getFilesDir(), this, settings);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == swCalendarSync) {
            settings.setSetting(Settings.SET_SYNC, new Boolean(isChecked).toString());
            IO.writeSettings(getFilesDir(), this, settings);
            if (isChecked) {
                if (!PermissionHandler.checkCalendar(this)) {
                    PermissionHandler.requestCalendar(this);
                    swCalendarSync.setChecked(false);
                }else{
                    SyncHandler.sync(this);
                }
            }else{
                CalendarController.deleteCalendar(getContentResolver());
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}
