package it.garybrady.travel;

/**
 * Created by Gary on 14/04/14.
 */
import android.app.NotificationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class AlarmManagerActivity extends Activity {

    private AlarmManagerBroadcastReceiver alarm;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_test);
        alarm = new AlarmManagerBroadcastReceiver();
        Bundle b = getIntent().getExtras();
        NotificationManager notificationManager = getNotificationManager();



        notificationManager.cancel(1);




    }






    @Override
    protected void onStart() {
        super.onStart();
    }
    private NotificationManager getNotificationManager()
    {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void startRepeatingTimer(View view) {
        Context context = this.getApplicationContext();
        if(alarm != null){
            alarm.SetAlarm(context);
        }else{
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelRepeatingTimer(View view){
        Context context = this.getApplicationContext();
        if(alarm != null){
            alarm.CancelAlarm(context);
        }else{
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }

    public void onetimeTimer(View view){
        Context context = this.getApplicationContext();
        if(alarm != null){
            alarm.setOnetimeTimer(context);
        }else{
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }


}
