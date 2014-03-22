package it.garybrady.travel;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import it.garybrady.traveldata.constants;
import it.garybrady.traveldata.myDatabase;

/**
 * Created by Gary on 22/03/14.
 */
public class GeofenceCurrent extends ListActivity {
    myDatabase dba;

    ListView currentGeo;

    View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_current_geo);

        currentGeo=(ListView)findViewById(android.R.id.list);
        fillData();
        registerListClickCallback();


        //loadListView();
    }



    private void fillData() {
        dba=new myDatabase(this);
        dba.open();
        // Get all of the notes from the database and create the item list
        Cursor c = dba.fetchAllActive();
        dba.close();
        startManagingCursor(c);

        String[] from = new String[] { constants.G_TITLE };
        int[] to = new int[] { R.id.text1 };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.geofence_titles, c, from, to);
        currentGeo.setAdapter(notes);
    }

    private void registerListClickCallback() {
        currentGeo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long idInDB) {
                dba.open();
                Cursor cursor = dba.getRow(idInDB);
                dba.close();
                if (cursor.moveToFirst()) {
                    int idDB = cursor.getInt(0);
                    String title = cursor.getString(1);
                    int active = cursor.getInt(2);
                    String message = "id: " + idDB + "  title: " + title + "  active: " + active;
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }



}