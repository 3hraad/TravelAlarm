package it.garybrady.travel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.google.android.gms.maps.model.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.content.SharedPreferences.Editor;


import it.garybrady.travel.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;

public class BestBusMap extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final int GPS_ERRORDIALOG_REQUEST = 9001;
    @SuppressWarnings("unused")
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9002;
    GoogleMap mMap;
    EditText et;
    String webStopRef="http://192.3.177.209/liveInfo.php?RefNo=";
    String receivedBus=null;
    String selectedBus=null;
    ImageView refresh;
    Animation rotation;
    Button handle;
    private HashMap<String,String> HM;
    LatLng destination=null;



    private static final float DEFAULTZOOM = 15;
    @SuppressWarnings("unused")
    private static final String LOGTAG = "Maps";

    LocationClient mLocationClient;
    Marker mArrive, mDestination,mDeptarture;
    SlidingDrawer sd;
    ArrayList<String> realBusTimeInfo;
    ListView busInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        receivedBus=b.getString("SelectedBus");




        if (servicesOK()) {
            setContentView(R.layout.activity_map);
            StrictMode.enableDefaults();
            if (initMap()) {
                mMap.setMyLocationEnabled(true);
                mLocationClient = new LocationClient(this, this, this);
                mLocationClient.connect();
            }
            else {
                Toast.makeText(this, "Map not available!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "shit yo!", Toast.LENGTH_SHORT).show();
        }
        sd = (SlidingDrawer) findViewById(R.id.slidingDrawer2);
        sd.setEnabled(false);




        mMap.setPadding(0,70,0,0);
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker clickedMarker) {

                if(clickedMarker.getTitle().equals("Closest Stop to Destination")){

                }else if(clickedMarker.getTitle().equals("Destination")){
                    Toast.makeText(getApplicationContext(),"Long click to drag pin or to create a new Destination",Toast.LENGTH_LONG).show();

                } else{
                busInfoList = (ListView) findViewById(R.id.listViewBusTimes);
                realBusTimeInfo = new ArrayList<String>();
                //getBusInfo(clickedMarker.getTitle());
                selectedBus =clickedMarker.getTitle();
                new loadBusTimeInfo(clickedMarker.getTitle()).execute();
                }
                return false;
            }
        });


        refresh =(ImageView) findViewById(R.id.ivReload);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedBus==null){
                    Toast.makeText(getApplication(),"No bus stop selected",Toast.LENGTH_LONG).show();
                }else{
                    realBusTimeInfo = new ArrayList<String>();
                    new loadBusTimeInfo(selectedBus).execute();
                }
                sd.open();

            }
        });


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                if (destination!=null)
                {
                    mDestination.remove();
                    mArrive.remove();
                }
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title("Destination")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                mDestination = mMap.addMarker(options);
                mDestination.setDraggable(true);

                destination=latLng;
                plotMarkers("arrive");
            }


        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                mArrive.remove();
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                destination=mDestination.getPosition();
                plotMarkers("arrive");
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
               if (marker.getTitle().equals("Closest Stop to Destination")){
                   Bundle b = new Bundle();
                   b.putDouble("lat", marker.getPosition().latitude);
                   b.putDouble("lng", marker.getPosition().longitude);
                   b.putDouble("radius", 75);
                   Intent i = new Intent(BestBusMap.this,GeofenceConstruct.class);
                   i.putExtras(b);
                   startActivity(i);
                   finish();
               }
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    public boolean servicesOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        }
        else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(this, "Can't connect to Google Play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean initMap() {
        if (mMap == null) {
            SupportMapFragment mapFrag =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mMap = mapFrag.getMap();
        }
        return (mMap != null);
    }

    private void gotoLocation(double lat, double lng,float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.moveCamera(update);


    }

    public void geoLocate(View v) throws IOException {

        et = (EditText) findViewById(R.id.etLongGeoLocate);
        String location = et.getText().toString();
        if (location.length() == 0) {
            Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show();
            return;
        }

        hideSoftKeyboard(v);

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);
        Address add = list.get(0);
        String locality = add.getLocality();
        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

        double lat = add.getLatitude();
        double lng = add.getLongitude();

        gotoLocation(lat, lng, DEFAULTZOOM);

        //Add a marker to searched location
		
		/*if(marker !=null){
			marker.remove();
		}
		
		MarkerOptions options = new MarkerOptions()
			.title(locality)
			.position(new LatLng(lat,lng));
		marker = mMap.addMarker(options);*/


    }

    private void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.mapTypeNormal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MapStateManager mgr = new MapStateManager(this);
        mgr.saveMapState(mMap);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MapStateManager mgr = new MapStateManager(this);
        CameraPosition position = mgr.getSavedCameraPosition();
        if (position != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            mMap.moveCamera(update);
            mMap.setMapType(mgr.getSavedMapType());
        }

    }


    protected LatLng getCurrentLocation() {
        Location currentLocation = mLocationClient.getLastLocation();
        if (currentLocation == null) {
            Toast.makeText(this, "Current location isn't available", Toast.LENGTH_SHORT).show();
            return null;
        }
        else {
            LatLng ll = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            return ll;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
    }

    @Override
    public void onConnected(Bundle arg0) {
        plotMarkers("dept");

//		Toast.makeText(this, "Connected to location service", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected() {
    }

    public void plotMarkers(String whichStop){
        double lat = 0,lng=0;
        if (whichStop=="dept"){
            LatLng ll=getCurrentLocation();
            lat=ll.latitude;
            lng=ll.longitude;
        } else if (whichStop=="arrive"){
            lat=destination.latitude;
            lng=destination.longitude;
        }

        String ref = null;
        String result = "";
        String readerURL;
        StringBuilder readerBuild = new StringBuilder();
        readerBuild.append("http://192.3.177.209/busMap/closeBus.php?BusNo=");

        readerBuild.append(receivedBus);
        readerBuild.append("&lat=");
        readerBuild.append(lat);
        readerBuild.append("&lng=");
        readerBuild.append(lng);
        readerURL=readerBuild.toString();
        Toast.makeText(getApplicationContext(),readerURL,Toast.LENGTH_LONG).show();
        InputStream isr = null;
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(readerURL);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            isr = entity.getContent();
        }
        catch(Exception e){
            Log.e("log_tag", "Error in http connection "+e.toString());

        }
        //convert response to string
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(isr,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "  ");
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
                ref=json.getString("refNo");
                lat= new Double(json.getString("lat"));
                lng= new Double(json.getString("long"));

                MarkerOptions options = new MarkerOptions()
                        .title(ref)
                        .position(new LatLng(lat,lng));

                if (whichStop=="dept"){

                    mDeptarture = mMap.addMarker(options);
                    mDeptarture.setDraggable(false);
                } else if (whichStop=="arrive"){
                    mArrive = mMap.addMarker(options);
                    mArrive.setDraggable(true);
                    mArrive.setTitle("Closest Stop to Destination");
                    mArrive.setSnippet("Click here to set alarm");

                }



            }
        } catch (Exception e) {

            Log.e("log_tag", "Error Parsing Data "+e.toString());
        }


    }


    public void getBusInfo(String stopReference){

        String temp = null;
        String result = "";
        InputStream isr = null;
        String refURL;
        StringBuilder urlBuild = new StringBuilder();
        urlBuild.append(webStopRef);
        urlBuild.append(stopReference);
        refURL = urlBuild.toString();



        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(refURL);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            isr = entity.getContent();
        }
        catch(Exception e){
            Log.e("log_tag", "Error in http connection "+e.toString());

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
                temp=json.getString("busNo");
                temp +="  ";
                temp +=json.getString("dest");
                temp +="  ";
                temp +=json.getString("time");
                realBusTimeInfo.add(i, temp);
            }
        } catch (Exception e) {

            Log.e("log_tag", "Error Parsing Data "+e.toString());
        }


    }

    private class loadBusTimeInfo extends AsyncTask<Void, Integer, Void>{
        String ref=null;
        public loadBusTimeInfo(String stopReference) {
            super();
            ref = stopReference;
        }

        @Override
        protected void onPreExecute() {
            busInfoList.setAdapter(null);


            rotation = AnimationUtils.loadAnimation(BestBusMap.this, R.anim.rotation_clockwise);
            rotation.setRepeatCount(Animation.INFINITE);
            refresh.startAnimation(rotation);
            sd.animateOpen();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String temp = null;
            String result = "";
            InputStream isr = null;
            String refURL;
            StringBuilder urlBuild = new StringBuilder();
            urlBuild.append(webStopRef);
            urlBuild.append(ref);
            refURL = urlBuild.toString();



            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(refURL);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                isr = entity.getContent();
            }
            catch(Exception e){
                Log.e("log_tag", "Error in http connection "+e.toString());

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
                    temp=json.getString("busNo");
                    temp +="  ";
                    temp +=json.getString("dest");
                    temp +="  ";
                    temp +=json.getString("time");
                    realBusTimeInfo.add(i, temp);
                }
            } catch (Exception e) {

                Log.e("log_tag", "Error Parsing Data "+e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplicationContext(),"Times loaded",Toast.LENGTH_LONG).show();
            refresh.clearAnimation();
            //loadBusTimeInfo(clickedMarker.getTitle()).execute;
            ArrayAdapter<String> arrayAdapter=
                    new ArrayAdapter<String>(BestBusMap.this,android.R.layout.simple_list_item_1, realBusTimeInfo );
            busInfoList.setAdapter(arrayAdapter);
        }
    }
}
