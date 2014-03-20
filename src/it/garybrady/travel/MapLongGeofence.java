package it.garybrady.travel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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

public class MapLongGeofence extends FragmentActivity implements
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
    Circle circle;
    SeekBar radiusSeekbar;
    Switch  radiusSwitch;
    Button go,setAlarm;
    EditText searchedLocation;
    String rec_title=null;
    double rec_lat,rec_lng;
    int rec_id;
    Bundle b;



    private static final float DEFAULTZOOM = 15;
    @SuppressWarnings("unused")
    private static final String LOGTAG = "Maps";

    LocationClient mLocationClient;
    Marker marker;
    SlidingDrawer sd;
    ProgressBar loadBusInfo;
    //WebView wb;
    String webSite="http://www.rtpi.ie/Popup_Content/WebDisplay/WebDisplay.aspx?stopRef=";
    ArrayList<String> realBusTimeInfo;
    ListView busInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (servicesOK()) {
            setContentView(R.layout.activity_map_long_geofence);
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


        go=(Button)findViewById(R.id.bLongGeoLocate);
        go.setVisibility(View.VISIBLE);

        setAlarm=(Button)findViewById(R.id.bSetLongGeofence);
        setAlarm.setVisibility(View.INVISIBLE);

        searchedLocation=(EditText)findViewById(R.id.etLongGeoLocate);
        searchedLocation.setVisibility(View.VISIBLE);

        radiusSeekbar=(SeekBar)findViewById(R.id.sbRadius);
        radiusSeekbar.setVisibility(View.INVISIBLE);

        radiusSeekbar.setMax(1000);
        radiusSeekbar.setProgress(50);
        radiusSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                displayRadius();
            }
        });

        radiusSwitch=(Switch)findViewById(R.id.swRadius);
        radiusSwitch.setVisibility(View.INVISIBLE);
        radiusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setRadius(radiusSeekbar.getProgress());
                displayRadius();
            }
        });

        //set space for radius tools
        mMap.setPadding(0,70,0,70);
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker clickedMarker) {
                clickedMarker.hideInfoWindow();

                return false;
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                radiusSwitch.setVisibility(View.VISIBLE);
                radiusSeekbar.setVisibility(View.VISIBLE);
                MarkerOptions options = new MarkerOptions()
                        .position(latLng);
                marker = mMap.addMarker(options);
                marker.setDraggable(true);
                circle=mMap.addCircle(new CircleOptions()
                        .center(latLng)
                        .radius(50)
                        .strokeColor(Color.WHITE)
                        .strokeWidth(3)
                        .fillColor(0x6F323299)

                );
                go.setVisibility(View.INVISIBLE);
                setAlarm.setVisibility(View.VISIBLE);
                searchedLocation.setVisibility(View.INVISIBLE);

            }

        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                circle.setCenter(marker.getPosition());
                circle.setStrokeWidth(5);
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                circle.setCenter(marker.getPosition());
                circle.setStrokeWidth(10);
                circle.setStrokeColor(Color.BLUE);
                circle.setFillColor(0x6F323299);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                circle.setCenter(marker.getPosition());
                circle.setStrokeWidth(3);
                circle.setStrokeColor(Color.WHITE);
            }
        });

        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle pass = new Bundle();
                if(b.getString("title",null)!=null){
                    pass.putString("title",b.getString("title"));
                }
                pass.putDouble("lat", marker.getPosition().latitude);
                pass.putDouble("lng", marker.getPosition().longitude);
                pass.putDouble("radius", circle.getRadius());
                Intent i = new Intent(MapLongGeofence.this,GeofenceConstruct.class);
                i.putExtras(pass);
                startActivity(i);
                finish();
            }
        });

        b = getIntent().getExtras();
        if (b!=null){
            previousAlarmReceived();
        }
    }

    private void previousAlarmReceived() {
        rec_id=b.getInt("id");
        rec_title=b.getString("title");
        rec_lat=b.getDouble("lat");
        rec_lng=b.getDouble("lng");
        LatLng latLng= new LatLng(rec_lat, rec_lng);
        mMap.clear();
        radiusSwitch.setVisibility(View.VISIBLE);
        radiusSeekbar.setVisibility(View.VISIBLE);
        MarkerOptions options = new MarkerOptions()
                .title(rec_title)
                .position(latLng);
        marker = mMap.addMarker(options);
        marker.setDraggable(true);
        circle=mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(50)
                .strokeColor(Color.WHITE)
                .strokeWidth(3)
                .fillColor(0x6F323299)

        );
        go.setVisibility(View.INVISIBLE);
        setAlarm.setVisibility(View.VISIBLE);
        searchedLocation.setVisibility(View.INVISIBLE);

    }

    private void displayRadius() {

        if(radiusSwitch.isChecked()){
            if(radiusSeekbar.getProgress()<50){
                Toast.makeText(getApplicationContext(),"Use meters instead of kilometers for a smaller radius",Toast.LENGTH_LONG).show();
                radiusSeekbar.setProgress(50);
            }else{
                Toast.makeText(getApplicationContext(),"Radius Distance: "+circle.getRadius()/1000+"Km",Toast.LENGTH_LONG).show();
            }
        }else{
            if(radiusSeekbar.getProgress()<50){
                Toast.makeText(getApplicationContext(),"Minimum Distance is 50m",Toast.LENGTH_LONG).show();
                radiusSeekbar.setProgress(50);
            }else{
                Toast.makeText(getApplicationContext(),"Radius Distance: "+circle.getRadius()+"m",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setRadius(int progress) {
        int tempRadius=0;
        if(radiusSwitch.isChecked()){
            tempRadius= progress*4;
            circle.setRadius(tempRadius);
        }else{
            tempRadius= progress;
            circle.setRadius(tempRadius);
        }
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
        String location = searchedLocation.getText().toString();
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

    protected void gotoCurrentLocation() {
        Location currentLocation = mLocationClient.getLastLocation();
        if (currentLocation == null) {
            Toast.makeText(this, "Current location isn't available", Toast.LENGTH_SHORT).show();
        }
        else {
            LatLng ll = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, DEFAULTZOOM);
            mMap.animateCamera(update);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
    }

    @Override
    public void onConnected(Bundle arg0) {
//		Toast.makeText(this, "Connected to location service", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected() {
    }




}
