package it.garybrady.travel;

import android.app.Activity;
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
public class ViewGeofences extends Activity {
    myDatabase dba;
    int maxId;
    TextView geo;
    ListView previousGeo;
    float historicX = Float.NaN, historicY = Float.NaN;
    static final int DELTA = 50;
    enum Direction {LEFT, RIGHT;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_geofence);

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

                            Toast.makeText(getApplicationContext(),"slide",Toast.LENGTH_LONG).show();
                            return true;
                        }
                        else if (event.getX() - historicX > DELTA)
                        {
                            Toast.makeText(getApplicationContext(),"slide",Toast.LENGTH_LONG).show();
                            return true;
                        } break;
                    default: return false;
                }
                return false;
            }
        });
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
        previousGeo.setAdapter(notes);
    }

    private void registerListClickCallback() {
        previousGeo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long idInDB) {
                dba.open();
                Cursor cursor=dba.getRow(idInDB);
                dba.close();
                if(cursor.moveToFirst()){
                    int idDB=cursor.getInt(0);
                    String title=cursor.getString(1);
                    int active=cursor.getInt(2);
                    String message="id: "+idDB+"  title: "+title+"  active: "+active;
                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                }
            }
        });
    }



}
