package it.garybrady.travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import it.garybrady.traveldata.myDatabase;

public class MyActivity extends Activity {

    //TextView test;
    myDatabase dba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.enableDefaults();
        setUpButtons();
        //test = (TextView) findViewById(R.id.testView);
        Bundle bun = getIntent().getExtras();
//        geoIDs =  bun.getIntArray("id");

        if(bun!=null){
            String alarm=bun.getString("id",null);
            Bundle b = new Bundle();
            b.putString("id",alarm);
            Intent i = new Intent(this, TriggeredGeofence.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtras(b);
            startActivity(i);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bun = getIntent().getExtras();
        if(bun!=null){
            String alarm=bun.getString("id",null);
            Bundle b = new Bundle();
            b.putString("id",alarm);
            Intent i = new Intent(this, TriggeredGeofence.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtras(b);
            startActivity(i);

        }
    }

    private void addAddressToDB(String message) {
        dba=new myDatabase(this);
        dba.open();
        dba.insertAddress(message);
        dba.close();
    }

    public void setUpButtons(){
        Button myMapActivity = (Button) findViewById(R.id.bBusAlarm);

        myMapActivity.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyActivity.this, BusViewPicker.class);
                startActivity(i);
            }
        });

        Button sampleGeofence = (Button) findViewById(R.id.bGPSAlarm);
        sampleGeofence.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gps = new Intent(MyActivity.this, MapLongGeofence.class);
                startActivity(gps);
            }
        });

        Button bestBus=(Button)findViewById(R.id.bBestBus);
        bestBus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyActivity.this,BestBusViewPicker.class);
                startActivity(i);
            }
        });

        /*Button viewGeo=(Button)findViewById(R.id.bViewGeofence);
        viewGeo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyActivity.this,ViewGeofences.class);
                startActivity(i);
            }
        });

        Button prevGeo=(Button)findViewById(R.id.bViewprevGeo);
        prevGeo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyActivity.this,ViewPreviousGeo.class);
                startActivity(i);
            }
        });*/

        Button allGeo=(Button)findViewById(R.id.bViewAllGeo);
        allGeo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyActivity.this,ViewBothGeofences.class);
                startActivity(i);
            }
        });

        /*Button gcm = (Button) findViewById(R.id.bGCMtest);
        gcm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gps = new Intent(MyActivity.this, GcmActivity.class);
                startActivity(gps);
            }
        });*/

        /*Button gcmReg = (Button) findViewById(R.id.bGCMreg);
        gcmReg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gps = new Intent(MyActivity.this, RegisterActivity.class);
                startActivity(gps);
            }
        });*/

        /*final Button subAdd = (Button) findViewById(R.id.bSubmitAddress);
        subAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et= (EditText)findViewById(R.id.etDumbAddress);
                String tempAddress = et.getText().toString();
                addAddressToDB(tempAddress);
                //subAdd.setText(tempAddress);
                Toast.makeText(getApplicationContext(),"Added to DB",Toast.LENGTH_LONG).show();
            }
        });

        Button gcmDummy = (Button) findViewById(R.id.bViewAdd);
        gcmDummy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gps = new Intent(MyActivity.this, GCMmap.class);
                startActivity(gps);
            }
        });
             */
    }
    /*@Override
    protected void onPause() {
        super.onPause();
        dba.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dba.close();
    }*/
    //sd = (SlidingDrawer) findViewById(R.id.slidingDrawer1);
		/*wb = (WebView) findViewById(R.id.webView1);
		wb.loadUrl("http://www.rtpi.ie/Popup_Content/WebDisplay/WebDisplay.aspx?stopRef=241831");

		testDrawer = (Button) findViewById(R.id.bGPSAlarm);
		testDrawer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sd.toggle();

			}
		});*/


}









