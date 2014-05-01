package it.garybrady.travel;
     /*
     * Called if user clicks notification from bus alarm
     * Allows them to stop or repeat bus alarm
     * */
/**
 * Created by Gary on 14/04/14.
 */
import android.app.NotificationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AlarmManagerActivity extends Activity {

    private AlarmManagerBroadcastReceiver alarm;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_test);
        alarm = new AlarmManagerBroadcastReceiver();
        NotificationManager notificationManager = getNotificationManager();



        notificationManager.cancel(1);
        Button continueAlarm = (Button) findViewById(R.id.bContinueAlarm);
        continueAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



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
        finish();
    }





}
