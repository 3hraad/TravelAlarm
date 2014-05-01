package it.garybrady.travel;
 /*
 * Called when user taps widget
 * Gets info from shared prefrences
 * Calls server for info
 * */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
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

import static android.support.v4.app.ActivityCompat.startActivity;


public class MyWidgetIntentReceiver extends BroadcastReceiver {

    private static int clickCount = 0;
    Context context;
    String ref="NothinHereBrah";
    String busNo[]=new String[2],destination[]=new String[2],eta[]=new String[2];
    RemoteViews remoteViews;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("it.garybrady.travel.action.WIDGET_UPDATE")){
            updateWidgetPictureAndButtonListener(context);
        }
    }

    private void updateWidgetPictureAndButtonListener(Context c) {
        context=c;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        ConnectionDetector cd = new ConnectionDetector(c.getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {

            clearRemoteView();
            remoteViews.setTextViewText(R.id.tvBusDest,"No Internet Connection");
            remoteViews.setOnClickPendingIntent(R.id.widget_button, MyWidgetProvider.buildButtonPendingIntent(context));
            MyWidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);


        }else{
            SharedPreferences prefs = context.getSharedPreferences("busInfo",Context.MODE_PRIVATE);
            ref= prefs.getString("busRef","nada");
            Log.e("CMON", "from prefs -- "+ref);
            if (ref.equals("nada")){
                remoteViews.setTextViewText(R.id.tvBusDest,"Please select a bus");
                remoteViews.setTextViewText(R.id.tvBusDest1,"within Travel Alarm");
                Log.e("CMON", "Tried to tell em to pick");
                remoteViews.setOnClickPendingIntent(R.id.widget_button, MyWidgetProvider.buildButtonPendingIntent(context));
                MyWidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);


            }else{
                new loadBusTimeInfo().execute();
            }

        }

        /*//remoteViews.setImageViewResource(R.id.widget_image, getImageToSet());
        remoteViews.setTextViewText(R.id.tvBusNo,String.valueOf(clickCount));
        clickCount++;

        //REMEMBER TO ALWAYS REFRESH YOUR BUTTON CLICK LISTENERS!!!
        remoteViews.setOnClickPendingIntent(R.id.widget_button, MyWidgetProvider.buildButtonPendingIntent(context));

        MyWidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);*/

    }

    private void clearRemoteView() {
        remoteViews.setTextViewText(R.id.tvBusDest,"");
        remoteViews.setTextViewText(R.id.tvLastUpdated,"");
        remoteViews.setTextViewText(R.id.tvBusNo,"");
        remoteViews.setTextViewText(R.id.tvBusETA,"");
        remoteViews.setTextViewText(R.id.tvBusNo1,"");
        remoteViews.setTextViewText(R.id.tvBusDest1,"");
        remoteViews.setTextViewText(R.id.tvBusETA1,"");
    }


    private class loadBusTimeInfo extends AsyncTask<Void, Integer, Void> {

        String webStopRef="http://192.3.177.209/liveInfo.php?RefNo=";
        String refURL;
        public loadBusTimeInfo() {
            super();
            //ref = stopReference;
        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected Void doInBackground(Void... params) {
            String temp = null;
            String result = "";
            InputStream isr = null;

            StringBuilder urlBuild = new StringBuilder();
            urlBuild.append(webStopRef);
            urlBuild.append(ref);
            refURL = urlBuild.toString();



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

                for(int i=0; i<2;i++){
                    JSONObject json = jArray.getJSONObject(i);
                    busNo[i]=json.getString("busNo");
                    destination[i]=json.getString("dest");
                    eta[i] =json.getString("time");
                }
            } catch (Exception e) {

                Log.e("log_tag", "Error Parsing Data "+e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            //RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            //remoteViews.setImageViewResource(R.id.widget_image, getImageToSet());
            String date = "Last Updated :" + (DateFormat.format("dd-MM-yyyy hh:mm", new java.util.Date()).toString());

            if (busNo!=null){
                remoteViews.setTextViewText(R.id.tvLastUpdated, date);
                remoteViews.setTextViewText(R.id.tvBusNo,busNo[0]);
                remoteViews.setTextViewText(R.id.tvBusDest,destination[0]);
                remoteViews.setTextViewText(R.id.tvBusETA,eta[0]);
                remoteViews.setTextViewText(R.id.tvBusNo1,busNo[1]);
                remoteViews.setTextViewText(R.id.tvBusDest1,destination[1]);
                remoteViews.setTextViewText(R.id.tvBusETA1,eta[1]);
            }else{
                clearRemoteView();
                remoteViews.setTextViewText(R.id.tvBusNo,"No Information Available");

            }



            //REFRESH BUTTON CLICK LISTENER
            remoteViews.setOnClickPendingIntent(R.id.widget_button, MyWidgetProvider.buildButtonPendingIntent(context));

            MyWidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
        }
    }
}

