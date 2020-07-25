package com.example.kaamasaan;

        import androidx.annotation.NonNull;
        import androidx.appcompat.widget.AppCompatImageView;
        import androidx.core.app.ActivityCompat;
        import androidx.fragment.app.FragmentActivity;

        import android.Manifest;
        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.graphics.Color;
        import android.graphics.Typeface;
        import android.graphics.drawable.ColorDrawable;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.os.Bundle;
        import android.os.Handler;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.AdapterView;
        import android.widget.BaseAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.ListView;
        import android.widget.RatingBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.bumptech.glide.Glide;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.HashMap;

public class FoundSPMapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    public LocationManager lm;
    public static double latitude = 0.0;
    public static double longitude=0.0;
    int RequestCode = 10;
    public static ArrayList<ServiceProvider>alServiceProviders;
    boolean alServiceProvidersSizeGreaterThanZero = false;
    ProgressDialog mprogressdialog;
    Marker customerMarker;
    Typeface mfont;
    ArrayList<Marker> alMarkers = new ArrayList();
    ArrayList<Message>chatList = new ArrayList();
    Button viewInList;
    boolean doeschatHashmapOfCustomerExist;
    boolean  doeschatHashmapOfServiceProviderExist;
    String mMessage;
    ListView lv;
    ArrayList<WorkRating>alWorkRating;

    // PlaceAutocompleteFragment placeAutoComplete;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_s_p_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        viewInList = findViewById(R.id.btn_view_in_list);

        mprogressdialog = new ProgressDialog(this);
        mprogressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mprogressdialog.setIndeterminate(true);
        mprogressdialog.setCancelable(false);
        mprogressdialog.setMessage("Loading....");
        mfont = Typeface.createFromAsset(this.getAssets(), "fonts/KaushanScript-Regular.otf");
        viewInList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alServiceProvidersSizeGreaterThanZero==true){
                    Intent intent = new Intent(FoundSPMapActivity.this,FoundSPListActivity.class);
                    intent.putParcelableArrayListExtra("spArray",alServiceProviders);

                    // saving the changed/current location of customer on search map
                    // customerMarker.remove();
                    startActivity(intent);

                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

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
        // Toast.makeText(FoundSPMapActivity.this,"onMapReady",Toast.LENGTH_LONG).show();


        if(latitude==0.0||longitude==0.0){
            getcurrentLocation();
        }
        else{
            getcurrentLocation();
        }



        final Handler handler = new Handler();
        mprogressdialog.show();
       /* Window window = mprogressdialog.getWindow();
        window.setLayout(250, 200);*/

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                findServiceProviders(SearchServiceProviderActivity.selectedCategory);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        if(alServiceProvidersSizeGreaterThanZero==true){
                            for(int i=0;i<alServiceProviders.size();i++){
                                Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(alServiceProviders.get(i).getLatititude(),alServiceProviders.get(i).getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                marker.setPosition(new LatLng(alServiceProviders.get(i).getLatititude(),alServiceProviders.get(i).getLongitude()));
                                alMarkers.add(marker);


                            }
                            mprogressdialog.dismiss();
                        }
                        else{
                            mprogressdialog.dismiss();
                             Toast.makeText(FoundSPMapActivity.this,"No "+SearchServiceProviderActivity.selectedCategory+ " found in this area",Toast.LENGTH_LONG).show();

                        }

                    }
                }, 5000);





            }
        }, 5000);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getPosition().latitude==latitude&&marker.getPosition().longitude==longitude){
                    // do nothing if  marker is customer's location marker
                }
                else {
                    Dialog dialog= showCustomDialog(marker.getPosition().latitude,marker.getPosition().longitude);
                    dialog.show();
                }
                return false;
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lm.removeUpdates(this);
        // instantiate the class, LatLng
        LatLng latlng = new LatLng(latitude, longitude);

        customerMarker  = mMap.addMarker(new MarkerOptions().position(latlng).title("You").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        customerMarker.setPosition(latlng);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,10.2f));

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

    private void getcurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         //   Toast.makeText(FoundSPMapActivity.this,"permissions not granted",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(FoundSPMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RequestCode);


        }
        else{
            lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            //  Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);

            //   Toast.makeText(FoundSPMapActivity.this,"Location Update called",Toast.LENGTH_LONG).show();

            //  lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, locationListener);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Toast.makeText(FoundSPMapActivity.this,"onResume called",Toast.LENGTH_LONG).show();

        final Handler handler = new Handler();
        mprogressdialog.show();
        /*Window window = mprogressdialog.getWindow();
        window.setLayout(250, 200);
*/
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                findServiceProviders(SearchServiceProviderActivity.selectedCategory);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        if(alServiceProvidersSizeGreaterThanZero==true){
                            for(int i=0;i<alServiceProviders.size();i++){
                                Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(alServiceProviders.get(i).getLatititude(),alServiceProviders.get(i).getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                marker.setPosition(new LatLng(alServiceProviders.get(i).getLatititude(),alServiceProviders.get(i).getLongitude()));

                                alMarkers.add(marker);


                            }
                            mprogressdialog.dismiss();
                        }
                        else{
                            mprogressdialog.dismiss();
                           // Toast.makeText(FoundSPMapActivity.this,"No "+SearchServiceProviderActivity.selectedCategory+ " found in this area",Toast.LENGTH_LONG).show();

                        }

                    }
                }, 5000);





            }
        }, 5000);

    }

    GoogleMap.OnMarkerDragListener dragListener = new GoogleMap.OnMarkerDragListener() {
        @Override
        public void onMarkerDragStart(Marker marker) {
            for(int i=0;i<alMarkers.size();i++){
                alMarkers.get(i).remove();

            }
            alMarkers.clear();
            alServiceProvidersSizeGreaterThanZero=false;
        }

        @Override
        public void onMarkerDrag(Marker marker) {

        }

        @Override
        public void onMarkerDragEnd(Marker marker) {

            latitude = marker.getPosition().latitude;
            longitude = marker.getPosition().longitude;




            findServiceProviders(SearchServiceProviderActivity.selectedCategory);
            final Handler handler = new Handler();
            mprogressdialog.show();

            /*Window window = mprogressdialog.getWindow();
            window.setLayout(250, 200);*/

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms

                    if(alServiceProvidersSizeGreaterThanZero==true){
                        for(int i=0;i<alServiceProviders.size();i++){
                            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(alServiceProviders.get(i).getLatititude(),alServiceProviders.get(i).getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            marker.setPosition(new LatLng(alServiceProviders.get(i).getLatititude(),alServiceProviders.get(i).getLongitude()));
                            alMarkers.add(marker);
                        }
                        mprogressdialog.dismiss();
                    }
                    else{
                        mprogressdialog.dismiss();
                       Toast.makeText(FoundSPMapActivity.this,"No "+SearchServiceProviderActivity.selectedCategory+ " found in this area",Toast.LENGTH_LONG).show();
                    }

                }
            }, 5000);


        }
    };


    public void findServiceProviders(final String category){
        alServiceProviders = new ArrayList<>();
        final Location currentLocation = new Location("currentLocation");
        final Location sp_location = new Location("Marker");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    ServiceProvider serviceProvider = ssn.getValue(ServiceProvider.class);
                    if(serviceProvider.getCategory().equals(category)){
                        LatLng mlatLng = new LatLng(serviceProvider.getLatititude(),serviceProvider.getLongitude());


                        // getting current Location

                        currentLocation.setLatitude(latitude);
                        currentLocation.setLongitude(longitude);

                        // getting service provider Location
                        sp_location .setLatitude(mlatLng.latitude);
                        sp_location .setLongitude(mlatLng.longitude);
                        Float distance = currentLocation.distanceTo(sp_location );
                        //   Log.v("Distance in meters",distance+"");
                        if(distance<5001){
                            alServiceProviders.add(serviceProvider);

                        }






                    }
                    alServiceProvidersSizeGreaterThanZero = true;
                }
                if(alServiceProviders.size()>0){
                    alServiceProvidersSizeGreaterThanZero = true;
                }
                else{
                    alServiceProvidersSizeGreaterThanZero = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onPostResume() {

        super.onPostResume();
        //  Toast.makeText(FoundSPMapActivity.this,"OnPostResume is called",Toast.LENGTH_LONG).show();

    }



    private Dialog showCustomDialog(double lat, double longi){
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.found_sp_marker_dialog);
        dialog.setTitle("Details");



        final String imageUrl[]  = {null};

        final TextView name = dialog.findViewById(R.id.txt_name);
        TextView profession = dialog.findViewById(R.id.txt_profession);
        TextView bio = dialog.findViewById(R.id.txt_bio);
        TextView lbl_jobs =  dialog.findViewById(R.id.lbl_job);
        TextView lbl_rating =   dialog.findViewById(R.id.lbl_rating);
        Button cancel = dialog.findViewById(R.id.btn_cancel);
        Button visitProfile = dialog.findViewById(R.id.btn_profile);
        ImageButton sendMessage = dialog.findViewById(R.id.btn_chat);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        name.setTypeface(mfont);
        profession.setTypeface(mfont);
        bio.setTypeface(mfont);
        lbl_jobs.setTypeface(mfont);
        lbl_rating.setTypeface(mfont);
         int rating=0,noOfReviews=0, noOfJobs=0;
        for(int i=0;i<alServiceProviders.size();i++){
            if(lat==alServiceProviders.get(i).latititude &&longi==alServiceProviders.get(i).longitude){
                name.setText(alServiceProviders.get(i).getUserName());
                profession.setText(alServiceProviders.get(i).getCategory());
                bio.setText(alServiceProviders.get(i).getBio());
                imageUrl[0] = alServiceProviders.get(i).getProfilePicUrl();
                rating = (alServiceProviders.get(i).responseRating+alServiceProviders.get(i).getAverageWorkRating())/2;
                if(alServiceProviders.get(i).getWorkRatingList()!=null) {
                    noOfReviews = alServiceProviders.get(i).getWorkRatingList().size();
                }
                if(alServiceProviders.get(i).getCompletedWorkList()!=null) {
                    noOfJobs = alServiceProviders.get(i).getCompletedWorkList().size();
                }
                alWorkRating = alServiceProviders.get(i).getWorkRatingList();
            }
        }

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Toast.makeText(FoundSPMapActivity.this,name.getText().toString(),Toast.LENGTH_LONG).show();
                showChatDialog(name.getText().toString());

            }
        });

        final int finalRating = rating;
        final int finalNoOfReviews = noOfReviews;
        final int finalNoOfJobs = noOfJobs;
        visitProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              showProfileDialog(name.getText().toString(),imageUrl[0], finalRating, finalNoOfReviews, finalNoOfJobs,
                      alWorkRating);
            }
        });


        return dialog;
    }

    private void showProfileDialog(final String sp_UserName,String imageUrl,int rating,int noOfReviews,int noOfJobs,
                                   ArrayList<WorkRating>alWorkRating){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_serviceprovider_profile);
        dialog.setTitle(sp_UserName);
       // Toast.makeText(FoundSPMapActivity.this,sp_UserName,Toast.LENGTH_LONG).show();
        AppCompatImageView iv = dialog.findViewById(R.id.iv_prof_pic);
        TextView tv = dialog.findViewById(R.id.txt_name_dialog);
        TextView txt_reviews = dialog.findViewById(R.id.txt_reviews);
        TextView txt_jobs = dialog.findViewById(R.id.txt_jobs);
        RatingBar ratingBar1,ratingBar2,ratingBar3,ratingBar4,ratingBar5;
        ListView lv_reviews = dialog.findViewById(R.id.lv_reviews);
        txt_reviews.setText("Reviews:"+noOfReviews);
        txt_jobs.setText("Jobs:"+noOfJobs);
        ratingBar1 = dialog.findViewById(R.id.ratingbar1);
        ratingBar2 = dialog.findViewById(R.id.ratingbar2);
        ratingBar3 =dialog.findViewById(R.id.ratingbar3);
        ratingBar4 = dialog.findViewById(R.id.ratingbar4);
        ratingBar5 = dialog.findViewById(R.id.ratingbar5);
        tv.setText(sp_UserName);
        if(rating==1){
            ratingBar1.setRating(rating);
        }
        else if(rating==2){
            ratingBar2.setRating(rating);
        }
        else if(rating==3){
            ratingBar3.setRating(rating);
        }
        else if(rating==4){
            ratingBar4.setRating(rating);
        }
        else if(rating==5){
            ratingBar5.setRating(rating);
        }

        if(alWorkRating!=null){
            ReviewsAdapter reviewsAdapter = new ReviewsAdapter();
            lv_reviews.setAdapter(reviewsAdapter);
        }
        Glide.with(getApplicationContext()).load(imageUrl).into(iv);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }


    private void showChatDialog(final String sp_UserName){
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.message_dialog);
        dialog.setTitle(sp_UserName);

      lv = dialog.findViewById(R.id.lv_chats);
        Button send = dialog.findViewById(R.id.btn_send);
        Button cancel = dialog.findViewById(R.id.btn_cancel);
        final EditText ed_messge = dialog.findViewById(R.id.ed_message);


        if( doesChatHashmapOfCustomerExist()==true) {
          //  Toast.makeText(FoundSPMapActivity.this, "Chatmap of customer exists ", Toast.LENGTH_LONG).show();

                    if (doesChatListWithThisServiceProviderExistInHasmapOfCustomer(sp_UserName) == true) {
                      //  Toast.makeText(FoundSPMapActivity.this, "ChatList With This ServiceProvider Exists In Hashmap Of Customer ", Toast.LENGTH_LONG).show();
                        HashMap hashMap = MainActivity.mcustomer.getChatHashmap();
                        ArrayList<Message> alMessage = (ArrayList<Message>) hashMap.get(sp_UserName);
                        chatList = alMessage;
                        CustomAdapter customAdapter = new CustomAdapter();
                        lv.setAdapter(customAdapter);
                        lv.setSelection(customAdapter.getCount()-1);
                    }
                }
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(FoundSPMapActivity.this);
                alert.setTitle("Alert!!");
                alert.setMessage("Are you sure to delete message?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Yes Pressed

                        chatList.remove(position);

                        CustomAdapter customAdapterChatList = new CustomAdapter();
                        lv.setAdapter(customAdapterChatList);
                        lv.setSelection(customAdapterChatList.getCount()-1);

                        HashMap hashMap = MainActivity.mcustomer.getChatHashmap();
                        hashMap.put(sp_UserName,chatList);
                        MainActivity.mcustomer.setChatHashmap(hashMap);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(MainActivity.mcustomer.getId());
                        ref.setValue(MainActivity.mcustomer);
                        dialog.dismiss();

                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });


                alert.show();
                return true;
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ed_messge.getText().toString().isEmpty()) {
                    mMessage = ed_messge.getText().toString();
                    ed_messge.setText("");
                    if (doesChatHashmapOfCustomerExist() == true) {

                        if (doesChatListWithThisServiceProviderExistInHasmapOfCustomer(sp_UserName) == true) {
                          //  Toast.makeText(FoundSPMapActivity.this, "ChatList With This ServiceProvider Exists In Hashmap Of Customer ", Toast.LENGTH_LONG).show();
                            HashMap hashMap = MainActivity.mcustomer.getChatHashmap();
                            ArrayList<Message> alMessage = (ArrayList<Message>) hashMap.get(sp_UserName);


                            Message message1 = new Message("You", mMessage,getCurrentDate());

                            alMessage.add(message1);
                            chatList = alMessage;
                            CustomAdapter customAdapter = new CustomAdapter();
                            lv.setAdapter(customAdapter);
                            lv.setSelection(customAdapter.getCount() - 1);
                          //  Toast.makeText(FoundSPMapActivity.this, chatList.size() + "", Toast.LENGTH_LONG).show();

                            hashMap.put(sp_UserName, alMessage);

                            MainActivity.mcustomer.setChatHashmap(hashMap);
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(MainActivity.mcustomer.getId());
                            ref.setValue(MainActivity.mcustomer);
                            //   MainActivity.mcustomer = customer;

                            // now manipulate Service Provider
                            final String spID = getIDOfServiceProvider(sp_UserName);
                            doeschatHashmapOfServiceProviderExist(spID);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                    if (doeschatHashmapOfServiceProviderExist == true) {

                                        ServiceProvider serviceProvider = getServiceProvider(spID);
                                        HashMap<String, ArrayList<Message>> map = serviceProvider.getChatHashmap();
                                        ArrayList<Message> almsg = map.get(MainActivity.mcustomer.getUserName());

                                        if (almsg != null) {
                                         //   Toast.makeText(FoundSPMapActivity.this, "size of message array is null", Toast.LENGTH_LONG).show();

                                            Message message = new Message(MainActivity.mcustomer.getUserName(), mMessage,getCurrentDate());
                                            almsg.add(message);
                                           // Toast.makeText(FoundSPMapActivity.this, "size of message array after updating is: " + almsg.size(), Toast.LENGTH_LONG).show();
                                            map.put(MainActivity.mcustomer.getUserName(), almsg);

                                             serviceProvider.setChatHashmap(map);
                                            final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(spID);
                                            sp_ref.setValue(serviceProvider);


                                        } else {//i-e arrayList is null
                                         //   Toast.makeText(FoundSPMapActivity.this, "array is null ", Toast.LENGTH_LONG).show();
                                            almsg = new ArrayList<>();
                                            Message message = new Message(MainActivity.mcustomer.getUserName(), mMessage,getCurrentDate());
                                            almsg.add(message);
                                            map.put(MainActivity.mcustomer.getUserName(), almsg);

                                            serviceProvider.setChatHashmap(map);
                                            final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(spID);
                                            sp_ref.setValue(serviceProvider);

                                        }
                                    } else if (doeschatHashmapOfServiceProviderExist == false) {
                                        ServiceProvider serviceProvider = getServiceProvider(spID);
                                        HashMap<String, ArrayList<Message>> map = new HashMap<>();
                                        Message message = new Message(MainActivity.mcustomer.getUserName(), mMessage,getCurrentDate());
                                        ArrayList<Message> almsg = new ArrayList();
                                        almsg.add(message);
                                        map.put(MainActivity.mcustomer.getUserName(), almsg);

                                         serviceProvider.setChatHashmap(map);
                                        final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(spID);
                                        sp_ref.setValue(serviceProvider);

                                    }// end manipulation of service provider

                                }
                            }, 5000);


                        }// end if i-e doesChatListWithThisServiceProviderExistInHasmapOfCustomer(sp_UserName)== true

                        else if (doesChatListWithThisServiceProviderExistInHasmapOfCustomer(sp_UserName) == false) {
                           // Toast.makeText(FoundSPMapActivity.this, "declaration of array", Toast.LENGTH_LONG).show();
                            ArrayList<Message> alMessage = new ArrayList<>();
                            Message message1 = new Message("You", mMessage,getCurrentDate());
                            alMessage.add(message1);
                            chatList = alMessage;
                            CustomAdapter customAdapter = new CustomAdapter();
                            lv.setAdapter(customAdapter);
                            HashMap hashMap = MainActivity.mcustomer.getChatHashmap();
                            hashMap.put(sp_UserName, alMessage);
                            Customer customer = new Customer(MainActivity.mcustomer.getId(), MainActivity.mcustomer.getFullName(),
                                    MainActivity.mcustomer.getUserName(), MainActivity.mcustomer.getPhone(),
                                    MainActivity.mcustomer.getPassword(), MainActivity.mcustomer.getImageUrl(),
                                    MainActivity.mcustomer.getBroadCastRequestsIdsList(), hashMap,MainActivity.mcustomer.getReceivedVisitRequestList(),
                                    MainActivity.mcustomer.getPendingVisitsList(),MainActivity.mcustomer.getCompletedVisitsList()
                                    ,MainActivity.mcustomer.getResponseTimeRatingList(), MainActivity.mcustomer.getReceivedWorkRequestList(), MainActivity.mcustomer.getPendingWorkList(),
                                    MainActivity.mcustomer.completedWorkList,MainActivity.mcustomer.workRatingList,MainActivity.mcustomer.responseRating,
                                    MainActivity.mcustomer.averageWorkRating);
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(MainActivity.mcustomer.getId());
                            ref.setValue(customer);
                            MainActivity.mcustomer = customer;


                            // now manipulate Service Provider
                            String spID = getIDOfServiceProvider(sp_UserName);
                            if (doeschatHashmapOfServiceProviderExist == true) {
                                ServiceProvider serviceProvider = getServiceProvider(spID);
                                HashMap<String, ArrayList<Message>> map = serviceProvider.getChatHashmap();
                                ArrayList<Message> almsg = map.get(MainActivity.mcustomer.getUserName());
                                if (almsg != null) {
                                    Message message = new Message(MainActivity.mcustomer.getUserName(), mMessage,getCurrentDate());
                                    almsg.add(message);
                                    almsg.add(message);
                                    map.put(MainActivity.mcustomer.getUserName(), almsg);
                                    serviceProvider.setChatHashmap(map);


                                    final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(spID);
                                    sp_ref.setValue(serviceProvider);

                                } else {//i-e arrayList is null
                                    almsg = new ArrayList<>();
                                    Message message = new Message(MainActivity.mcustomer.getUserName(), mMessage,getCurrentDate());
                                    almsg.add(message);
                                    map.put(MainActivity.mcustomer.getUserName(), almsg);
                                    serviceProvider.setChatHashmap(map);

                                    final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(spID);
                                    sp_ref.setValue(serviceProvider);
                                }
                            } else if (doeschatHashmapOfServiceProviderExist == false) {
                                ServiceProvider serviceProvider = getServiceProvider(spID);
                                HashMap<String, ArrayList<Message>> map = new HashMap<>();
                                Message message = new Message(MainActivity.mcustomer.getUserName(), mMessage,getCurrentDate());
                                ArrayList<Message> almsg = new ArrayList();
                                almsg.add(message);
                                map.put(MainActivity.mcustomer.getUserName(), almsg);
                                serviceProvider.setChatHashmap(map);

                                final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(spID);
                                sp_ref.setValue(serviceProvider);

                            }// end manipulation of service provider

                        }
                    } else if (doesChatHashmapOfCustomerExist() == false) {
                       // Toast.makeText(FoundSPMapActivity.this, "false else if part", Toast.LENGTH_LONG).show();
                        HashMap<String, ArrayList<Message>> map = new HashMap<>();
                        ArrayList<Message> almsg = new ArrayList<>();
                        Message message = new Message("You",mMessage,getCurrentDate());
                        almsg.add(message);
                        chatList = almsg;
                        CustomAdapter customAdapter = new CustomAdapter();
                        lv.setAdapter(customAdapter);

                        map.put(sp_UserName, almsg);
                        MainActivity.mcustomer.setChatHashmap(map);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(MainActivity.mcustomer.getId());
                        ref.setValue(MainActivity.mcustomer);


                        // now manipulate Service Provider
                        String spID = getIDOfServiceProvider(sp_UserName);
                        if (doeschatHashmapOfServiceProviderExist == true) {
                            ServiceProvider serviceProvider = getServiceProvider(spID);
                            HashMap<String, ArrayList<Message>> hashmap = serviceProvider.getChatHashmap();
                            ArrayList<Message> almessage = map.get(MainActivity.mcustomer.getUserName());
                            if (almessage != null) {
                                Message msg = new Message(MainActivity.mcustomer.getUserName(), mMessage,getCurrentDate());
                                almessage.add(msg);

                                hashmap.put(MainActivity.mcustomer.getUserName(), almessage);
                                serviceProvider.setChatHashmap(hashmap);


                                final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(spID);
                                sp_ref.setValue(serviceProvider);

                            } else {//i-e arrayList is null
                                almessage = new ArrayList<>();
                                Message message1 = new Message(MainActivity.mcustomer.getUserName(), mMessage,getCurrentDate());
                                almessage.add(message1);
                                map.put(MainActivity.mcustomer.getUserName(), almessage);
                                serviceProvider.setChatHashmap(map);

                                final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(spID);
                                sp_ref.setValue(serviceProvider);
                            }
                        } else if (doeschatHashmapOfServiceProviderExist == false) {
                            ServiceProvider serviceProvider = getServiceProvider(spID);
                            HashMap<String, ArrayList<Message>> hashmap = new HashMap<>();
                            Message message1 = new Message(MainActivity.mcustomer.getUserName(), mMessage,getCurrentDate());
                            ArrayList<Message> almessage = new ArrayList();
                            almessage.add(message1);
                            hashmap.put(MainActivity.mcustomer.getUserName(), almessage);
                            serviceProvider.setChatHashmap(hashmap);

                            final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(spID);
                            sp_ref.setValue(serviceProvider);

                        }// end manipulation of service provider


                    }

                }
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_messge.setText("");








            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        getLatestMessageFromServiceProider(sp_UserName);


    }




// check wether hashmap of Chat already exist in Customer Object or not
    public void doeschatHashmapOfCustomerExist(String customerId){
        final boolean[] bool = {false};
        final DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(customerId);
        customer_ref.orderByKey().equalTo("chatHashmap").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    doeschatHashmapOfCustomerExist = true;
                }
                else{
                    doeschatHashmapOfCustomerExist = false;
                }

                customer_ref.orderByKey().equalTo("chatHashmap").removeEventListener(this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       // Toast.makeText(FoundSPMapActivity.this,"checking hashMap existence of customer",Toast.LENGTH_LONG).show();


    }

    private boolean doesChatHashmapOfCustomerExist(){
        boolean bool = false;
        HashMap<String,ArrayList<Message>> map = MainActivity.mcustomer.getChatHashmap();
        if(map==null){
            bool =false;
        }
        else{
            bool = true;
        }
        return bool;
    }


    private boolean doesChatListWithThisServiceProviderExistInHasmapOfCustomer(String sp_UserName){
        final boolean[] bool = {false};
        HashMap<String,ArrayList<Message>> map = MainActivity.mcustomer.getChatHashmap();
        ArrayList<Message>alMessage = map.get(sp_UserName);
        if(alMessage==null){
         bool[0]=false;
        }
        else{
            bool[0]=true;
        }
      //  Toast.makeText(FoundSPMapActivity.this,"checking chat array existence of customer",Toast.LENGTH_LONG).show();
        return bool[0];
    }

    public void doeschatHashmapOfServiceProviderExist(String spID){

        final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(spID);
        sp_ref.orderByKey().equalTo("chatHashmap").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                     doeschatHashmapOfServiceProviderExist=true;
                   // Toast.makeText(FoundSPMapActivity.this,"hashmap of service provider exists ",Toast.LENGTH_LONG).show();
                }
                else{
                    doeschatHashmapOfServiceProviderExist=false;
                   // Toast.makeText(FoundSPMapActivity.this,"hashmap of service provider does not exist ",Toast.LENGTH_LONG).show();
                }
                sp_ref.orderByKey().equalTo("chatHashmap").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private boolean doesChatListWithThisCustomerExistInHasmapOfServiceProvider(){
        final boolean[] bool = {false};
        return bool[0];
    }




    
    private String getIDOfServiceProvider(String sp_UserName){
        String spID="";
        for(int i = 0;i<alServiceProviders.size();i++){
            if(sp_UserName.equals(alServiceProviders.get(i).getUserName())){
                spID = alServiceProviders.get(i).getId();
            }
        }
        return spID;
    }

    private ServiceProvider getServiceProvider(String spID){
        ServiceProvider serviceProvider = new ServiceProvider();
                      for(int i = 0;i<alServiceProviders.size();i++) {
                          if (spID.equals(alServiceProviders.get(i).getId())) {
                              serviceProvider = alServiceProviders.get(i);
                          }
                      }
                  return serviceProvider ;
                    }



    class CustomAdapter extends BaseAdapter {
        public static final int you =0;
        public static  final int serviceprovider = 1;

        @Override
        public int getCount() {
            return chatList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(getItemViewType(position)==you) {
                convertView = getLayoutInflater().inflate(R.layout.chat_list_row_my_side, null);
            }
            else if(getItemViewType(position)==serviceprovider){
                convertView = getLayoutInflater().inflate(R.layout.chat_list_row, null);
            }


           TextView txt_msg =convertView.findViewById(R.id.txt_message);
            TextView txt_date_time = convertView.findViewById(R.id.txt_date_time);



            txt_msg.setText(chatList.get(position).getMsg());
            txt_date_time.setText(chatList.get(position).getCurrentDate());




            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            Message message = chatList.get(position);
            if(message.getUserName().equals("You")){
                return you;
            }
            else {
                return serviceprovider;
            }

        }

    }

    public String getCurrentDate(){
        long date = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss:a");
        String formattedCurrentDate = df.format(date);

        return formattedCurrentDate;
    }

    public void getLatestMessageFromServiceProider(final String sp_UserName){
        final HashMap<String, ArrayList<Message>>[] map = new HashMap[1];

        final DatabaseReference chat_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
        chat_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    Customer customer = ssn.getValue(Customer.class);
                    if(customer.getId().equals(MainActivity.mcustomer.getId())){
                        map[0] = customer.getChatHashmap();
                        if(map[0] ==null){
                            // do nothing
                            //   Toast.makeText(getActivity(),"map is null",Toast.LENGTH_LONG).show();
                        }
                        else{
                            MainActivity.mcustomer.setChatHashmap(map[0]);
                            chatList = map[0].get(sp_UserName);
                            //  Toast.makeText(getActivity(),"chat List is not cleared"+chatList.size(),Toast.LENGTH_LONG).show();

                        }
                    }
                }// end for loop

                //   Toast.makeText(getActivity(),"size of chatlist in OnChange:   "+chatList.size(),Toast.LENGTH_LONG).show();
                if(chatList!=null) {

                    CustomAdapter customAdapter = new CustomAdapter();
                    lv.setAdapter(customAdapter);
                    lv.setSelection(customAdapter.getCount() - 1);
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class ReviewsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return alWorkRating.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.review_list_row, null);
            AppCompatImageView iv = convertView.findViewById(R.id.iv);
            TextView txt_name = convertView.findViewById(R.id.txt_username);
            final TextView txt_review = convertView.findViewById(R.id.txt_review);
            RatingBar ratingBar = convertView.findViewById(R.id.ratingbar);



            txt_name.setText(alWorkRating.get(position).getUserNameOfUser());
            txt_review.setText(alWorkRating.get(position).getReview());
            ratingBar.setRating(alWorkRating.get(position).getRating());
            Glide.with(FoundSPMapActivity.this).load(alWorkRating.get(position).getImageUrlOfUser()).into(iv);



            return convertView;

        }
    }

    public void showReviewDetailsDialog(int position){
        View view =  getLayoutInflater().inflate(R.layout.review_detail_dialog,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(FoundSPMapActivity.this,android.R.style.Theme_Holo_Dialog_NoActionBar);
        final AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setView(view,0,0,0,0);

        AppCompatImageView iv =  view.findViewById(R.id.iv);
        TextView txt_name=  view.findViewById(R.id.txt_username);
        final RatingBar ratingBar = view.findViewById(R.id.ratingbar);
        TextView txt_review=  view.findViewById(R.id.txt_review);
        ImageButton button_close =  view.findViewById(R.id.btn_close);

        txt_name.setText(alWorkRating.get(position).getUserNameOfUser());
        txt_review.setText(alWorkRating.get(position).getReview());
        ratingBar.setRating(alWorkRating.get(position).getRating());
        Glide.with(FoundSPMapActivity.this).load(alWorkRating.get(position).getImageUrlOfUser()).into(iv);

        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });



        alertDialog.show();


    }
}
