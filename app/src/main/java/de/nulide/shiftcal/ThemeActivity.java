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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.appbar.AppBarLayout;

import de.nulide.shiftcal.logic.io.SettingsIO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.tools.ColorHelper;

public class ThemeActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, ColorPickerClickListener {

    private Switch swDarkMode;
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

        swDarkMode = findViewById(R.id.swDarkMode);
        if (settings.isAvailable(Settings.SET_DARK_MODE)) {
            swDarkMode.setChecked(new Boolean(settings.getSetting(Settings.SET_DARK_MODE)));
        }
        swDarkMode.setOnCheckedChangeListener(this);

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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == swDarkMode){
            View view = getWindow().getDecorView();
            if(isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            settings.setSetting(Settings.SET_DARK_MODE, String.valueOf(isChecked));
            SettingsIO.writeSettings(getFilesDir(), this, settings);
            finish();
            Intent home = new Intent(this, CalendarActivity.class);
            startActivity(home);
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
        //getSupportActionBar().setB;
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
}
