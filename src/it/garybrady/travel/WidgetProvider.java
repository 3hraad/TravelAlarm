package it.garybrady.travel;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Gary on 09/02/14.
 */
public class WidgetProvider extends AppWidgetProvider {
    final static int APPWIDGET= 3333;
    static String  ref="241831";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //Loop through all widgets to display an update
        final int N=appWidgetIds.length;
        SharedPreferences prefs = context.getSharedPreferences("busInfo",0);
        ref= prefs.getString("busRef","0");
        for(int i=0;i<N;i++){
            int appWidgetId=appWidgetIds[i];
            String titlePrefix="Time since the widget was started";
            updateAppWidget(context,appWidgetManager,appWidgetId,titlePrefix);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);


        String tempBusNo,tempBusDest,tempBusETA;
        String date = "Last Updated :" + (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        SharedPreferences prefs = context.getSharedPreferences("busInfo",0);
        ref= prefs.getString("busRef","0");
        JSONArray jArray = getBusInfo(ref);


        try {
            JSONObject json = jArray.getJSONObject(0);
            tempBusNo=json.getString("busNo");
            tempBusDest =json.getString("dest");
            tempBusETA =json.getString("time");
            views.setTextViewText(R.id.tvBusNo,tempBusNo);
            views.setTextViewText(R.id.tvBusDest,tempBusDest);
            views.setTextViewText(R.id.tvBusETA,tempBusETA);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject json = jArray.getJSONObject(1);
            tempBusNo=json.getString("busNo");
            tempBusDest =json.getString("dest");
            tempBusETA =json.getString("time");
            views.setTextViewText(R.id.tvBusNo1,tempBusNo);
            views.setTextViewText(R.id.tvBusDest1,tempBusDest);
            views.setTextViewText(R.id.tvBusETA1,tempBusETA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        views.setTextViewText(R.id.tvLastUpdated, date);
        Toast.makeText(context, "Clicked!!", Toast.LENGTH_SHORT).show();
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String titlePrefix) {

        long millis=System.currentTimeMillis();
        int seconds=(int)(millis/1000);
        int minutes=seconds/60;
        seconds=seconds%60;
        ArrayList<String> realBusTimeInfo;
        ListView busInfoList;
        String  ref="241831";
        String tempBusNo,tempBusDest,tempBusETA;
        String date = "Last Updated :" + (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString())+"Bus Ref: "+ref;
        CharSequence text=titlePrefix;
        text =text+ " " + minutes + ":" + String.format("%02d",seconds);

        //Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget_layout);


        JSONArray jArray = getBusInfo(ref);


        try {
            JSONObject json = jArray.getJSONObject(0);
            tempBusNo=json.getString("busNo");
            tempBusDest =json.getString("dest");
            tempBusETA =json.getString("time");
            views.setTextViewText(R.id.tvBusNo,tempBusNo);
            views.setTextViewText(R.id.tvBusDest,tempBusDest);
            views.setTextViewText(R.id.tvBusETA,tempBusETA);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject json = jArray.getJSONObject(1);
            tempBusNo=json.getString("busNo");
            tempBusDest =json.getString("dest");
            tempBusETA =json.getString("time");
            views.setTextViewText(R.id.tvBusNo1,tempBusNo);
            views.setTextViewText(R.id.tvBusDest1,tempBusDest);
            views.setTextViewText(R.id.tvBusETA1,tempBusETA);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        views.setTextViewText(R.id.tvLastUpdated, date);

        //tell that widget manager
        appWidgetManager.updateAppWidget(appWidgetId,views);
    }




    public static JSONArray getBusInfo(String stopReference){
        String webStopRef="http://192.3.177.209/liveInfo.php?RefNo=";
        String temp = null;
        String result = "";
        InputStream isr = null;
        String refURL;
        StringBuilder urlBuild = new StringBuilder();
        urlBuild.append(webStopRef);
        urlBuild.append(stopReference);
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
            return  jArray;
        } catch (Exception e) {
            Log.e("log_tag", "Error Parsing Data "+e.toString());
            return null;
        }


    }
}
