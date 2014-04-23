package it.garybrady.travel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;
import it.garybrady.traveldata.myDatabase;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import android.preview.support.wearable.notifications.*;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat;
import android.app.Notification.Builder;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    final public static String ONE_TIME = "onetime";
    Context c;
    private NotificationManager notificationManager;
    myDatabase dba;
    String info[]=new String[3];
    String notificationDeets=null;






    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();
        c= context;

        //You can do the processing here.
        Bundle extras = intent.getExtras();
        StringBuilder msgStr = new StringBuilder();

        if(extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)){
            //Make sure this intent has been sent by the one-time timer button.
            msgStr.append("One time Timer : ");
        }
        Format formatter = new SimpleDateFormat("hh:mm:ss a");
        msgStr.append(formatter.format(new Date()));

        //Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();
        dba = new myDatabase(c);
        dba.open();
        info=dba.getMostRecentAlarm();
        dba.close();
        new loadBusTimeInfo(info[1]).execute();
        //Toast.makeText(context, info[0]+info[1]+info[2], Toast.LENGTH_LONG).show();
        notificationDeets = "The "+String.valueOf(info[0])+" is leaving in "+String.valueOf(info[2]);

        //Release the lock
        wl.release();
    }

    public void SetAlarm(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After 30 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 20 , pi);
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void setOnetimeTimer(Context context){
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }

    private class loadBusTimeInfo extends AsyncTask<Void, Integer, Void> {
        String ref=null;
        boolean trig =false;

        public loadBusTimeInfo(String stopReference) {
            super();
            ref = stopReference;
        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected Void doInBackground(Void... params) {
            String webStopRef="http://192.3.177.209/liveInfo.php?RefNo=";
            String tempBusNO = null, tempTime=null;
            String result = "";

            InputStream isr = null;
            String refURL;
            StringBuilder urlBuild = new StringBuilder();
            urlBuild.append(webStopRef);
            urlBuild.append(ref);
            refURL = urlBuild.toString();
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
                    tempBusNO=json.getString("busNo");
                    if (tempBusNO.contains(info[0])){

                        tempTime =json.getString("time");
                        if (tempTime.contains(info[2])){
                            trig=true;
                            break;
                        }else{
                            trig=false;
                        }
                    }else{
                        trig=false;
                    }

                }
            } catch (Exception e) {

                Log.e("log_tag", "Error Parsing Data "+e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (trig){
                sendNotif();
            }

        }
    }

    private void sendNotif() {
        //Toast.makeText(c,"get yo bus",Toast.LENGTH_LONG).show();
        //notificationManager = getNotificationManager();
        Intent stopCheck = new Intent(c, AlarmStopRepeating.class);
        //stopCheck.putExtra("stop", 11);

        Bundle ok = new Bundle();
        ok.putInt("stop", 11);
        stopCheck.putExtras(ok);

        Intent repeatCheck = new Intent(c, AlarmContinueRepeating.class);
        Bundle repeat = new Bundle();
        repeat.putInt("repeat", 33);
        repeatCheck.putExtras(repeat);
        //repeatCheck.putExtra("repeat", 33);

        Notification.Builder notificationBuilder  = new Notification.Builder(c)  //using the Notification.Builder class
                .setContentTitle("Travel Alarm")
                .setContentText(notificationDeets)
                .setSmallIcon(R.drawable.ic_launcher)
                .setOngoing(true)
//                .setPriority(Notification.PRIORITY_HIGH)


                .addAction(
                        R.drawable.img_notification_done,
                        "Stop Alarm",
                        PendingIntent.getActivity(c, 0, stopCheck, 0, null))

                .addAction(
                        R.drawable.img_notification_restart,
                        "Reset Alarm",
                        PendingIntent.getActivity(c, 0, repeatCheck, 0, null));



        // Create a WearablesNotification.Builder to add special functionality for wearables
        /*Notification notification =
                new WearableNotifications.Builder(notificationBuilder)
                        .setHintHideIcon(true)
                        .build();*/

        // Get an instance of the NotificationManager service
        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(c);
        notificationManager.notify(1, notificationBuilder.build());
    }

    private NotificationManager getNotificationManager()
    {
        return (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}

