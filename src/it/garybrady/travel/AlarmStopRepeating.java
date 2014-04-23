package it.garybrady.travel;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

/**
 * Created by Gary on 17/04/14.
 */
public class AlarmStopRepeating extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();
        //NotificationManager notificationManager = getNotificationManager();


        Context context = this.getApplicationContext();
        if(alarm != null){
            alarm.CancelAlarm(context);
        }else{
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.cancel(1);
        finish();
    }
    private NotificationManager getNotificationManager()
    {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
}