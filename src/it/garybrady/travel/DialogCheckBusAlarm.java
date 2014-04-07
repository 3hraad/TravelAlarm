package it.garybrady.travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

/**
 * Created by Gary on 04/04/14.
 */
public class DialogCheckBusAlarm extends Activity {
    NumberPicker checkInterval, etaAlarm;
    String eta[]={"5","10","15"};
    String check[]={"1","2","5"};
    int iETA, iCheck;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_bus_alarm);
        etaAlarm=(NumberPicker)findViewById(R.id.npTime);
        etaAlarm.setMinValue(1);
        etaAlarm.setMaxValue(3);
        etaAlarm.setDisplayedValues(eta);



        checkInterval=(NumberPicker)findViewById(R.id.npCheckInterval);
        checkInterval.setMinValue(1);
        checkInterval.setMaxValue(3);
        checkInterval.setDisplayedValues(check);

        Button setAlarm = (Button) findViewById(R.id.bSetBusChecker);
        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iETA= Integer.parseInt(eta[etaAlarm.getValue()-1].toString());
                iCheck=Integer.parseInt(check[checkInterval.getValue()-1].toString());
                Intent data= new Intent();
                data.putExtra("eta",iETA);
                data.putExtra("check",iCheck);
                setResult(RESULT_OK, data);
                finish();
            }
        });

    }
}