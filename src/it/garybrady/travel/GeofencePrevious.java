package it.garybrady.travel;

import android.app.ListActivity;
import android.content.Intent;
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
public class GeofencePrevious extends ListActivity {
    myDatabase dba;
    View header;
    ListView previousGeo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_previous_geo);


        previousGeo=(ListView)findViewById(android.R.id.list);
        header = View.inflate(this,R.layout.list_view_header,null);

        previousGeo.addHeaderView(header);
        fillData();
        registerListClickCallback();

    }


    private void fillData() {
        dba=new myDatabase(this);
        dba.open();
        // Get all of the notes from the database and create the item list
        Cursor c = dba.previousGeo();
        dba.close();
        startManagingCursor(c);

        String[] from = new String[] { constants.G_TITLE };
        int[] to = new int[] { R.id.text1 };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.geofence_titles, c, from, to);
        previousGeo.setAdapter(notes);
        if (!c.moveToFirst()){
            Toast.makeText(getApplicationContext(), "No Previous Alarms Found", Toast.LENGTH_LONG).show();

        }
    }


    private void registerListClickCallback() {
        previousGeo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long idInDB) {
                dba.open();
                Cursor cursor=dba.getRowLocation(idInDB);
                dba.close();
                if(cursor.moveToFirst()){
                    int idDB=cursor.getInt(0);
                    String title=cursor.getString(1);
                    double lat=cursor.getDouble(2);
                    double lng=cursor.getDouble(3);

                    Bundle b =new Bundle();
                    b.putInt("id",idDB);
                    b.putString("title", title);
                    b.putDouble("lat", lat);
                    b.putDouble("lng", lng);
                    Intent i = new Intent(getApplicationContext(),MapLongGeofence.class);
                    i.putExtras(b);
                    cursor.close();
                    startActivity(i);

                    finish();

                }


            }
        });
    }



}
