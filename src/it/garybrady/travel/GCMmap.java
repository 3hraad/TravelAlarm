package it.garybrady.travel;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import it.garybrady.traveldata.myDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GCMmap extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final int GPS_ERRORDIALOG_REQUEST = 9001;
    @SuppressWarnings("unused")
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9002;
    GoogleMap mMap;
    EditText et;
    String webStopRef="http://192.3.177.209/liveInfo.php?RefNo=";
    String receivedAddress=null;
    myDatabase dba = new myDatabase(this);



    private static final float DEFAULTZOOM = 15;
    @SuppressWarnings("unused")
    private static final String LOGTAG = "Maps";

    LocationClient mLocationClient;
    Marker marker;
    SlidingDrawer sd;
    //WebView wb;
    String webSite="http://www.rtpi.ie/Popup_Content/WebDisplay/WebDisplay.aspx?stopRef=";
    ArrayList<String> realBusTimeInfo;
    ListView busInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            Toast.makeText(this, "shit yo, no gs!", Toast.LENGTH_SHORT).show();
        }
        sd = (SlidingDrawer) findViewById(R.id.slidingDrawer2);


        dba.open();
        receivedAddress=dba.getMostRecentAddress();
        dba.close();



        try {
            gcmGoto(receivedAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void gcmGoto(String address) throws IOException {

        et = (EditText) findViewById(R.id.etLongGeoLocate);
        String location = address.toString();
        if (location.length() == 0) {
            Toast.makeText(this, "No location Received", Toast.LENGTH_SHORT).show();
            return;
        }



        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);
        Address add = list.get(0);
        String locality = add.getLocality();
        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

        double lat = add.getLatitude();
        double lng = add.getLongitude();



        //Add a marker to searched location

		if(marker !=null){
			marker.remove();
		}

		MarkerOptions options = new MarkerOptions()
			.title(locality)
			.position(new LatLng(lat,lng));
		marker = mMap.addMarker(options);
        //gotoLocation(lat, lng, DEFAULTZOOM);

        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, DEFAULTZOOM);
        mMap.moveCamera(update);
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
        /*if (position != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            mMap.moveCamera(update);
            mMap.setMapType(mgr.getSavedMapType());
        }*/

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
