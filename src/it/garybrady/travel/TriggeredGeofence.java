package it.garybrady.travel;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Gary on 06/03/14.
 */
public class TriggeredGeofence extends Activity {
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        mp = MediaPlayer.create(this, R.raw.audio);
        mp.start();
        Button b = (Button) findViewById(R.id.bStopAlarm);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
            }
        });

    }
}
