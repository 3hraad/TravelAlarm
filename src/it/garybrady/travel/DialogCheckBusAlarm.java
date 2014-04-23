package it.garybrady.travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

/**
 * Created by Gary on 04/04/14.
 */
public class DialogCheckBusAlarm extends Activity {
    NumberPicker busPicker, etaAlarm;
    String eta[]={"5 Mins","10 Mins","15 Mins"};
    String busNo[]={"205","208","201"};

    String iETA=null,bus=null;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_bus_alarm);
        etaAlarm=(NumberPicker)findViewById(R.id.npTime);
        etaAlarm.setMinValue(1);
        etaAlarm.setMaxValue(3);
        etaAlarm.setDisplayedValues(eta);



        busPicker=(NumberPicker)findViewById(R.id.npBusNoPick);
        busPicker.setMinValue(1);
        busPicker.setMaxValue(3);
        busPicker.setDisplayedValues(busNo);

        Button setAlarm = (Button) findViewById(R.id.bSetBusChecker);
        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iETA= eta[etaAlarm.getValue()-1].toString();
                bus=busNo[busPicker.getValue()-1].toString();
                Intent data= new Intent();
                data.putExtra("eta",iETA);
                data.putExtra("bus",bus);
                setResult(RESULT_OK, data);
                finish();
            }
        });

    }
}