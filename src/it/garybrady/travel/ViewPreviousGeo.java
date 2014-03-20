package it.garybrady.travel;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import it.garybrady.traveldata.constants;
import it.garybrady.traveldata.myDatabase;

import java.util.List;


/**
 * Created by Gary on 14/03/14.
 */
public class ViewPreviousGeo extends Activity {
    myDatabase dba;
    int maxId;
    TextView max;
    ListView previousGeo;
    float historicX = Float.NaN, historicY = Float.NaN;
    static final int DELTA = 50;
    enum Direction {LEFT, RIGHT;}

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

        previousGeo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        historicX = event.getX();
                        historicY = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        if (event.getX() - historicX < -DELTA)
                        {

                            Toast.makeText(getApplicationContext(),"slide recieved captain",Toast.LENGTH_LONG).show();
                            return true;
                        }
                        else if (event.getX() - historicX > DELTA)
                        {
                            Toast.makeText(getApplicationContext(),"slide recieved captain",Toast.LENGTH_LONG).show();
                            return true;
                        } break;
                    default: return false;
                }
                return false;
            }
        });
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
        Cursor c = dba.previousGeo();
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
                    Intent i = new Intent(ViewPreviousGeo.this,MapLongGeofence.class);
                    i.putExtras(b);
                    cursor.close();
                    startActivity(i);
                    finish();

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