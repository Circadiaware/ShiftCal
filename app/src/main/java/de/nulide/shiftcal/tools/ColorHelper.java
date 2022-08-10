package de.nulide.shiftcal.tools;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import de.nulide.shiftcal.R;
import de.nulide.shiftcal.logic.object.Settings;

public class ColorHelper {

    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    public static int brightenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 1.2f;
        return Color.HSVToColor(hsv);
    }

    public static int changeActivityColors(AppCompatActivity c, Toolbar t, Settings settings){
        Boolean useMaterialYou = new Boolean(settings.getSetting(Settings.SET_MATERIALYOUCOLOR));
        int color = c.getResources().getColor(R.color.colorPrimary);
        if (useMaterialYou) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                color = c.getWindow().getStatusBarColor();
                t.setBackgroundColor(color);
            }
        }else{
            if (settings.isAvailable(Settings.SET_COLOR)) {
                color = Integer.parseInt(settings.getSetting(Settings.SET_COLOR));
            }
            c.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
            c.getSupportActionBar().setDisplayShowTitleEnabled(false);
            c.getSupportActionBar().setDisplayShowTitleEnabled(true);
            Window window = c.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(darkenColor(color));
        }
        return color;
    }
}
