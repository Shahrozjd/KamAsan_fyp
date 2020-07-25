package com.example.kaamasaan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.IOException;
import java.util.List;

public class MapsActivitySPSignup extends FragmentActivity implements OnMapReadyCallback, LocationListener , GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    public LocationManager lm;
    double latitude = 0.0,longitude=0.0;
    int RequestCode = 10;
    Button btn_save,btn_cancel;
    //AutocompleteSupportFragment autocompleteFragment;

  //  PlaceAutocompleteFragment autocompleteFragment1;
    AutoCompleteTextView mSearchView;

    private GoogleApiClient googleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40,-68),new LatLng(71,136));
    androidx.appcompat.widget.SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_s_p_signup);
      //  Toast.makeText(MapsActivitySPSignup.this,"OnCreate Called",Toast.LENGTH_LONG).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn_save = findViewById(R.id.btn_save);
        btn_cancel = findViewById(R.id.btn_cancel);
      //  searchView = findViewById(R.id.searchview);











        // mSearchView = findViewById(R.id.tv_search_places);


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceProviderSignUpPart1Activity.latitude = latitude;
                ServiceProviderSignUpPart1Activity.longitude = longitude;
                onBackPressed();
            }
        });

        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm.getActiveNetworkInfo()== null) {
            Toast.makeText(MapsActivitySPSignup.this," Kindly Turn on your internet Connection",Toast.LENGTH_LONG).show();
        }
        else if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessageNoGPS();
        }
        else{
            getcurrentLocation();
          //  LatLng latlng = new LatLng(latitude,longitude);
           /* mMap.clear();
           Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title("Current").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
           marker.setPosition(latlng);
           mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));*/

        }


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       mMap.setOnMarkerDragListener(dragListener);

        if(ServiceProviderSignUpPart1Activity.latitude==0.0||ServiceProviderSignUpPart1Activity.longitude==0.0){
            getcurrentLocation();

          //   Toast.makeText(MapsActivitySPSignup.this," statics latlng are zero",Toast.LENGTH_LONG).show();
           /*
            LatLng currentLocation  = new LatLng(latitude,longitude);

            mMap.addMarker(new MarkerOptions().position(currentLocation).title("You").draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,10.2f));*/
        }

        else{
           lm.removeUpdates(this);
            // these latitude and longitude saves the current location and will be returned if Cancel button is pressed
            latitude = ServiceProviderSignUpPart1Activity.latitude;
            longitude=ServiceProviderSignUpPart1Activity.longitude;

         //   Toast.makeText(MapsActivitySPSignup.this," statics latlng are not zero",Toast.LENGTH_LONG).show();
            LatLng changedLocation  = new LatLng(ServiceProviderSignUpPart1Activity.latitude,ServiceProviderSignUpPart1Activity.longitude);
            mMap.addMarker(new MarkerOptions().position(changedLocation).title("You").draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(changedLocation,10.2f));
        }





    }


    protected void buildAlertMessageNoGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please turn on your location")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    private void getcurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivitySPSignup.this,"permissions not granted",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(MapsActivitySPSignup.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RequestCode);


        }
        else{
            lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
          //  Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);

          //  Toast.makeText(MapsActivitySPSignup.this,"Location Update called",Toast.LENGTH_LONG).show();

            //  lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, locationListener);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==RequestCode){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

             //   Toast.makeText(MapsActivitySPSignup.this,"Permission Granted, Now you can access location data.",Toast.LENGTH_LONG).show();
                getcurrentLocation();
            } else {

                Toast.makeText(MapsActivitySPSignup.this,"Permission denied,  You cannot access location data.",Toast.LENGTH_LONG).show();


            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
      //  Toast.makeText(this,"In onLocationChanged",Toast.LENGTH_SHORT).show();


        // these latitude and longitude saves the current location and will be returned if Cancel button is pressed
        latitude = ServiceProviderSignUpPart1Activity.latitude;
        longitude =  ServiceProviderSignUpPart1Activity.longitude;


         ServiceProviderSignUpPart1Activity.latitude = location.getLatitude();
         ServiceProviderSignUpPart1Activity.longitude = location.getLongitude();
         lm.removeUpdates(this);
        // instantiate the class, LatLng
        LatLng latlng = new LatLng( ServiceProviderSignUpPart1Activity.latitude,  ServiceProviderSignUpPart1Activity.longitude);

        Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title("You").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        marker.setPosition(latlng);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,10.2f));


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getApplicationContext(),"Location is Turned on",Toast.LENGTH_LONG).show();

        LatLng latlng = new LatLng(latitude, longitude);

        Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title("").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        marker.setPosition(latlng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,10.2f));
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getApplicationContext(),"Kindly turn on the location to use location services",Toast.LENGTH_LONG).show();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    GoogleMap.OnMarkerDragListener dragListener = new GoogleMap.OnMarkerDragListener() {
        @Override
        public void onMarkerDragStart(Marker marker) {

        }

        @Override
        public void onMarkerDrag(Marker marker) {

        }

        @Override
        public void onMarkerDragEnd(Marker marker) {
          //  Toast.makeText(getApplicationContext(),"lat: " +marker.getPosition().latitude+"   lat:"+ marker.getPosition().longitude,Toast.LENGTH_LONG).show();
            ServiceProviderSignUpPart1Activity.latitude = marker.getPosition().latitude;
            ServiceProviderSignUpPart1Activity.longitude = marker.getPosition().longitude;

        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
