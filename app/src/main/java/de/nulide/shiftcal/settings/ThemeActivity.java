package de.nulide.shiftcal.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import de.nulide.shiftcal.CalendarActivity;
import de.nulide.shiftcal.R;
import de.nulide.shiftcal.logic.io.IO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.tools.ColorHelper;

public class ThemeActivity extends AppCompatActivity implements View.OnClickListener, ColorPickerClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    public final static int DARK_MODE_OFF = 0;
    public final static int DARK_MODE_ON = 1;

    private Switch swMaterialYou;
    private Spinner sDarkMode;
    private Spinner sFirstDayOfWeek;
    private Button btnAppColor;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        settings  = IO.readSettings(getFilesDir());
        int color = ColorHelper.changeActivityColors(this, toolbar, settings);


        swMaterialYou = findViewById(R.id.swMaterialColor);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            swMaterialYou.setEnabled(false);
        }else{
            if(settings.isAvailable(Settings.SET_MATERIALYOUCOLOR)){
                swMaterialYou.setChecked(Boolean.getBoolean(settings.getSetting(Settings.SET_MATERIALYOUCOLOR)));
            }
            swMaterialYou.setOnCheckedChangeListener(this);
        }


        sDarkMode = findViewById(R.id.sDarkMode);
        ArrayAdapter<String> adapterDarkMode;
        List<String> listDarkMode;
        listDarkMode = new ArrayList<String>();
        listDarkMode.add("Off");
        listDarkMode.add("On");
        listDarkMode.add("System");
        adapterDarkMode = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, listDarkMode);
        adapterDarkMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sDarkMode.setAdapter(adapterDarkMode);
        if (!settings.isAvailable(Settings.SET_DARK_MODE)) {
            settings.setSetting(Settings.SET_DARK_MODE, String.valueOf(DARK_MODE_OFF));
        }
        sDarkMode.setSelection(Integer.parseInt(settings.getSetting(Settings.SET_DARK_MODE)));
        sDarkMode.setOnItemSelectedListener(this);

        btnAppColor = findViewById(R.id.btnAppColor);
        btnAppColor.setOnClickListener(this);

        sFirstDayOfWeek = findViewById(R.id.sFirstDayOfWeek);
        ArrayAdapter<String> adapterFDoW;
        List<String> listFDoW;
        listFDoW = new ArrayList<String>();
        listFDoW.add("Sunday");
        listFDoW.add("Monday");
        listFDoW.add("Tuesday");
        listFDoW.add("Wednesday");
        listFDoW.add("Thursday");
        listFDoW.add("Friday");
        listFDoW.add("Saturday");
        adapterFDoW = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, listFDoW);
        adapterFDoW.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sFirstDayOfWeek.setAdapter(adapterFDoW);
        if (!settings.isAvailable(Settings.SET_START_OF_WEEK)) {
            settings.setSetting(Settings.SET_START_OF_WEEK, String.valueOf(1));
        }
        sFirstDayOfWeek.setSelection(Integer.parseInt(settings.getSetting(Settings.SET_START_OF_WEEK)));
        sFirstDayOfWeek.setOnItemSelectedListener(this);

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
        IO.writeSettings(getFilesDir(), this, settings);
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
    }

    public static void setDarkMode(int i){
        switch(i){
            case DARK_MODE_ON:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case DARK_MODE_OFF:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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
                IO.writeSettings(getFilesDir(), this, settings);
                finish();
                Intent home = new Intent(this, CalendarActivity.class);
                startActivity(home);
            }
        }else if(parent == sFirstDayOfWeek){
            if(settings.isAvailable(Settings.SET_START_OF_WEEK)&&
                    Integer.parseInt(settings.getSetting(Settings.SET_START_OF_WEEK)) == position){

            }else {
                settings.setSetting(Settings.SET_START_OF_WEEK, String.valueOf(position));
                IO.writeSettings(getFilesDir(), this, settings);
                finish();
                Intent home = new Intent(this, CalendarActivity.class);
                startActivity(home);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        settings.setSetting(Settings.SET_MATERIALYOUCOLOR, ((Boolean)b).toString());
        IO.writeSettings(getFilesDir(), this, settings);
        finish();
        Intent home = new Intent(this, CalendarActivity.class);
        startActivity(home);
    }
}
