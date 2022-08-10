package de.nulide.shiftcal.settings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import de.nulide.shiftcal.R;
import de.nulide.shiftcal.logic.io.IO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.tools.ColorHelper;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView settingsList;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<String> settingsL;

    final String SET_THEME = "Theme";
    final String SET_DND = "DoNotDisturb";
    final String SET_ALARM = "ShiftAlarm";
    final String SET_SYNC = "Sync";
    final String SET_EXP_CAL = "Export Calendar";
    final String SET_IMP_CAL = "Import Calendar";
    final String SET_TPP = "Third-Party-Projects";
    final String SET_ABOUT = "About";

    final int SAVE_REQUEST_CODE = 355;
    final int READ_REQUEST_CODE = 356;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Settings settings  = IO.readSettings(getFilesDir());
        int color = ColorHelper.changeActivityColors(this, toolbar, settings);


        settingsList = findViewById(R.id.settingslist);
        settingsL = new ArrayList<>();
        listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                settingsL);
        settingsList.setAdapter(listAdapter);
        settingsList.setOnItemClickListener(this);
        settingsL.add(SET_THEME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            settingsL.add(SET_DND);
        }
        settingsL.add(SET_ALARM);
        settingsL.add(SET_SYNC);
        settingsL.add(SET_EXP_CAL);
        settingsL.add(SET_IMP_CAL);
        settingsL.add(SET_TPP);
        settingsL.add(SET_ABOUT);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = null;
        switch(settingsL.get(i)){

            case SET_THEME:
                intent = new Intent(this, ThemeActivity.class);
                startActivity(intent);
                break;

            case SET_DND:
                intent = new Intent(this, DNDActivity.class);
                startActivity(intent);
                break;

            case SET_ALARM:
                intent = new Intent(this, AlarmActivity.class);
                startActivity(intent);
                break;

            case SET_SYNC:
                intent = new Intent(this, CalendarSyncActivity.class);
                startActivity(intent);
                break;

            case SET_EXP_CAL:
                intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.putExtra(Intent.EXTRA_TITLE, IO.JSON_SC_FILE_NAME);
                intent.setType("*/*");
                startActivityForResult(intent, SAVE_REQUEST_CODE);
                break;


            case SET_IMP_CAL:
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
                break;

            case SET_TPP:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gitlab.com/Nulide/ShiftCal/-/blob/master/ThirdPartyProjects.md"));
                startActivity(intent);
                break;

            case SET_ABOUT:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    IO.importShiftCal(getFilesDir(), this, inputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        }else if(requestCode == SAVE_REQUEST_CODE && resultCode == Activity.RESULT_OK){

            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    ParcelFileDescriptor sco = this.getContentResolver().openFileDescriptor(uri, "w");
                    IO.exportShiftCal(getFilesDir(), new FileOutputStream(sco.getFileDescriptor()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
