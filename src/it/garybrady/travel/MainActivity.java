package it.garybrady.travel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
import android.widget.Button;
import android.widget.SlidingDrawer;
import android.widget.TextView;

public class MainActivity extends Activity {
	Button testDrawer;
	SlidingDrawer sd;
	WebView wb;
	TextView test;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		StrictMode.enableDefaults();
		//test = (TextView) findViewById(R.id.testView);
		Button myMapActivity = (Button) findViewById(R.id.bBusAlarm);
		myMapActivity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, MarkerMap.class);
				startActivity(i);
			}
		});
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



	
}




