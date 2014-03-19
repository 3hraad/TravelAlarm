package it.garybrady.travel;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import it.garybrady.traveldata.constants;
import it.garybrady.traveldata.myDatabase;

import java.util.List;


/**
 * Created by Gary on 14/03/14.
 */
public class ViewGeofences extends Activity {
    myDatabase dba;
    int maxId;
    TextView max;
    ListView previousGeo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_geofence);
        max=(TextView)findViewById(R.id.tvMaxId);
        Button getMax=(Button)findViewById(R.id.bGetMaxId);
        getMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxId=getMaxId();
                max.setText(String.valueOf(maxId));
            }
        });
        previousGeo=(ListView)findViewById(R.id.lvGeofence);
        fillData();
        registerListClickCallback();
        //loadListView();
    }

    private void loadListView() {
        dba=new myDatabase(this);
        dba.open();

        List<String> titles = dba.getAllGeo();
        dba.close();

        // Creating adapter
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, titles);
        // Drop down layout style - list view with radio button
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        previousGeo.setAdapter(dataAdapter);
    }

    private void fillData() {
        dba=new myDatabase(this);
        dba.open();
        // Get all of the notes from the database and create the item list
        Cursor c = dba.fetchAllNotes();
        startManagingCursor(c);

        String[] from = new String[] { constants.G_TITLE };
        int[] to = new int[] { R.id.text1 };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.geofence_titles, c, from, to);
        previousGeo.setAdapter(notes);
    }

    private void registerListClickCallback() {
        previousGeo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long idInDB) {
                dba.open();
                Cursor cursor=dba.getRow(idInDB);
                if(cursor.moveToFirst()){
                    int idDB=cursor.getInt(0);
                    String title=cursor.getString(1);
                    String message="id: "+idDB+"  title: "+title;
                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private int getMaxId() {
        int temp;
        dba=new myDatabase(this);
        dba.open();
        temp=dba.getMaxGeoId();
        dba.close();
        return temp;
    }


}
