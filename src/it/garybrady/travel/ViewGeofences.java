package it.garybrady.travel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import it.garybrady.traveldata.myDatabase;

/**
 * Created by Gary on 14/03/14.
 */
public class ViewGeofences extends Activity {
    myDatabase dba;
    int maxId;
    TextView max;

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
