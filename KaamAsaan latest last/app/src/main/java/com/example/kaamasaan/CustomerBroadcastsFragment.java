package com.example.kaamasaan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerBroadcastsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerBroadcastsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    int REQ_Code = 100;
    FirebaseDatabase database;
    DatabaseReference broadcast_ref;
    DatabaseReference cat_ref;
    DatabaseReference customer_ref;
    EditText ed_title, ed_description;
    TextView tv_lat, tv_lng;
    ListView lv;
    Button btn_changeLocation, btn_uploadimages;
    public LocationManager lm;
    ConnectivityManager cm;
    ArrayList<String> al_cat = new ArrayList();
    ArrayAdapter<String> arrayAdapter;
    LinearLayout sv;
    String selectedCategory = "";
    ProgressDialog mprogressdialog;
    String broadCastId;
    com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar seekbar;
    ImageButton btnPic1, btnPic2, btnPic3,btnPic4, btnPic5, btnPic6;
    ImageButton btnRem1, btnRem2, btnRem3,btnRem4, btnRem5, btnRem6;
    Spinner spinner_category;
    androidx.appcompat.widget.AppCompatImageView iv1, iv2, iv3,iv4,iv5,iv6;
    private static final int SELECT_FROM_GALLERY_Pic1 = 1;
    private static final int SELECT_FROM_GALLERY_Pic2 = 2;
    private static final int SELECT_FROM_GALLERY_Pic3 = 3;
    private static final int SELECT_FROM_GALLERY_Pic4 = 4;
    private static final int SELECT_FROM_GALLERY_Pic5 = 5;
    private static final int SELECT_FROM_GALLERY_Pic6 = 6;

    Uri capturedImageUri1, capturedImageUri2, capturedImageUri3,capturedImageUri4, capturedImageUri5, capturedImageUri6;
    double latitude = 0.0, longitude=0.0;
    RatingBar ratingBar;
    double minVisitCost,maxVisitCost;


    HashMap<String, Uri> picHashMap = new HashMap<>();
    StorageReference mStorageRef;
    ArrayList<String> keysList = new ArrayList();
    ArrayList<String>downloadedImageUrls = new ArrayList();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerBroadcastsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerBroadcastsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerBroadcastsFragment newInstance(String param1, String param2) {
        CustomerBroadcastsFragment fragment = new CustomerBroadcastsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        database = FirebaseDatabase.getInstance();
        broadcast_ref = database.getReference("Broadcasts");
        cat_ref = database.getReference("Categories");
        customer_ref = database.getReference("Users").child("Customers");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        mprogressdialog = new ProgressDialog(getActivity());
        mprogressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mprogressdialog.setMessage("Sending Broadcast...");
        mprogressdialog.setIndeterminate(true);
        mprogressdialog.setCancelable(false);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_customer_broadcasts, container, false);
        sv = view.findViewById(R.id.item_scrollvw);
        ed_title = view.findViewById(R.id.ed_title);
        ed_description = view.findViewById(R.id.ed_description);
        ratingBar = view.findViewById(R.id.rating_bar);

        spinner_category = view.findViewById(R.id.spinner_category);
        al_cat.add("Select Category");

        btn_changeLocation = view.findViewById(R.id.btn_chooselocation);
        btn_uploadimages = view.findViewById(R.id.btn_uploadimages);


        seekbar = view.findViewById(R.id.rangeSeekbar);
        seekbar.setRangeValues(100, 2500);

spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if( parent.getItemAtPosition(position).toString().equals("Select Category"))  {
            // do nothing i-e selectedCategory = ""
        }
        else {
            selectedCategory = parent.getItemAtPosition(position).toString();


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
});


        getAllCategories();
       /* tv_lat.setText(String.valueOf(0.0));
        tv_lng.setText(String.valueOf(0.0));*/


        seekbar .setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                //Now you have the minValue and maxValue of your RangeSeekbar
                Toast.makeText(getActivity(), minValue + "-" + maxValue, Toast.LENGTH_LONG).show();
                minVisitCost = (double)minValue;
                maxVisitCost = (double)maxValue;
            }
        });

// Get noticed while dragging
        seekbar .setNotifyWhileDragging(true);


        btn_changeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cm.getActiveNetworkInfo() == null) {
                    Toast.makeText(getActivity(), "Connect to internet please", Toast.LENGTH_LONG).show();

                } else if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    buildAlertMessageNoGPS();
                } else {
                    Intent intent = new Intent(getActivity(), MapsActivityBroadCast.class);
                    intent.putExtra("lat", latitude);
                    intent.putExtra("lng",longitude);
                    startActivityForResult(intent, REQ_Code);


                }
            }
        });



        sv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ed_description.getParent().requestDisallowInterceptTouchEvent(false);

                return false;
            }
        });

        ed_description.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ed_description.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });




        btn_uploadimages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageUploadDialog();
            }
        });


        return view;
    }

    protected void buildAlertMessageNoGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    private void getAllCategories() {
        cat_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ssn : dataSnapshot.getChildren()) {
                    Category category = ssn.getValue(Category.class);
                    al_cat.add(category.getCategory());

                }


                ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,al_cat);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_category.setAdapter(adapter);
                arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_checked, al_cat);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_Code) {
            if (resultCode == RESULT_OK) {
               /* tv_lat.setText(String.valueOf(data.getDoubleExtra("lat", 0.0)));
                tv_lng.setText(String.valueOf(data.getDoubleExtra("lng", 0.0)));*/
                latitude =data.getDoubleExtra("lat", 0.0);
                longitude =data.getDoubleExtra("lng", 0.0);
            }
        }
        else if(requestCode==SELECT_FROM_GALLERY_Pic1){
            if (resultCode == RESULT_OK) {
                capturedImageUri1 = data.getData();
                if (capturedImageUri1 != null) {

                  //  Toast.makeText(getActivity(), capturedImageUri1.toString(), Toast.LENGTH_LONG).show();
                     Glide.with(getActivity()).load(capturedImageUri1).into(iv1);
                    picHashMap.put("pic1",capturedImageUri1);


                }
                else{
                  //  Toast.makeText(getActivity(), "imageUri is null", Toast.LENGTH_LONG).show();

                }
            }
        }else if(requestCode==SELECT_FROM_GALLERY_Pic2){
            if (resultCode == RESULT_OK) {
                capturedImageUri2 = data.getData();
                if (capturedImageUri2 != null) {

                //    Toast.makeText(getActivity(), capturedImageUri2.toString(), Toast.LENGTH_LONG).show();

                    Glide.with(getActivity()).load(capturedImageUri2).into(iv2);
                    picHashMap.put("pic2",capturedImageUri2);
                  //  Toast.makeText(getActivity(),"size of hashmap: "+picHashMap.size(),Toast.LENGTH_LONG).show();


                } else {
                 //   Toast.makeText(getActivity(), "imageUri is null", Toast.LENGTH_LONG).show();

                }
            }
        }
        else if(requestCode==SELECT_FROM_GALLERY_Pic3) {
            if (resultCode == RESULT_OK) {
                capturedImageUri3 = data.getData();
                if (capturedImageUri3 != null) {

                 //   Toast.makeText(getActivity(), capturedImageUri3.toString(), Toast.LENGTH_LONG).show();

                    Glide.with(getActivity()).load(capturedImageUri3).into(iv3);
                    picHashMap.put("pic3",capturedImageUri3);
                  //  Toast.makeText(getActivity(),"size of hashmap: "+picHashMap.size(),Toast.LENGTH_LONG).show();


                } else {
                //    Toast.makeText(getActivity(), "imageUri is null", Toast.LENGTH_LONG).show();

                }
            }
        }
        else if(requestCode==SELECT_FROM_GALLERY_Pic4) {
            if (resultCode == RESULT_OK) {
                capturedImageUri4 = data.getData();
                if (capturedImageUri4 != null) {

                    //   Toast.makeText(getActivity(), capturedImageUri3.toString(), Toast.LENGTH_LONG).show();

                    Glide.with(getActivity()).load(capturedImageUri4).into(iv4);
                    picHashMap.put("pic4",capturedImageUri4);
                    //  Toast.makeText(getActivity(),"size of hashmap: "+picHashMap.size(),Toast.LENGTH_LONG).show();


                } else {
                    //    Toast.makeText(getActivity(), "imageUri is null", Toast.LENGTH_LONG).show();

                }
            }
        }
        else if(requestCode==SELECT_FROM_GALLERY_Pic5) {
            if (resultCode == RESULT_OK) {
                capturedImageUri5 = data.getData();
                if (capturedImageUri5 != null) {

                    //   Toast.makeText(getActivity(), capturedImageUri3.toString(), Toast.LENGTH_LONG).show();

                    Glide.with(getActivity()).load(capturedImageUri5).into(iv5);
                    picHashMap.put("pic5",capturedImageUri5);
                    //  Toast.makeText(getActivity(),"size of hashmap: "+picHashMap.size(),Toast.LENGTH_LONG).show();


                } else {
                    //    Toast.makeText(getActivity(), "imageUri is null", Toast.LENGTH_LONG).show();

                }
            }
        }
        else if(requestCode==SELECT_FROM_GALLERY_Pic6) {
            if (resultCode == RESULT_OK) {
                capturedImageUri6 = data.getData();
                if (capturedImageUri6 != null) {

                    //   Toast.makeText(getActivity(), capturedImageUri3.toString(), Toast.LENGTH_LONG).show();

                    Glide.with(getActivity()).load(capturedImageUri6).into(iv6);
                    picHashMap.put("pic6",capturedImageUri6);
                    //  Toast.makeText(getActivity(),"size of hashmap: "+picHashMap.size(),Toast.LENGTH_LONG).show();


                } else {
                    //    Toast.makeText(getActivity(), "imageUri is null", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    public void updateBroadCastsIdsListInCustomer() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(MainActivity.mcustomer.getId());

        ref.orderByKey().equalTo("broadCastRequestsIdsList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //  Toast.makeText(getActivity(),"snapshot exists",Toast.LENGTH_LONG).show();
                    ArrayList<String> broadCastsIdsList = MainActivity.mcustomer.getBroadCastRequestsIdsList();
                    broadCastsIdsList.add(broadCastId);
                    MainActivity.mcustomer.setBroadCastRequestsIdsList(broadCastsIdsList);

                    Customer customer = new Customer(MainActivity.mcustomer.getId(), MainActivity.mcustomer.getFullName(), MainActivity.mcustomer.getUserName(),
                            MainActivity.mcustomer.getPhone(), MainActivity.mcustomer.getPassword(), MainActivity.mcustomer.getImageUrl(), broadCastsIdsList
                            , MainActivity.mcustomer.getChatHashmap(), MainActivity.mcustomer.getReceivedVisitRequestList(),
                            MainActivity.mcustomer.getPendingVisitsList(), MainActivity.mcustomer.getCompletedVisitsList(),MainActivity.mcustomer.getResponseTimeRatingList(),
                            MainActivity.mcustomer.getReceivedWorkRequestList(), MainActivity.mcustomer.getPendingWorkList(),
                            MainActivity.mcustomer.completedWorkList, MainActivity.mcustomer.workRatingList, MainActivity.mcustomer.responseRating,
                            MainActivity.mcustomer.averageWorkRating);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(MainActivity.mcustomer.getId());
                    ref.setValue(customer);
                    MainActivity.mcustomer = customer;

                    ref.orderByKey().equalTo("broadCastRequestsIdsList").removeEventListener(this);

                } else {
                    // Toast.makeText(getActivity(),"snapshot does not exist",Toast.LENGTH_LONG).show();
                    ArrayList<String> broadCastsIdsList = new ArrayList();
                    broadCastsIdsList.add(broadCastId);
                    HashMap<String, ArrayList<Message>> chatHashMap = new HashMap<>();
                    MainActivity.mcustomer.setBroadCastRequestsIdsList(broadCastsIdsList);
                    Customer customer = new Customer(MainActivity.mcustomer.getId(), MainActivity.mcustomer.getFullName(), MainActivity.mcustomer.getUserName(),
                            MainActivity.mcustomer.getPhone(), MainActivity.mcustomer.getPassword(), MainActivity.mcustomer.getImageUrl(), broadCastsIdsList
                            , MainActivity.mcustomer.getChatHashmap(), MainActivity.mcustomer.getReceivedVisitRequestList(),
                            MainActivity.mcustomer.getPendingVisitsList(), MainActivity.mcustomer.getCompletedVisitsList()
                            ,MainActivity.mcustomer.getResponseTimeRatingList() , MainActivity.mcustomer.getReceivedWorkRequestList(), MainActivity.mcustomer.getPendingWorkList(),
                            MainActivity.mcustomer.completedWorkList, MainActivity.mcustomer.workRatingList, MainActivity.mcustomer.responseRating,
                            MainActivity.mcustomer.averageWorkRating);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(MainActivity.mcustomer.getId());
                    ref.setValue(customer);
                   MainActivity.mcustomer = customer;

                    ref.orderByKey().equalTo("broadCastRequestsIdsList").removeEventListener(this);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void showImageUploadDialog(){

        if (ed_title.getText().toString().isEmpty() || ed_description.getText().toString().isEmpty()

        ) {
            Toast.makeText(getActivity(), "All fields must be filled", Toast.LENGTH_LONG).show();
        } else if (latitude < 1) {
            Toast.makeText(getActivity(), "Location must be selected", Toast.LENGTH_LONG).show();
        } else if (selectedCategory.equals("")) {
            Toast.makeText(getActivity(), "Category must be selected", Toast.LENGTH_LONG).show();
        }

        else {

            View view = getActivity().getLayoutInflater().inflate(R.layout.broadcast_upload_images_dialog, null);   // custom view

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Holo_Dialog_NoActionBar);
            final AlertDialog alertDialog = builder.create();
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            alertDialog.setView(view, 0, 0, 0, 0); // binding alert dialog with custom view
            // casting variables
            Button btn_sendBroadCast = view.findViewById(R.id.btn_broadcast);
            iv1 = view.findViewById(R.id.iv_pic1);
            iv2 = view.findViewById(R.id.iv_pic2);
            iv3 = view.findViewById(R.id.iv_pic3);
            iv4 = view.findViewById(R.id.iv_pic4);
            iv5 = view.findViewById(R.id.iv_pic5);
            iv6 = view.findViewById(R.id.iv_pic6);
            btnPic1 = view.findViewById(R.id.btn_pic1);
            btnPic2 = view.findViewById(R.id.btn_pic2);
            btnPic3 = view.findViewById(R.id.btn_pic3);
            btnPic4 = view.findViewById(R.id.btn_pic4);
            btnPic5 = view.findViewById(R.id.btn_pic5);
            btnPic6 = view.findViewById(R.id.btn_pic6);
            btnRem1 = view.findViewById(R.id.btn_rem1);
            btnRem2 = view.findViewById(R.id.btn_rem2);
            btnRem3 = view.findViewById(R.id.btn_rem3);
            btnRem4 = view.findViewById(R.id.btn_rem4);
            btnRem5 = view.findViewById(R.id.btn_rem5);
            btnRem6 = view.findViewById(R.id.btn_rem6);

            // adding click listener on add and remove Picture buttons
              btnPic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                startActivityForResult(i, SELECT_FROM_GALLERY_Pic1);

            }
        });
        btnRem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picHashMap.get("pic1")==null){
                   // Toast.makeText(getActivity(),"iv is empty",Toast.LENGTH_LONG).show();
                }
                else{
                   // iv1.setImageResource(0);
                    iv1.setImageResource(R.drawable.uploadphoto);
                    picHashMap.remove("pic1");
                  //  Toast.makeText(getActivity(),"size of hashmap: "+picHashMap.size(),Toast.LENGTH_LONG).show();
                }
            }
        });
        btnPic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picHashMap.get("pic1")==null){
                    Toast.makeText(getActivity(),"Kindly upload photo in No: 1 Picture Box first",Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                    startActivityForResult(i, SELECT_FROM_GALLERY_Pic2);
                }

            }
        });
        btnRem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picHashMap.get("pic2")==null){
                  //  Toast.makeText(getActivity(),"iv is empty",Toast.LENGTH_LONG).show();
                }
                else{


                    iv2.setImageResource(R.drawable.uploadphoto);
                    picHashMap.remove("pic2");



                }
            }
        });
        btnPic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picHashMap.get("pic2")==null){
                    Toast.makeText(getActivity(),"Kindly upload photo in No: 2 Picture Box first",Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                    startActivityForResult(i, SELECT_FROM_GALLERY_Pic3);
                }
            }
        });
        btnRem3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picHashMap.get("pic3")==null){
                  //  Toast.makeText(getActivity(),"iv is empty",Toast.LENGTH_LONG).show();
                }
                else{
                    iv3.setImageResource(R.drawable.uploadphoto);
                    picHashMap.remove("pic3");
                    //Toast.makeText(getActivity(),"size of hashmap: "+picHashMap.size(),Toast.LENGTH_LONG).show();
                }
            }
        });
            btnPic4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(picHashMap.get("pic3")==null){
                        Toast.makeText(getActivity(),"Kindly upload photo in No: 3 Picture Box first",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                        startActivityForResult(i, SELECT_FROM_GALLERY_Pic4);
                    }
                }
            });
            btnRem4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(picHashMap.get("pic4")==null){
                        //  Toast.makeText(getActivity(),"iv is empty",Toast.LENGTH_LONG).show();
                    }
                    else{
                        iv4.setImageResource(R.drawable.uploadphoto);
                        picHashMap.remove("pic4");
                        //Toast.makeText(getActivity(),"size of hashmap: "+picHashMap.size(),Toast.LENGTH_LONG).show();
                    }
                }
            });
            btnPic5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(picHashMap.get("pic4")==null){
                        Toast.makeText(getActivity(),"Kindly upload photo in No: 4 Picture Box first",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                        startActivityForResult(i, SELECT_FROM_GALLERY_Pic5);
                    }
                }
            });
            btnRem5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(picHashMap.get("pic5")==null){
                        //  Toast.makeText(getActivity(),"iv is empty",Toast.LENGTH_LONG).show();
                    }
                    else{
                        iv5.setImageResource(R.drawable.uploadphoto);
                        picHashMap.remove("pic5");
                        //Toast.makeText(getActivity(),"size of hashmap: "+picHashMap.size(),Toast.LENGTH_LONG).show();
                    }
                }
            });
            btnPic6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(picHashMap.get("pic5")==null){
                        Toast.makeText(getActivity(),"Kindly upload photo in No: 5 Picture Box first",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                        startActivityForResult(i, SELECT_FROM_GALLERY_Pic6);
                    }
                }
            });
            btnRem6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(picHashMap.get("pic6")==null){
                        //  Toast.makeText(getActivity(),"iv is empty",Toast.LENGTH_LONG).show();
                    }
                    else{
                        iv6.setImageResource(R.drawable.uploadphoto);
                        picHashMap.remove("pic6");
                        //Toast.makeText(getActivity(),"size of hashmap: "+picHashMap.size(),Toast.LENGTH_LONG).show();
                    }
                }
            });
            btn_sendBroadCast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String[] imageUrl1 = new String[1];
                    final String[] imageUrl2 = new String[1];
                    final String[] imageUrl3 = new String[1];
                    final String[] imageUrl4 = new String[1];
                    final String[] imageUrl5 = new String[1];
                    final String[] imageUrl6 = new String[1];

                    broadCastId = broadcast_ref.push().getKey();

                    if(picHashMap.size()<1) {
                        imageUrl1[0] = new String();
                        imageUrl2[0] = new String();
                        imageUrl3[0] = new String();
                        imageUrl4[0] = new String();
                        imageUrl5[0] = new String();
                        imageUrl6[0] = new String();
                        ArrayList<VisitRequest>VisitRequestsList = new ArrayList<>();
                        BroadCastRequest broadCastRequest = new BroadCastRequest(broadCastId, MainActivity.mcustomer.getUserName(), ed_title.getText().toString(),
                                ed_description.getText().toString(), selectedCategory, latitude,longitude,
                                (double)ratingBar.getRating(),minVisitCost,
                                maxVisitCost, 0, imageUrl1[0], imageUrl2[0], imageUrl3[0],imageUrl4[0],imageUrl5[0],imageUrl6[0]
                                ,VisitRequestsList );
                        broadcast_ref.child(broadCastId).setValue(broadCastRequest);
                        updateBroadCastsIdsListInCustomer();
                        Toast.makeText(getActivity(), "BroadCast has been sent successfully", Toast.LENGTH_LONG).show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                ed_title.setText("");
                                ed_description.setText("");
                                ratingBar.setRating(1);
                                alertDialog.dismiss();
                            }
                        }, 4000);
                    }
                    else{//i-e pic hasmap is not empty
                        mprogressdialog.show();

                        for ( String key :picHashMap.keySet() ) {
                            keysList.add(key);
                        }

                        for(int i=0;i<keysList.size();i++){
                            final StorageReference storageReference = mStorageRef.child("BroadCasts" + "/" + broadCastId+"/"+ i+ ".jpg");
                            final String[] mDurl = {""};

                            UploadTask utask = (UploadTask) storageReference.putFile(picHashMap.get(keysList.get(i)));
                            //  Toast.makeText(getActivity(),"uri of image"+picHashMap.get(keysList.get(i))
                            //          ,Toast.LENGTH_LONG).show();
                            Task<Uri> urlTas= utask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    return storageReference.getDownloadUrl();

                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        //  Toast.makeText(getActivity(),"Task  is successful",Toast.LENGTH_LONG).show();
                                        Uri downloadUri = task.getResult();
                                        final String downloadURL = downloadUri.toString();
                                        mDurl[0] = downloadURL;
                                        downloadedImageUrls.add(mDurl[0]);
                                        // here write your business logic
                                        if(downloadedImageUrls.size()==keysList.size()){
                                            //    Toast.makeText(getActivity(),"afer last imge",Toast.LENGTH_LONG).show();
                                            if(downloadedImageUrls.size()==1){
                                                imageUrl1[0] = downloadedImageUrls.get(0);
                                                imageUrl2[0] = new String();
                                                imageUrl3[0] = new String();
                                                imageUrl4[0] = new String();
                                                imageUrl5[0] = new String();
                                                imageUrl6[0] = new String();
                                            }
                                            else if(downloadedImageUrls.size()==2){
                                                imageUrl1[0] = downloadedImageUrls.get(0);
                                                imageUrl2[0] =downloadedImageUrls.get(1);
                                                imageUrl3[0] = new String();
                                                imageUrl4[0] = new String();
                                                imageUrl5[0] = new String();
                                                imageUrl6[0] = new String();
                                            }
                                            else if(downloadedImageUrls.size()==3){
                                                imageUrl1[0] = downloadedImageUrls.get(0);
                                                imageUrl2[0] =downloadedImageUrls.get(1);
                                                imageUrl3[0] = downloadedImageUrls.get(2);
                                                imageUrl4[0] = new String();
                                                imageUrl5[0] = new String();
                                                imageUrl6[0] = new String();
                                            }
                                            else if(downloadedImageUrls.size()==4){
                                                imageUrl1[0] = downloadedImageUrls.get(0);
                                                imageUrl2[0] =downloadedImageUrls.get(1);
                                                imageUrl3[0] = downloadedImageUrls.get(2);
                                                imageUrl4[0] = downloadedImageUrls.get(3);
                                                imageUrl5[0] = new String();
                                                imageUrl6[0] = new String();
                                            }
                                            else if(downloadedImageUrls.size()==5){
                                                imageUrl1[0] = downloadedImageUrls.get(0);
                                                imageUrl2[0] =downloadedImageUrls.get(1);
                                                imageUrl3[0] = downloadedImageUrls.get(2);
                                                imageUrl4[0] = downloadedImageUrls.get(3);
                                                imageUrl5[0] = downloadedImageUrls.get(4);
                                                imageUrl6[0] = new String();
                                            }
                                            else if(downloadedImageUrls.size()==6){
                                                imageUrl1[0] = downloadedImageUrls.get(0);
                                                imageUrl2[0] =downloadedImageUrls.get(1);
                                                imageUrl3[0] = downloadedImageUrls.get(2);
                                                imageUrl4[0] = downloadedImageUrls.get(3);
                                                imageUrl5[0] = downloadedImageUrls.get(4);
                                                imageUrl6[0] =  downloadedImageUrls.get(5);
                                            }
                                            ArrayList<VisitRequest>VisitRequestsList = new ArrayList<>();
                                            BroadCastRequest broadCastRequest = new BroadCastRequest(broadCastId, MainActivity.mcustomer.getUserName(), ed_title.getText().toString(),
                                                    ed_description.getText().toString(), selectedCategory,latitude, longitude,
                                                    (double)ratingBar.getRating(), minVisitCost,
                                                    maxVisitCost, 0, imageUrl1[0], imageUrl2[0], imageUrl3[0],imageUrl4[0],
                                                    imageUrl5[0], imageUrl6[0]
                                                    ,VisitRequestsList);
                                            broadcast_ref.child(broadCastId).setValue(broadCastRequest);
                                            mprogressdialog.dismiss();
                                            Toast.makeText(getActivity(), "BroadCast has been sent successfully", Toast.LENGTH_LONG).show();
                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {


                                                    ed_title.setText("");
                                                    ed_description.setText("");

                                                    iv1.setImageResource(R.drawable.uploadphoto);
                                                    iv2.setImageResource(R.drawable.uploadphoto);
                                                    iv3.setImageResource(R.drawable.uploadphoto);
                                                    iv4.setImageResource(R.drawable.uploadphoto);
                                                    iv5.setImageResource(R.drawable.uploadphoto);
                                                    iv6.setImageResource(R.drawable.uploadphoto);
                                                    ratingBar.setRating(1);


                                                    updateBroadCastsIdsListInCustomer();
                                                    alertDialog.dismiss();

                                                }
                                            }, 4000);
                                        }// end if
                                    }// end if

                                    else{// i-e if task is not successfull


                                        Toast.makeText(getActivity(),"Some Error occured:\n It may be due to network or internet speed.",Toast.LENGTH_LONG).show();


                                    }// end else



                                }// end onComplete

                            });// end addOnCompleteListener
                        }// end for loop




                    }// end else i-e images uploaded



                }
            });


            alertDialog.show();
            Window window = alertDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }// most outer else
    }


}