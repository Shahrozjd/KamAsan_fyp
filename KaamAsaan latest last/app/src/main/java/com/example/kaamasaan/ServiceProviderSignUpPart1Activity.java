package com.example.kaamasaan;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ServiceProviderSignUpPart1Activity extends AppCompatActivity {
    Button btnChangeLocation;
   ImageButton btn_Next, btnBack;
    EditText edName, edUserName, edPassword, edConfirmPassword, ed_PhoneNumber;
    TextView txt_phoneCode, txt_title;
    Typeface mfont;
    AppCompatImageButton btn_pic_gallery;

    AppCompatImageView iv;
    private  static final int SELECT_FROM_GALLERY = 1;
    private static final int  CAPTURE_FROM_CAMERA = 2;
    public static double latitude = 0.0;
    public static double longitude=   0.0;
    int RequestCode = 10;
    public LocationManager lm;
    public static final String MYPREFERENCES = "MyPref";
    public static SharedPreferences sharedpreferences;
    RadioGroup rg;
    FirebaseDatabase database;
    DatabaseReference user_ref;
    ProgressDialog mprogressdialog;
    boolean alreadyExist = false;
    boolean isUserNameAlreadyExistInCustomers = false;
    boolean isUserNameAlreadyExistInServiceProviders = false;
    public static String verifiedphone = "nonumber";
    Uri capturedImageUri;
    de.hdodenhof.circleimageview.CircleImageView imv;
    CheckBox checkBox;


    public static final String fullNamekey = "fullnamekey";
    public static final String userNamekey = "userNamekey";
    public static final String phonekey = "phonekey";
    public static final String passwordkey = "passwordkey";
    public static final String latitudekey = "latitudekey";
    public static final String longitudekey = "longitudekey";
    public static final String categorykey = "categorykey";
    public static final String radiuskey = "radiuskey";
    public static final String  availabilitytimekey= "availabilitytimekey";
    public static final String visitcostkey = "visitcostkey";
    public static final String responsetimekey = "responsetimekey";
    public static final String biokey = "biokey";
    public static final  String urikey = "urikey";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_sign_up_part1);
        mfont = Typeface.createFromAsset(this.getAssets(), "fonts/KaushanScript-Regular.otf");
        sharedpreferences = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        btn_Next = findViewById(R.id.btn_Next);
        btnChangeLocation = findViewById(R.id.btn_change_location);
        btnBack = findViewById(R.id.btn_back);
        btn_pic_gallery = findViewById(R.id.btn_pic_gallery);

        edName = findViewById(R.id.ed_name);
        edUserName = findViewById(R.id.ed_username);
        edPassword = findViewById(R.id.ed_password);
        edConfirmPassword = findViewById(R.id.ed_confirmpassword);
        ed_PhoneNumber = findViewById(R.id.ed_phoneNo);
         checkBox = findViewById(R.id.check_currentlocation);
        imv = findViewById(R.id.imv);



        mprogressdialog = new ProgressDialog(this);
        mprogressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mprogressdialog.setMessage("Please wait...");
        mprogressdialog.setIndeterminate(true);
        mprogressdialog.setCancelable(false);


        database = FirebaseDatabase.getInstance();
        user_ref = database.getReference("Users").child("ServiceProviders");









 btn_pic_gallery.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
      //
         startActivityForResult(i,SELECT_FROM_GALLERY);
     }
 });




        btn_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


              //  Toast.makeText(ServiceProviderSignUpPart1Activity.this,"Verification code has sent to you by sms",Toast.LENGTH_SHORT).show();

                 if(edName.getText().toString().isEmpty()||edUserName.getText().toString().isEmpty()||edPassword.getText().toString().isEmpty()
                 ||edConfirmPassword.getText().toString().isEmpty()||ed_PhoneNumber.getText().toString().isEmpty()){
                     Toast.makeText(ServiceProviderSignUpPart1Activity.this,"Error: All fields must be filled",Toast.LENGTH_SHORT).show();
                 }
                 else if(ed_PhoneNumber.getText().toString().length()<13||ed_PhoneNumber.getText().toString().length()>13){
                     ed_PhoneNumber.setError("Must be 12 digits with + sign. e.g +92xxxxxxxxxx ");
                     ed_PhoneNumber.requestFocus();
                 }
                 else if(!ed_PhoneNumber.getText().toString().substring(0,3).equals("+92")){
                    ed_PhoneNumber.setError("Please enter a valid phone number. e.g +92xxxxxxxxxx ");
                    ed_PhoneNumber.requestFocus();
                    Toast.makeText(ServiceProviderSignUpPart1Activity.this,
                            ed_PhoneNumber.getText().toString().substring(0,3),Toast.LENGTH_LONG).show();
                 }
                 else if(edPassword.getText().toString().length()<9){
                     edPassword.setError("Minimum length should be 9");
                     edPassword.requestFocus();
                 }
                 else if(!edConfirmPassword.getText().toString().equals(edPassword.getText().toString())){
                     edConfirmPassword.setError("Password mismatch");
                    edConfirmPassword.requestFocus();
                 }
                 else if(capturedImageUri==null){
                     Toast.makeText(ServiceProviderSignUpPart1Activity.this,"Profile picture must be uploaded",Toast.LENGTH_SHORT).show();
                }
                 else{

                     ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                     if(cm.getActiveNetworkInfo()== null) {
                         Toast.makeText(ServiceProviderSignUpPart1Activity.this,"Error: Connect to internet please",Toast.LENGTH_LONG).show();

                     }
                     else {

                         if(checkBox.isChecked()){
                             lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                             if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                                 buildAlertMessageNoGPS();
                             }
                             else{
                                 mprogressdialog.show();
                                 getcurrentLocation();


                                 isUserNameAlreadyExistInCustomers(edUserName.getText().toString());
                                 isUserNameAlreadyExistInServiceProviders(edUserName.getText().toString());

                                 final Handler handler = new Handler();
                                 handler.postDelayed(new Runnable() {
                                     @Override
                                     public void run() {
                                         // Do something after 5s = 5000ms
                                         if(isUserNameAlreadyExistInCustomers==true){

                                              mprogressdialog.dismiss();

                                             Toast.makeText(ServiceProviderSignUpPart1Activity.this,"Error: This User name" +
                                                     "has already Taken. Enter a diffterent User Name",Toast.LENGTH_LONG).show();
                                             edUserName.setError("User Name already exist");
                                             edUserName.requestFocus();

                                         }

                                         else {

                                             isUserNameAlreadyExistInServiceProviders(edUserName.getText().toString());
                                             final Handler handler = new Handler();
                                             handler.postDelayed(new Runnable() {
                                                 @Override
                                                 public void run() {
                                                     // Do something after 5s = 5000ms
                                                     if(isUserNameAlreadyExistInServiceProviders==true){

                                                         mprogressdialog.dismiss();

                                                         Toast.makeText(ServiceProviderSignUpPart1Activity.this,"Error: This User name" +
                                                                 "has already Taken. Enter a diffterent User Name",Toast.LENGTH_LONG).show();
                                                         edUserName.setError("User Name already exist");
                                                         edUserName.requestFocus();

                                                     }
                                                     else{
                                                         SharedPreferences.Editor editor = sharedpreferences.edit();
                                                         editor.putString(fullNamekey,edName.getText().toString());
                                                         editor.putString(userNamekey,edUserName.getText().toString());
                                                         editor.putString(phonekey,ed_PhoneNumber.getText().toString());
                                                         editor.putString(passwordkey,edPassword.getText().toString());
                                                         editor.putLong(latitudekey,Double.doubleToRawLongBits(latitude));
                                                         editor.putLong(longitudekey,Double.doubleToRawLongBits(longitude));
                                                         editor.putString(urikey,capturedImageUri.toString());
                                                         editor.commit();

                                                         mprogressdialog.dismiss();
                                                         //uncomment this code after application  completion

                                                         Intent intent;
                                                         intent = new Intent(ServiceProviderSignUpPart1Activity.this, VerifyPhoneActivity.class);
                                                             intent.putExtra("phone",  ed_PhoneNumber.getText().toString());
                                                             intent.putExtra("userType", "ServiceProvider");
                                                             System.out.println("intentphone "+ed_PhoneNumber.getText());
                                                            handler.removeCallbacks(this);
                                                             startActivity(intent);
                                                             finish();



                                                     }

                                                 }
                                             }, 5000);



                                         }
                                     }
                                 }, 5000);

                             }
                         }
                         else{// i-e changed location radio button is selected
                             if(latitude==0.0||longitude==0.0){
                                locationNotChangeOnMap();
                             }
                             else{

                                 mprogressdialog.show();
                                 isUserNameAlreadyExistInCustomers(edUserName.getText().toString());

                                 final Handler handler = new Handler();
                                 handler.postDelayed(new Runnable() {
                                     @Override
                                     public void run() {
                                         // Do something after 5s = 5000ms
                                         if(isUserNameAlreadyExistInCustomers==true){

                                            mprogressdialog.dismiss();
                                             Toast.makeText(ServiceProviderSignUpPart1Activity.this,"Error: This User name" +
                                                     "has already Taken. Enter a diffterent User Name",Toast.LENGTH_LONG).show();
                                             edUserName.setError("User Name already exist");
                                             edUserName.requestFocus();


                                         }
                                         else{
                                             mprogressdialog.dismiss();
                                             SharedPreferences.Editor editor = sharedpreferences.edit();
                                             editor.putString(fullNamekey,edName.getText().toString());
                                             editor.putString(userNamekey,edUserName.getText().toString());
                                             editor.putString(phonekey,ed_PhoneNumber.getText().toString());
                                             editor.putString(passwordkey,edPassword.getText().toString());
                                             editor.putLong(latitudekey,Double.doubleToRawLongBits(latitude));
                                             editor.putLong(longitudekey,Double.doubleToRawLongBits(longitude));
                                             editor.putString(urikey,capturedImageUri.toString());
                                             editor.commit();

                                             // uncomment this code after application completion
                                             Intent intent;
                                             intent = new Intent(ServiceProviderSignUpPart1Activity.this, VerifyPhoneActivity.class);
                                             intent.putExtra("phone", ed_PhoneNumber.getText().toString());
                                             intent.putExtra("userType", "ServiceProvider");
                                             handler.removeCallbacks(this);
                                             startActivity(intent);
                                           //  finish();



                                         }

                                     }
                                 }, 5000);


                                  // start 2nd activity

                             }

                         }
                     }
                 }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();

            }
        });

        btnChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkBox.isChecked()){
                    Toast.makeText(getApplicationContext(),"Error:\nPlease uncheck  the 'Current Location' option first",Toast.LENGTH_LONG).show();
                }
                else{
                    lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                    if(cm.getActiveNetworkInfo()== null) {
                        Toast.makeText(ServiceProviderSignUpPart1Activity.this,"Connect to internet please",Toast.LENGTH_LONG).show();

                    }

                    else if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){

                        buildAlertMessageNoGPS();
                    }
                    else{
                         // open map activity
                        startActivity(new Intent(ServiceProviderSignUpPart1Activity.this,MapsActivitySPSignup.class));

                    }
                }
            }
        });
    }

    private String GetCountryZipCode(){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    private void getcurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ServiceProviderSignUpPart1Activity.this,"permissions not granted",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(ServiceProviderSignUpPart1Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RequestCode);


        }

             else{
                // Toast.makeText(ServiceProviderSignUpPart1Activity.this,"Your location is not traced",Toast.LENGTH_LONG).show();
                 lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

             }


        }


    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            lm.removeUpdates(this);

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
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==RequestCode){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(ServiceProviderSignUpPart1Activity.this,"Permission Granted, Now you can access location data.",Toast.LENGTH_LONG).show();
                getcurrentLocation();
            } else {

                Toast.makeText(ServiceProviderSignUpPart1Activity.this,"Permission denied,  You cannot access location data.",Toast.LENGTH_LONG).show();


            }
        }
        else if(requestCode==110){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,CAPTURE_FROM_CAMERA);
            }
            else{
                Toast.makeText(ServiceProviderSignUpPart1Activity.this,"To take photo , Allow the app  to use external storage.",Toast.LENGTH_LONG).show();
              //  Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
              //  startActivityForResult(cameraIntent,CAPTURE_FROM_CAMERA);
            }
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

    protected void locationNotChangeOnMap(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You have not saved  your changed location on Map.\nOpen Map and save your changed location\n or" +
                "Select 'Current Location' Option")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }



    private boolean isUserNameAlreadyExist(String userName){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");

        Query query = null;
        hasintegers(userName);
        if( hasintegers(userName).length()>0){
            query= ref .orderByChild("userName").equalTo(userName) ;
        }
        else{
            query= ref .orderByChild("userName").equalTo(userName.toLowerCase()) ;
        }
        query.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    alreadyExist = true;
                }
                else{
                    alreadyExist = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return alreadyExist;

    }
    private String hasintegers(String userName){

        char[] chars = userName.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(char c : chars) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }

        return sb.toString();
    }




    private boolean isUserNameAlreadyExistInCustomers(String userName){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers");

        Query query = null;
        if(hasintegers(userName).length()>0){
            query =  ref .orderByChild("userName").equalTo(userName);
        }
        else{
            query =  ref .orderByChild("userName").equalTo(userName.toLowerCase()) ;
        }

        query.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    isUserNameAlreadyExistInCustomers = true;
                }
                else{
                    isUserNameAlreadyExistInCustomers = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return isUserNameAlreadyExistInCustomers;

    }

    private boolean isUserNameAlreadyExistInServiceProviders(String userName){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        Query query = null;
        if(hasintegers(userName).length()>0){
            query =  ref .orderByChild("userName").equalTo(userName);
        }
        else{
            query =  ref .orderByChild("userName").equalTo(userName.toLowerCase()) ;
        }
        query.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    isUserNameAlreadyExistInServiceProviders = true;
                }
                else{
                    isUserNameAlreadyExistInServiceProviders = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return isUserNameAlreadyExistInServiceProviders;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {

                capturedImageUri = data.getData();
                if (capturedImageUri != null) {


                    Glide.with(this).load(capturedImageUri).into(imv);

                }
               /* Bitmap photo = (Bitmap) data.getExtras().get("data");
                iv.setImageBitmap(photo);*/

            }
        }

        else if(requestCode == CAPTURE_FROM_CAMERA){
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Matrix matrix = new Matrix();
               // matrix.postRotate(getRotationDegree());

               /* if(getRotationDegree()==90)

                Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);*/



                   iv.setImageBitmap(photo);





//
                // METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getApplicationContext(), photo);

                //   METHOD TO GET THE ACTUAL PATH
             //   File finalFile = new File(getRealPathFromURI(tempUri));

               capturedImageUri = tempUri;
           //    Toast.makeText(ServiceProviderSignUpPart1Activity.this,capturedImageUri.toString(),Toast.LENGTH_LONG).show();
             //   Glide.with(this).load(capturedImageUri).into(iv);

            }
        }
    }


       public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public int getRotationDegree() {
        int degree = 0;

        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK||info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                degree = info.orientation;

                return degree;
            }
        }

        return degree;
    }


}