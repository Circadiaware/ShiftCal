package de.nulide.shiftcal.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.nulide.shiftcal.R;
import de.nulide.shiftcal.logic.io.IO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.tools.ColorHelper;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSourceCode;
    private Settings settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        settings  = IO.readSettings(getFilesDir());
        int color = ColorHelper.changeActivityColors(this, toolbar, settings);

        buttonSourceCode = findViewById(R.id.buttonSourceCode);
        buttonSourceCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonSourceCode){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gitlab.com/Nulide/ShiftCal"));
            startActivity(intent);
        }
    }
}
