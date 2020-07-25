package com.example.kaamasaan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomerSignUpActivity extends AppCompatActivity {
    EditText fullName,userName,phone,password,confirmpassword;
    TextView countrycode;
    Button signup;
    AppCompatImageButton btn_pic_gallery;
    AppCompatImageButton btn_pic_camera;
    AppCompatImageButton btn_rotate;
    private  static final int SELECT_FROM_GALLERY = 1;
    private static final int  CAPTURE_FROM_CAMERA = 2;
    Uri capturedImageUri;
    AppCompatImageView iv;

    FirebaseDatabase database;
    DatabaseReference user_ref;
    boolean alreadyExist = false;
    ProgressDialog mprogressdialog;
    boolean isUserNameAlreadyExistInCustomers = false;
    boolean isUserNameAlreadyExistInServiceProviders = false;
    public static final String MYPREFERENCES_Customer = "MyPref_Customer";
    public static SharedPreferences sharedpreferences_customer;

    public static final String fullNamekey_customer = "fullnamekey_customer";
    public static final String userNamekey_customer = "userNamekey_customer";
    public static final String phonekey_customer = "phonekey_customer";
    public static final String passwordkey_customer = "passwordkey_customer";
    public static final String urlkey_customer = "urlkey_customer";

    ArrayList<String> BroadCastRequestsList;

    public static String verifiedphone = "nonumber";


    // remove this code after application completion
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sign_up);
        fullName = findViewById(R.id.ed_customer_name);
        userName = findViewById(R.id.ed_customer_username);
        phone = findViewById(R.id.ed__customer_phoneNo);
        password = findViewById(R.id.ed_customer_password);
        confirmpassword = findViewById(R.id.ed_customer_confirmpassword);
        countrycode = findViewById(R.id.tv_customer_country_code);
        btn_pic_gallery = findViewById(R.id.btn_pic_gallery);
        btn_pic_camera = findViewById(R.id.btn_pic_camera);
        btn_rotate = findViewById(R.id.btn_rotate);
        signup = findViewById(R.id.btn_customer_signup);
        iv = findViewById(R.id.imv);
        database = FirebaseDatabase.getInstance();
        user_ref = database.getReference("Users").child("Customers");
        BroadCastRequestsList = new ArrayList<>();

        countrycode.setText(GetCountryZipCode());
        mprogressdialog = new ProgressDialog(this);
        mprogressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mprogressdialog.setMessage("Please wait...");
        mprogressdialog.setIndeterminate(true);
        mprogressdialog.setCancelable(false);
        sharedpreferences_customer = getSharedPreferences(MYPREFERENCES_Customer, Context.MODE_PRIVATE);

        // remove this code after app completion
        mStorageRef = FirebaseStorage.getInstance().getReference();





        btn_pic_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                //
                startActivityForResult(i,SELECT_FROM_GALLERY);
            }
        });

        btn_pic_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        Toast.makeText(CustomerSignUpActivity.this,"Permission already denied",Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(CustomerSignUpActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 110);

                    }
                    else if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED){
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent,CAPTURE_FROM_CAMERA);
                    }
                }
                else{
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent,CAPTURE_FROM_CAMERA);
                }

            }
        });

        btn_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(capturedImageUri==null){

                }
                else {
                    iv.setRotation(iv.getRotation() + 90);
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fullName.getText().toString().isEmpty()||userName.getText().toString().isEmpty()||password.getText().toString().isEmpty()
                        ||confirmpassword.getText().toString().isEmpty()||phone.getText().toString().isEmpty()){
                    Toast.makeText(CustomerSignUpActivity.this,"Error: All fields must be filled",Toast.LENGTH_SHORT).show();
                }
                else if(phone.getText().toString().length()<10||phone.getText().toString().length()>10){
                    phone.setError("Phone number should have 10 digits without space e.g 324XXXXXXX");
                    phone.requestFocus();

                }
                else if(password.getText().toString().length()<9){
                    password.setError("Minimum length should be 9");
                    password.requestFocus();
                }
                else if(!confirmpassword.getText().toString().equals(password.getText().toString())){
                    confirmpassword.setError("Password mismatch");
                    confirmpassword.requestFocus();
                }

                else if(capturedImageUri ==null){
                    Toast.makeText(CustomerSignUpActivity.this,"Profile picture must be uploaded",Toast.LENGTH_SHORT).show();
                }
                else{
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                    if(cm.getActiveNetworkInfo()== null) {
                        Toast.makeText(CustomerSignUpActivity.this,"Connect to internet please",Toast.LENGTH_LONG).show();
                    }
                    else{
                        mprogressdialog.show();
                       isUserNameAlreadyExistInCustomers(userName.getText().toString());

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                if(isUserNameAlreadyExistInCustomers==true){

                                    Toast.makeText(CustomerSignUpActivity.this,"Error: This User name" +
                                            " has already taken. Enter a different User Name",Toast.LENGTH_LONG).show();
                                    userName.setError("User Name already exist");
                                    userName.requestFocus();
                                    mprogressdialog.dismiss();
                                }
                                else{
                                    isUserNameAlreadyExistInServiceProviders(userName.getText().toString());
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            if(isUserNameAlreadyExistInServiceProviders==true){
                                                Toast.makeText(CustomerSignUpActivity.this,"Error: This User name" +
                                                        " has already taken. Enter a different User Name",Toast.LENGTH_LONG).show();
                                                userName.setError("User Name already exist");
                                                userName.requestFocus();
                                                mprogressdialog.dismiss();
                                            }

                                            else{
                                                            //uncomment thid code after app completion
                                                        SharedPreferences.Editor editor = sharedpreferences_customer.edit();
                                                       editor.putString(fullNamekey_customer,fullName.getText().toString());
                                                       editor.putString(userNamekey_customer,userName.getText().toString());
                                                       editor.putString(phonekey_customer, phone.getText().toString());
                                                       editor.putString(passwordkey_customer,password.getText().toString());
                                                       editor.putString(urlkey_customer,capturedImageUri.toString());
                                                       editor.commit();
                                                       // uncomment this code after application completion
                                                       Intent intent = new Intent(CustomerSignUpActivity.this,VerifyPhoneActivity.class);
                                                       intent.putExtra("phone","+92"+phone.getText().toString());
                                                       intent.putExtra("userType","Customer");
                                                     mprogressdialog.dismiss();
                                                     startActivity(intent);
                                                    // finish();

                                                      // remove this code after application completion

                                              //  final String userName =  CustomerSignUpActivity.sharedpreferences_customer.getString("userNamekey_customer","");
                                             //   String capturedImageUri =CustomerSignUpActivity.sharedpreferences_customer.getString("urlkey_customer","");

                                               /* final StorageReference storageReference = mStorageRef.child("Customers" + "/" + userName.getText().toString() + ".jpg");
                                                final String[] mDurl = {""};
                                                mprogressdialog.show();
                                               // Toast.makeText(CustomerSignUpActivity.this,capturedImageUri+"",Toast.LENGTH_LONG).show();
                                                UploadTask utask = (UploadTask) storageReference.putFile(capturedImageUri);
                                                Task<Uri> urlTask= utask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                                                            Uri downloadUri = task.getResult();
                                                            final String downloadURL = downloadUri.toString();
                                                            mDurl[0] = downloadURL;
                                                            String id=  user_ref.push().getKey();
                                                            String fullname = CustomerSignUpActivity.sharedpreferences_customer.getString("fullnamekey_customer","");
                                                            String phone = CustomerSignUpActivity.sharedpreferences_customer.getString("phonekey_customer","");
                                                            String password = CustomerSignUpActivity.sharedpreferences_customer.getString("passwordkey_customer","");
                                                            String imageUrl = CustomerSignUpActivity.sharedpreferences_customer.getString("urlkey_customer","");
                                                            HashMap<String,ArrayList<Message>> chatHashMap = new HashMap<>();
                                                            Customer customer = new Customer(id, fullName.getText().toString(),userName.getText().toString(),phone,password,mDurl[0],BroadCastRequestsList,chatHashMap);
                                                            user_ref.child(id).setValue(customer);
                                                            mprogressdialog.dismiss();
                                                           // Toast.makeText(CustomerSignUpActivity.this,"Phone number verified. Now you can Sign In.",Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(CustomerSignUpActivity.this, MainActivity.class);
                                                            startActivity(intent);

                                                        }
                                                        else{
                                                            mprogressdialog.dismiss();
                                                            Toast.makeText(CustomerSignUpActivity.this,"Some Error occured:\n It may be due to network or internet speed.",Toast.LENGTH_LONG).show();

                                                        }
                                                    }
                                                });

*/



                                            }// do not remove this bracket

                                        }
                                    }, 5000);



                                }


                            }
                        }, 5000);



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

    private boolean isUserNameAlreadyExist(String userName){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
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




    private String hasintegers(String userName){
      boolean  has_integers = false;
        char[] chars = userName.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(char c : chars) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }

      return sb.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//Toast.makeText(CustomerSignUpActivity.this,"Inside On ActivityResult",Toast.LENGTH_LONG).show();
        if (requestCode == SELECT_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {

                capturedImageUri = data.getData();
                if (capturedImageUri != null) {


                    Glide.with(this).load(capturedImageUri).into(iv);

                }


            }
        }

        else if(requestCode == CAPTURE_FROM_CAMERA){
         //   Toast.makeText(CustomerSignUpActivity.this,"Inside Capture from camera",Toast.LENGTH_LONG).show();
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Matrix matrix = new Matrix();
                iv.setImageBitmap(photo);





//
                // METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(CustomerSignUpActivity.this, photo);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110)
        {
            Toast.makeText(this, "request code is 110", Toast.LENGTH_LONG).show();
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
              //  Toast.makeText(this, "external storage permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAPTURE_FROM_CAMERA);
            }
            else
            {
              //  Toast.makeText(this, "external storage permission not granted", Toast.LENGTH_LONG).show();
            }
        }
        else{
          //  Toast.makeText(this, "request code is  not 110", Toast.LENGTH_LONG).show();
        }
    }
}
