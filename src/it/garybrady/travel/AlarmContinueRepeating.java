package it.garybrady.travel;
 /*
 * Called when user wants bus alarm to continue
 * Does nothing but remove notification
 * */
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.preview.support.v4.app.NotificationManagerCompat;

/**
 * Created by Gary on 17/04/14.
 */
public class AlarmContinueRepeating extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationManager notificationManager = getNotificationManager();

//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(1);
        finish();
    }
    private NotificationManager getNotificationManager()
    {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
}