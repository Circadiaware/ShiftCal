package de.nulide.shiftcal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import de.nulide.shiftcal.logic.io.SettingsIO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.tools.ColorHelper;

public class ThemeActivity extends AppCompatActivity implements View.OnClickListener, ColorPickerClickListener, AdapterView.OnItemSelectedListener {

    public final static int DARK_MODE_OFF = 0;
    public final static int DARK_MODE_ON = 1;
    public final static int DARK_MODE_AUTO = 2;

    private Spinner sDarkMode;
    private Button btnAppColor;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int color = getResources().getColor(R.color.colorPrimary);
        settings  = SettingsIO.readSettings(getFilesDir());
        if(settings.isAvailable(Settings.SET_COLOR)){
            color = Integer.parseInt(settings.getSetting(Settings.SET_COLOR));
        }

        sDarkMode = findViewById(R.id.sDarkMode);
        ArrayAdapter<String> adapter;
        List<String> list;
        list = new ArrayList<String>();
        list.add("Off");
        list.add("On");
        list.add("System");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sDarkMode.setAdapter(adapter);
        if (!settings.isAvailable(Settings.SET_DARK_MODE)) {
            settings.setSetting(Settings.SET_DARK_MODE, String.valueOf(DARK_MODE_OFF));
        }
        sDarkMode.setSelection(Integer.parseInt(settings.getSetting(Settings.SET_DARK_MODE)));
        sDarkMode.setOnItemSelectedListener(this);

        btnAppColor = findViewById(R.id.btnAppColor);
        btnAppColor.setOnClickListener(this);

        updateColors(color);
    }

    @Override
    public void onClick(View v) {
        if(v == btnAppColor){
            ColorPickerDialogBuilder
                    .with(this)
                    .setTitle("Choose color")
                    .initialColor(((ColorDrawable)btnAppColor.getBackground()).getColor())
                    .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                    .density(12)
                    .setPositiveButton("ok",this)
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
    public void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors) {
        settings.setSetting(Settings.SET_COLOR, String.valueOf(lastSelectedColor));
        SettingsIO.writeSettings(getFilesDir(), this, settings);
        updateColors(lastSelectedColor);
        finish();
        Intent home = new Intent(this, CalendarActivity.class);
        startActivity(home);
    }

    public void updateColors(int color){
        btnAppColor.setBackgroundColor(color);
        btnAppColor.setText(String.format("#%06X", (0xFFFFFF & color)));
        int[] rgb = { Color.red(color), Color.green(color), Color.blue(color) };
        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1]
                * rgb[1] * .691 + rgb[2] * rgb[2] * .068);

        if (brightness >= 200) {
            btnAppColor.setTextColor(Color.BLACK);
        }else{
            btnAppColor.setTextColor(Color.WHITE);
        }
        ColorHelper.changeActivityColors(this, color);
    }

    public static void setDarkMode(int i){
        switch(i){
            case DARK_MODE_ON:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case DARK_MODE_OFF:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case DARK_MODE_AUTO:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent == sDarkMode){
            if(settings.isAvailable(Settings.SET_DARK_MODE)&&
                    Integer.parseInt(settings.getSetting(Settings.SET_DARK_MODE)) == position){
            }else {
                setDarkMode(position);
                settings.setSetting(Settings.SET_DARK_MODE, String.valueOf(position));
                SettingsIO.writeSettings(getFilesDir(), this, settings);
                finish();
                Intent home = new Intent(this, CalendarActivity.class);
                startActivity(home);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
