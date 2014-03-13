package it.garybrady.travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

/**
 * Created by Gary on 06/02/14.
 */
public class BestBusViewPicker extends Activity {

    NumberPicker pickBus;
    String busOptions[]={"All","205","208"};
    Button viewBus;
    TextView selectBus;
    String temp=null;
    int tempInt=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_view_picker);
        pickBus = (NumberPicker) findViewById(R.id.npBusViewPicker);
        pickBus.setMaxValue(3);
        pickBus.setMinValue(1);
        pickBus.setDisplayedValues(busOptions);
        viewBus = (Button) findViewById(R.id.bBusViewPicker);
        selectBus=(TextView) findViewById(R.id.tvSelectBus);

        viewBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempInt=pickBus.getValue();
                temp=(busOptions[tempInt-1]);
                Intent i = new Intent(BestBusViewPicker.this,BestBusMap.class);
                Bundle b= new Bundle();
                b.putString("SelectedBus", temp);
                i.putExtras(b);
                startActivity(i);



            }
        });

    }
}
