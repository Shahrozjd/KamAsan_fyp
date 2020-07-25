package com.example.kaamasaan;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivityBroadCast extends FragmentActivity implements OnMapReadyCallback , LocationListener {

    private GoogleMap mMap;
    double lat, lng;

    Button btn_save,btn_cancel;
    int RequestCode = 10;
    public LocationManager lm;
    ProgressDialog mprogressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_broad_cast);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn_save = findViewById(R.id.btn_save);
        btn_cancel = findViewById(R.id.btn_cancel);
        mprogressdialog = new ProgressDialog(this);
        mprogressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mprogressdialog.setMessage("Loading...");
        mprogressdialog.setIndeterminate(true);
        mprogressdialog.setCancelable(false);
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat",0.0);
        lng = intent.getDoubleExtra("lng",0.0);




        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.putExtra("lat",lat);
                intent1.putExtra("lng",lng);
                setResult(RESULT_OK,intent1);
                finish();
            }
        });


    }


    private void getcurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"permissions not granted",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(MapsActivityBroadCast.this ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RequestCode);


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

                Toast.makeText(getApplicationContext(),"Permission denied,  You cannot access location data.",Toast.LENGTH_LONG).show();


            }
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
        if(lat==0.0||lng==0.0){
            getcurrentLocation();
        }
        else{
             LatLng latLng = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions().position(latLng).title("You").draggable(true));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.2f));

        }

        // Add a marker in Sydney and move the camera






    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        lm.removeUpdates(this);
         mprogressdialog.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                LatLng latLng  = new LatLng(lat,lng);
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("You").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                marker.setPosition(latLng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10.2f));
                mprogressdialog.dismiss();
            }
        }, 5000);




    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
            //  ServiceProviderSignUpPart1Activity.latitude = marker.getPosition().latitude;
            //   ServiceProviderSignUpPart1Activity.longitude = marker.getPosition().longitude;
            lat = marker.getPosition().latitude;
            lng = marker.getPosition().longitude;








        }
    };
}
