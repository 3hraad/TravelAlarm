package it.garybrady.travel;
 /*
 * Activity that is called from reciveTransitionIntentService
 * */
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import it.garybrady.traveldata.myDatabase;

import java.util.Collections;
import java.util.List;

/**
 * Created by Gary on 06/03/14.
 */
public class TriggeredGeofence extends Activity {
    MediaPlayer mp;
    Vibrator vibrator;
    int geoIDs[];
    String array[];
    String received;
    myDatabase dba;
    Button bStop;
    int realAlarms=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        bStop = (Button) findViewById(R.id.bStopAlarm);

        TextView whichGeo=(TextView)findViewById(R.id.tvTrig);
        Bundle bun = getIntent().getExtras();
//        geoIDs =  bun.getIntArray("id");
        received=bun.getString("id");
        String[] separated = received.split(",");
        whichGeo.setText("Entered Geofence:\n");
        for(int i=0;i<separated.length;i++){
            whichGeo.append(getTitle(Integer.parseInt(separated[i])));
            if(getTitle(Integer.parseInt(separated[i])).equals("")){
            }else{
                removeGeo(Integer.parseInt(separated[i]));
                realAlarms++;
            }

            //whichGeo.append(separated[i]);

            //mPrefs.clearGeofence(separated[i]);

        }
        if (realAlarms>0){
            mp = MediaPlayer.create(this, R.raw.ring);
            mp.setLooping(true);
            mp.start();

            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(new long[] { 500, 500 },0);


            bStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                vibrator.cancel();
                if(bStop.getText()=="Back To Menu"){
                    Intent i = new Intent(TriggeredGeofence.this,MyActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    bStop.setText("Back To Menu");
                }
            }
        });



        }else{
            finish();
        }
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
