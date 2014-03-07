package it.garybrady.travel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.widget.*;
import it.garybrady.traveldata.myDatabase;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import it.garybrady.travel.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

public class MyActivity extends Activity {
    Button testDrawer;
    SlidingDrawer sd;
    WebView wb;
    TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.enableDefaults();
        test = (TextView) findViewById(R.id.testView);
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

        Button gcm = (Button) findViewById(R.id.bGCMtest);
        gcm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gps = new Intent(MyActivity.this, GcmActivity.class);
                startActivity(gps);
            }
        });

        Button gcmReg = (Button) findViewById(R.id.bGCMreg);
        gcmReg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gps = new Intent(MyActivity.this, RegisterActivity.class);
                startActivity(gps);
            }
        });

        final Button subAdd = (Button) findViewById(R.id.bSubmitAddress);
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
    }

    private void addAddressToDB(String message) {
        myDatabase dba=new myDatabase(this);
        dba.open();
        dba.insertAddress(message);
        dba.close();
    }



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









