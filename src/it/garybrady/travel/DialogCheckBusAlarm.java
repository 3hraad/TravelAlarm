package it.garybrady.travel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Gary on 04/04/14.
 */
public class DialogCheckBusAlarm extends Activity {
    NumberPicker busPicker, etaAlarm;
    String eta[]={"5 Mins","10 Mins","15 Mins"};
    String busNo[]={};
    String ref=null;
    List<String> busNumbers=new ArrayList<String>();
    String[] busArray;

    String iETA=null,bus=null;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_bus_alarm);
        etaAlarm=(NumberPicker)findViewById(R.id.npTime);
        etaAlarm.setMinValue(1);
        etaAlarm.setMaxValue(3);
        etaAlarm.setDisplayedValues(eta);
        busPicker=(NumberPicker)findViewById(R.id.npBusNoPick);


        Bundle b = getIntent().getExtras();
        ref = b.getString("refNum");
        if (isNetworkAvailable()) {
            new getbusNumbers().execute();
            Button setAlarm = (Button) findViewById(R.id.bSetBusChecker);
            setAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iETA= eta[etaAlarm.getValue()-1].toString();
                    bus=busArray[busPicker.getValue()-1].toString();
                    Intent data= new Intent();
                    data.putExtra("eta",iETA);
                    data.putExtra("bus",bus);
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
        } else{
            Toast.makeText(getApplicationContext(),"No network available",Toast.LENGTH_LONG).show();
        }




    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class getbusNumbers extends AsyncTask<Void, Integer, Void> {


        @Override
        protected void onPreExecute() {


        }

        @Override
        protected Void doInBackground(Void... params) {
            String temp = null;
            String result = "";
            InputStream isr = null;
            String refURL= "http://192.3.177.209/busMap/busAlarmNumbers.php?refNo="+ref;
            Log.e("CMON", refURL);



            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(refURL);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                isr = entity.getContent();
            }
            catch(Exception e){
                Log.e("log_tag", "Error in http connection " + e.toString());

            }
            //convert response to string
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(isr,"iso-8859-1"),8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                isr.close();

                result=sb.toString();
            }
            catch(Exception e){
                Log.e("log_tag", "Error  converting result "+e.toString());
            }

            //parse json data
            try {
                String s = "";
                JSONArray jArray = new JSONArray(result);

                for(int i=0; i<jArray.length();i++){
                    JSONObject json = jArray.getJSONObject(i);
                    busNumbers.add(json.getString("busNo"));
                }



            } catch (Exception e) {

                Log.e("log_tag", "Error Parsing Data " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Object oBus[]=busNumbers.toArray();
            busPicker.setMinValue(1);
            busPicker.setMaxValue(busNumbers.size());


            busArray = Arrays.copyOf(oBus, oBus.length, String[].class);


            busPicker.setDisplayedValues(busArray);
        }
    }
}