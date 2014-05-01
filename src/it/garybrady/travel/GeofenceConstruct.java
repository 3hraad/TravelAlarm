/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.garybrady.travel;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import it.garybrady.travel.GeofenceUtils.REMOVE_TYPE;
import it.garybrady.travel.GeofenceUtils.REQUEST_TYPE;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import it.garybrady.traveldata.myDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GeofenceConstruct extends FragmentActivity {

    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 24;
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * DateUtils.HOUR_IN_MILLIS;

    // Store the current request
    private REQUEST_TYPE mRequestType;

    // Store the current type of removal
    private REMOVE_TYPE mRemoveType;

    // Persistent storage for geofences
    private SimpleGeofenceStore mPrefs;

    // Store a list of geofences to add
    List<Geofence> mCurrentGeofences;

    // Add geofences handler
    private GeofenceRequester mGeofenceRequester;
    // Remove geofences handler
    private GeofenceRemover mGeofenceRemover;
    // Handle to geofence 1 latitude in the UI
    private EditText mLatitude1;
    private long plat;

    private EditText mLongitude1;
    private long plng;
    private EditText mRadius1;
    private float prad;

    //geofence database
    myDatabase dba;
    EditText title;


    /*
     * Internal lightweight geofence objects for geofence 1 and 2
     */
    private SimpleGeofence mUIGeofence1;

    // decimal formats for latitude, longitude, and radius
    private DecimalFormat mLatLngFormat;
    private DecimalFormat mRadiusFormat;

    /*
     * An instance of an inner class that receives broadcasts from listeners and from the
     * IntentService that receives geofence transition events
     */
    private GeofenceSampleReceiver mBroadcastReceiver;

    // An intent filter for the broadcast receiver
    private IntentFilter mIntentFilter;

    // Store the list of geofences to remove
    private List<String> mGeofenceIdsToRemove;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the pattern for the latitude and longitude format
        String latLngPattern = getString(R.string.lat_lng_pattern);

        // Set the format for latitude and longitude
        mLatLngFormat = new DecimalFormat(latLngPattern);

        // Localize the format
        mLatLngFormat.applyLocalizedPattern(mLatLngFormat.toLocalizedPattern());

        // Set the pattern for the radius format
        String radiusPattern = getString(R.string.radius_pattern);

        // Set the format for the radius
        mRadiusFormat = new DecimalFormat(radiusPattern);

        // Localize the pattern
        mRadiusFormat.applyLocalizedPattern(mRadiusFormat.toLocalizedPattern());

        // Create a new broadcast receiver to receive updates from the listeners and service
        mBroadcastReceiver = new GeofenceSampleReceiver();

        // Create an intent filter for the broadcast receiver
        mIntentFilter = new IntentFilter();

        // Action for broadcast Intents that report successful addition of geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_ADDED);

        // Action for broadcast Intents that report successful removal of geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);

        // Action for broadcast Intents containing various types of geofencing errors
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);

        // All Location Services sample apps use this category
        mIntentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

        // Instantiate a new geofence storage area
        mPrefs = new SimpleGeofenceStore(this);

        // Instantiate the current List of geofences
        mCurrentGeofences = new ArrayList<Geofence>();

        // Instantiate a Geofence requester
        mGeofenceRequester = new GeofenceRequester(this);

        // Instantiate a Geofence remover
        mGeofenceRemover = new GeofenceRemover(this);

        // Attach to the main UI
        setContentView(R.layout.activity_confirm_geofence);

        // Get handles to the Geofence editor fields in the UI
        mLatitude1 = (EditText) findViewById(R.id.value_latitude_1);
        mLongitude1 = (EditText) findViewById(R.id.value_longitude_1);
        mLongitude1.setEnabled(false);
        mLatitude1.setEnabled(false);


        mRadius1 = (EditText) findViewById(R.id.value_radius_1);
        title=(EditText)findViewById(R.id.etTitle);

        Bundle b = getIntent().getExtras();
        if(b.getString("title",null)!=null){
            title.setText(b.getString("title"));
        }
        Double rLat =b.getDouble("lat",0);
        Double rLng =b.getDouble("lng",0);
        Double rRad =b.getDouble("radius",0);

        String temp = rLat.toString();
        mLatitude1.setText(temp) ;
        temp = rLng.toString();
        mLongitude1.setText(temp);
        temp = rRad.toString();
        mRadius1.setText(temp);



    }

    /*
     * Handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed() in
     * GeofenceRemover and GeofenceRequester may call startResolutionForResult() to
     * start an Activity that handles Google Play services problems. The result of this
     * call returns here, to onActivityResult.
     * calls
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // If the request was to add geofences
                        if (GeofenceUtils.REQUEST_TYPE.ADD == mRequestType) {

                            // Toggle the request flag and send a new request
                            mGeofenceRequester.setInProgressFlag(false);

                            // Restart the process of adding the current geofences
                            mGeofenceRequester.addGeofences(mCurrentGeofences);

                            Toast.makeText(getApplicationContext(),"Alarm Set",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(GeofenceConstruct.this, MyActivity.class));
                            finish();

                            // If the request was to remove geofences
                        } else if (GeofenceUtils.REQUEST_TYPE.REMOVE == mRequestType ){

                            // Toggle the removal flag and send a new removal request
                            mGeofenceRemover.setInProgressFlag(false);

                            // If the removal was by Intent
                            if (GeofenceUtils.REMOVE_TYPE.INTENT == mRemoveType) {

                                // Restart the removal of all geofences for the PendingIntent
                                mGeofenceRemover.removeGeofencesByIntent(
                                        mGeofenceRequester.getRequestPendingIntent());

                                // If the removal was by a List of geofence IDs
                            } else {

                                // Restart the removal of the geofence list
                                mGeofenceRemover.removeGeofencesById(mGeofenceIdsToRemove);
                            }
                        }
                        break;

                    // If any other result was returned by Google Play services
                    default:

                        // Report that Google Play services was unable to resolve the problem.
                        Log.d(GeofenceUtils.APPTAG, getString(R.string.no_resolution));
                }

                // If any other request code was received
            default:
                // Report that this Activity received an unknown requestCode
                Log.d(GeofenceUtils.APPTAG,
                        getString(R.string.unknown_activity_request_code, requestCode));

                break;
        }
    }

    /*
    * Get biggest id from geofence table to id of new geofence
    * */

    private int getMaxId() {
        int temp;
        dba=new myDatabase(this);
        dba.open();
        temp=dba.getMaxGeoId();
        dba.close();
        return temp;
    }
    /*
     * Whenever the Activity resumes, reconnect the client to Location
     * Services and reload the last geofences that were set
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Register the broadcast receiver to receive status updates
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, mIntentFilter);


    }





    @Override
    protected void onPause() {
        super.onPause();

    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {

            // In debug mode, log the status
            Log.d(GeofenceUtils.APPTAG, getString(R.string.play_services_available));

            // Continue
            return true;

            // Google Play services was not available for some reason
        } else {

            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), GeofenceUtils.APPTAG);
            }
            return false;
        }
    }


    public void onRegisterClicked(View view) {

        /*
         * Record the request as an ADD. If a connection error occurs,
         * the app can automatically restart the add request if Google Play services
         * can fix the error
         */
        mRequestType = GeofenceUtils.REQUEST_TYPE.ADD;

        /*
         * Check for Google Play services. Do this after
         * setting the request type. If connecting to Google Play services
         * fails, onActivityResult is eventually called, and it needs to
         * know what type of request was in progress.
         */
        if (!servicesConnected()) {

            return;
        }

        /*
         * Check that the input fields have values and that the values are with the
         * permitted range
         */
        if (!checkInputFields()) {
            return;
        }

        int maxId=getMaxId();
        maxId++;

        /*
         * Create a version of geofence 1 that is "flattened" into individual fields. This
         * allows it to be stored in SharedPreferences.
         */
        mUIGeofence1 = new SimpleGeofence(
                String.valueOf(maxId),
                // Get latitude, longitude, and radius from the UI
                Double.valueOf(mLatitude1.getText().toString()),
                Double.valueOf(mLongitude1.getText().toString()),
                Float.valueOf(mRadius1.getText().toString()),
                // Set the expiration time
                GEOFENCE_EXPIRATION_IN_MILLISECONDS,
                // Only detect entry transitions
                Geofence.GEOFENCE_TRANSITION_ENTER);

        // Store this flat version in SharedPreferences
        mPrefs.setGeofence(String.valueOf(maxId), mUIGeofence1);

        /*
         * Create a version of geofence 2 that is "flattened" into individual fields. This
         * allows it to be stored in SharedPreferences.
         */

        /*
         * Add Geofence objects to a List. toGeofence()
         * creates a Location Services Geofence object from a
         * flat object
         */
        mCurrentGeofences.add(mUIGeofence1.toGeofence());

        // Start the request. Fail if there's already a request in progress
        try {
            // Try to add geofences
            mGeofenceRequester.addGeofences(mCurrentGeofences);
            //Hey Ho add to DB
            dba=new myDatabase(this);
            dba.open();
            dba.insertGeofence(title.getText().toString(),Double.valueOf(mLatitude1.getText().toString()),
                    Double.valueOf(mLongitude1.getText().toString()));
            dba.close();


            Toast.makeText(getApplicationContext(),"Geofence Created",Toast.LENGTH_LONG).show();
            startActivity(new Intent(GeofenceConstruct.this, MyActivity.class));
            finish();

        } catch (UnsupportedOperationException e) {
            // Notify user that previous request hasn't finished.
            Toast.makeText(this, R.string.add_geofences_already_requested_error,
                    Toast.LENGTH_LONG).show();
        }

    }
    /**
     * Check all the input values and flag those that are incorrect
     * @return true if all the widget values are correct; otherwise false
     */
    private boolean checkInputFields() {
        // Start with the input validity flag set to true
        boolean inputOK = true;

        /*
         * Latitude, longitude, and radius values can't be empty. If they are, highlight the input
         * field in red and put a Toast message in the UI. Otherwise set the input field highlight
         * to black, ensuring that a field that was formerly wrong is reset.
         */


        if (TextUtils.isEmpty(title.getText())) {
            title.setTextColor(Color.RED);
            Toast.makeText(this, R.string.geofence_input_error_missing, Toast.LENGTH_LONG).show();

            // Set the validity to "invalid" (false)
            inputOK = false;
        }
        if (TextUtils.isEmpty(mLatitude1.getText())) {
            mLatitude1.setTextColor(Color.RED);
            Toast.makeText(this, R.string.geofence_input_error_missing, Toast.LENGTH_LONG).show();

            // Set the validity to "invalid" (false)
            inputOK = false;
        } else {

            //mLatitude1.setBackgroundColor(Color.BLACK);
        }



        if (TextUtils.isEmpty(mLongitude1.getText())) {
            mLongitude1.setTextColor(Color.RED);
            Toast.makeText(this, R.string.geofence_input_error_missing, Toast.LENGTH_LONG).show();

            // Set the validity to "invalid" (false)
            inputOK = false;
        } else {

            //mLongitude1.setBackgroundColor(Color.BLACK);
        }
        if (TextUtils.isEmpty(mRadius1.getText())) {
            mRadius1.setTextColor(Color.RED);
            Toast.makeText(this, R.string.geofence_input_error_missing, Toast.LENGTH_LONG).show();

            // Set the validity to "invalid" (false)
            inputOK = false;
        } else {

            //mRadius1.setBackgroundColor(Color.BLACK);
        }



        /*
         * If all the input fields have been entered, test to ensure that their values are within
         * the acceptable range. The tests can't be performed until it's confirmed that there are
         * actual values in the fields.
         */
        if (inputOK) {

            /*
             * Get values from the latitude, longitude, and radius fields.
             */
            double lat1 = Double.valueOf(mLatitude1.getText().toString());
            double lng1 = Double.valueOf(mLongitude1.getText().toString());
            float rd1 = Float.valueOf(mRadius1.getText().toString());
            //TODO init variables here

            /*
             * Test latitude and longitude for minimum and maximum values. Highlight incorrect
             * values and set a Toast in the UI.
             */

            if (lat1 > GeofenceUtils.MAX_LATITUDE || lat1 < GeofenceUtils.MIN_LATITUDE) {
                mLatitude1.setTextColor(Color.RED);
                Toast.makeText(
                        this,
                        R.string.geofence_input_error_latitude_invalid,
                        Toast.LENGTH_LONG).show();

                // Set the validity to "invalid" (false)
                inputOK = false;
            } else {

                //mLatitude1.setBackgroundColor(Color.BLACK);
            }

            if ((lng1 > GeofenceUtils.MAX_LONGITUDE) || (lng1 < GeofenceUtils.MIN_LONGITUDE)) {
                mLongitude1.setTextColor(Color.RED);
                Toast.makeText(
                        this,
                        R.string.geofence_input_error_longitude_invalid,
                        Toast.LENGTH_LONG).show();

                // Set the validity to "invalid" (false)
                inputOK = false;
            } else {

                //mLongitude1.setBackgroundColor(Color.BLACK);
            }




            if (rd1 < GeofenceUtils.MIN_RADIUS) {
                mRadius1.setTextColor(Color.RED);
                Toast.makeText(
                        this,
                        R.string.geofence_input_error_radius_invalid,
                        Toast.LENGTH_LONG).show();

                // Set the validity to "invalid" (false)
                inputOK = false;
            } else {

                //mRadius1.setBackgroundColor(Color.BLACK);
            }

        }

        // If everything passes, the validity flag will still be true, otherwise it will be false.
        return inputOK;
    }

    /**
     * Define a Broadcast receiver that receives updates from connection listeners and
     * the geofence transition service.
     */
    public class GeofenceSampleReceiver extends BroadcastReceiver {
        /*
         * Define the required method for broadcast receivers
         * This method is invoked when a broadcast Intent triggers the receiver
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            // Check the action code and determine what to do
            String action = intent.getAction();

            // Intent contains information about errors in adding or removing geofences
            if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_ERROR)) {

                handleGeofenceError(context, intent);

                // Intent contains information about successful addition or removal of geofences
            } else if (
                    TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_ADDED)
                            ||
                            TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_REMOVED)) {

                handleGeofenceStatus(context, intent);

                // Intent contains information about a geofence transition
            } else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_TRANSITION)) {

                handleGeofenceTransition(context, intent);

                // The Intent contained an invalid action
            } else {
                Log.e(GeofenceUtils.APPTAG, getString(R.string.invalid_action_detail, action));
                Toast.makeText(context, R.string.invalid_action, Toast.LENGTH_LONG).show();
            }
        }

        /**
         * If you want to display a UI message about adding or removing geofences, put it here.
         *
         * @param context A Context for this component
         * @param intent The received broadcast Intent
         */
        private void handleGeofenceStatus(Context context, Intent intent) {

        }

        /**
         * Report geofence transitions to the UI
         *
         * @param context A Context for this component
         * @param intent The Intent containing the transition
         */
        private void handleGeofenceTransition(Context context, Intent intent) {
            /*
             * If you want to change the UI when a transition occurs, put the code
             * here. The current design of the app uses a notification to inform the
             * user that a transition has occurred.
             */
        }

        /**
         * Report addition or removal errors to the UI, using a Toast
         *
         * @param intent A broadcast Intent sent by ReceiveTransitionsIntentService
         */
        private void handleGeofenceError(Context context, Intent intent) {
            String msg = intent.getStringExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS);
            Log.e(GeofenceUtils.APPTAG, msg);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
