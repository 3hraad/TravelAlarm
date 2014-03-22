package it.garybrady.travel;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * Created by Gary on 22/03/14.
 */
public class ViewBothGeofences extends TabActivity {

    private static final String CURRENT_SPEC="Current";
    private static final String PREVIOUS_SPEC="Previous";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_geos);

        TabHost tabHost=getTabHost();

        TabSpec currentSpec=tabHost.newTabSpec(CURRENT_SPEC);
        currentSpec.setIndicator(CURRENT_SPEC);
        Intent iCurrent = new Intent(this,GeofenceCurrent.class);
        currentSpec.setContent(iCurrent);

        TabSpec prevSpec=tabHost.newTabSpec(PREVIOUS_SPEC);
        prevSpec.setIndicator(PREVIOUS_SPEC);
        Intent iPrev = new Intent(this,GeofencePrevious.class);
        prevSpec.setContent(iPrev);

        tabHost.addTab(currentSpec);
        tabHost.addTab(prevSpec);

    }
}
