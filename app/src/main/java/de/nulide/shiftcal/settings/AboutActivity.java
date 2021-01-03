package de.nulide.shiftcal.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import de.nulide.shiftcal.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSourceCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

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
