package it.garybrady.travel;

import android.app.Activity;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import it.garybrady.traveldata.myDatabase;

/**
 * Created by Gary on 06/03/14.
 */
public class TriggeredGeofence extends Activity {
    MediaPlayer mp;
    int geoIDs[];
    String array[];
    String received;
    myDatabase dba;
    Button bStop;
    private SimpleGeofenceStore mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        mp = MediaPlayer.create(this, R.raw.audio);
        mp.start();
        bStop = (Button) findViewById(R.id.bStopAlarm);

        TextView whichGeo=(TextView)findViewById(R.id.tvTrig);
        Bundle bun = getIntent().getExtras();
//        geoIDs =  bun.getIntArray("id");
        received=bun.getString("id");
        String[] separated = received.split(",");
        whichGeo.setText("Entered Geofence:\n");
        for(int i=0;i<separated.length;i++){
            whichGeo.append(getTitle(Integer.parseInt(separated[i])));
            //whichGeo.append(separated[i]);
            removeGeo(Integer.parseInt(separated[i]));
            //mPrefs.clearGeofence(separated[i]);

        }
        bStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                if(bStop.getText()=="Back To Menu"){
                    finish();
                }else{
                    bStop.setText("Back To Menu");
                }
            }
        });



    }
    private void removeGeo(int x) {
        dba=new myDatabase(this);
        dba.open();
        dba.deactivateGeo(x);
        dba.close();
    }

    private String getTitle(int id){
        String title="";
        dba=new myDatabase(this);
        dba.open();
        Cursor cursor=dba.getActiveTitle(id);
        dba.close();
        if(cursor.moveToFirst()){
            title=cursor.getString(0);
            title+="\n";
        }

        return title;

    }
}
