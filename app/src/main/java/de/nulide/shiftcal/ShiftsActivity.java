package de.nulide.shiftcal;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import de.nulide.shiftcal.logic.io.IO;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.logic.object.Shift;
import de.nulide.shiftcal.logic.object.ShiftCalendar;
import de.nulide.shiftcal.sync.SyncHandler;
import de.nulide.shiftcal.tools.ColorHelper;
import de.nulide.shiftcal.ui.ShiftAdapter;

public class ShiftsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    static ShiftCalendar sc;
    private ListView listViewShifts;
    private ArrayList<Shift> shifts;

    private FloatingActionButton fabAddShift;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Settings settings  = IO.readSettings(getFilesDir());
        int color = ColorHelper.changeActivityColors(this, toolbar, settings);


        fabAddShift = findViewById(R.id.fabAddShift);
        fabAddShift.setBackgroundTintList(ColorStateList.valueOf(color));
        fabAddShift.setOnClickListener(this);


        listViewShifts = findViewById(R.id.listViewShifts);
        registerForContextMenu(listViewShifts);
        listViewShifts.setOnItemClickListener(this);
        updateShifts();


    }

    public void updateShifts() {
        sc = IO.readShiftCal(getFilesDir());
        shifts = new ArrayList<>(sc.getShiftList());
        ShiftAdapter adapter = new ShiftAdapter(this, shifts);
        listViewShifts.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        Intent myIntent = new Intent(this, ShiftCreatorActivity.class);
        myIntent.putExtra("toedit", -1);
        startActivity(myIntent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent myIntent = new Intent(this, ShiftCreatorActivity.class);
        myIntent.putExtra("toedit", shifts.get(i).getId());
        startActivity(myIntent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Archieve");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        if (item.getTitle() == "Edit") {
            Intent myIntent = new Intent(this, ShiftCreatorActivity.class);
            myIntent.putExtra("toedit", shifts.get(index).getId());
            startActivity(myIntent);
        } else if (item.getTitle() == "Delete") {
            sc.deleteWorkDaysWithShift(shifts.get(index).getId());
            sc.deleteShift(shifts.get(index).getId());
            IO.writeShiftCal(getFilesDir(), this, sc);
            SyncHandler.sync(this);
            updateShifts();
        } else if (item.getTitle() == "Archieve") {
            Shift shift = shifts.get(index);
            shift.setArchieved();
            sc.setShift(shift.getId(), shift);
            IO.writeShiftCal(getFilesDir(), this, sc);
            updateShifts();
        }else{
            return false;
        }
        return true;


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateShifts();
    }

}
