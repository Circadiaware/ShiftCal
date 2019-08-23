package de.nulide.shiftcal;

import android.content.Intent;
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

import de.nulide.shiftcal.logic.io.CalendarIO;
import de.nulide.shiftcal.logic.object.Shift;
import de.nulide.shiftcal.logic.object.ShiftCalendar;
import de.nulide.shiftcal.ui.ShiftAdapter;

public class ShiftsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    static ShiftCalendar sc;
    private ListView listViewShifts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

        listViewShifts = findViewById(R.id.listViewShifts);
        registerForContextMenu(listViewShifts);
        listViewShifts.setOnItemClickListener(this);
        updateShifts();


    }

    public void updateShifts() {
        sc = CalendarIO.readShiftCal(getFilesDir());
        ShiftAdapter adapter = new ShiftAdapter(this, new ArrayList<Shift>(sc.getShifts()));
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
        myIntent.putExtra("toedit", sc.getShifts().get(i).getId());
        startActivity(myIntent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int index=info.position;
        if(item.getTitle()=="Edit")
        {
            Intent myIntent = new Intent(this, ShiftCreatorActivity.class);
            myIntent.putExtra("toedit", sc.getShifts().get(index).getId());
            startActivity(myIntent);
        }
        else if(item.getTitle()=="Delete")
        {

        }else {
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
