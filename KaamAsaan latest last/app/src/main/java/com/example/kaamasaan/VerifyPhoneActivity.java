package com.example.kaamasaan;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.kaamasaan.SplashScreenActivity.db;

public class VerifyPhoneActivity extends AppCompatActivity {

    EditText ed_code;
    Button btnVerify;
    String phoneNumber;
    String userType;
    private String verificationId;
    private FirebaseAuth mAuth;

    // varaibles for customer signup
    FirebaseDatabase database;
    DatabaseReference user_ref;
    StorageReference mStorageRef;
    ProgressDialog mprogressdialog;
    ArrayList<String> BroadCastRequestsIdsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        mAuth = FirebaseAuth.getInstance();
        ed_code  = findViewById(R.id.ed_code);
        btnVerify = findViewById(R.id.btn_verify);
        phoneNumber = getIntent().getStringExtra("phone");
        userType = getIntent().getStringExtra("userType");
        mprogressdialog = new ProgressDialog(this);
        mprogressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mprogressdialog.setMessage("Signing Up...");
        mprogressdialog.setIndeterminate(true);
        mprogressdialog.setCancelable(false);


        if(userType.equals("Customer")) {
            database = FirebaseDatabase.getInstance();
            user_ref = database.getReference("Users").child("Customers");
            mStorageRef = FirebaseStorage.getInstance().getReference();
            BroadCastRequestsIdsList = new ArrayList<>();
        }

      //  Toast.makeText(VerifyPhoneActivity.this,"phone is; "+ phoneNumber,Toast.LENGTH_SHORT).show();
        sendVerificationCode(phoneNumber);

      btnVerify.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              String code= ed_code.getText().toString();

              if(code.isEmpty()||code.length()<6){
                  ed_code.setError("Enter valid code");
                  ed_code.requestFocus();
              }
              else {
                  verifyCode(code);
              }
          }
      });
    }

    private void sendVerificationCode(String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD,mCallBack);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
    mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override              //s is verfication id that is sent by the sms
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
         //   super.onCodeSent(s, forceResendingToken);
            // in this method we will store the verification code that is sent by sms
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
          // this method is called when the verification is completed and Here we
            // get the code that is sent by sms automatically. If this function succeed
            // user don't have to enter the code manually

            // now getting the code that is sent by the sms
            String code=  phoneAuthCredential.getSmsCode();   // code will not null if the code sent by sms will not be null;
            if(code!=null){
                ed_code.setText(code);
               // verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
         Toast.makeText(VerifyPhoneActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            super.onCodeAutoRetrievalTimeOut(s);
            Toast.makeText(VerifyPhoneActivity.this,"code auto retreival time out ",Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code){
        // This method is used when user manually enters the code sent by the sms and clicks the signIn button
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        singUpWithCredential(credential);
    }

    private void singUpWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // start the new Activity

                    Intent intent;
                    if(userType.equals("ServiceProvider")) {
                        Toast.makeText(VerifyPhoneActivity.this,"Phone number verified",Toast.LENGTH_SHORT).show();
                        ServiceProviderSignUpPart1Activity.verifiedphone = phoneNumber;
                        intent = new Intent(VerifyPhoneActivity.this, ServiceProviderSignUpPart2Activity.class);
                        startActivity(intent);
                       // finish();
                    }
                    else if(userType.equals("Customer")){

                        CustomerSignUpActivity.verifiedphone = phoneNumber.substring(2);
                        final String userName =  CustomerSignUpActivity.sharedpreferences_customer.getString("userNamekey_customer","");
                        String capturedImageUri =CustomerSignUpActivity.sharedpreferences_customer.getString("urlkey_customer","");

                        final StorageReference storageReference = mStorageRef.child("Customers" + "/" + userName + ".jpg");
                        final String[] mDurl = {""};
                        mprogressdialog.show();
                        UploadTask utask = (UploadTask) storageReference.putFile(Uri.parse(capturedImageUri));
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
                                    ArrayList<VisitRequest>receivedVisitRequestList = new ArrayList<>();
                                    ArrayList<VisitRequest>pendingVisitsList = new ArrayList<>();
                                    ArrayList<VisitRequest>completedVisitsList = new ArrayList<>();
                                    ArrayList<ResponseTimeRating>responseTimeRatingList = new ArrayList<>();
                                    ArrayList<WorkRequest>receivedWorkRequestList = new ArrayList<>();
                                    ArrayList<WorkRequest>pendingWorkList = new ArrayList<>();
                                    ArrayList<WorkRequest>completedWorkList = new ArrayList<>();
                                    ArrayList<WorkRating>workRatingList = new ArrayList<>();

                                    int responseRating = 0;
                                    int averageWorkRating = 1;



                                    Customer customer = new Customer(id, fullname,userName,phone,password,mDurl[0],BroadCastRequestsIdsList,chatHashMap,
                                           receivedVisitRequestList,pendingVisitsList,completedVisitsList,responseTimeRatingList,receivedWorkRequestList,pendingWorkList,
                                            completedWorkList,workRatingList,responseRating,averageWorkRating);
                                    user_ref.child(id).setValue(customer);
                                    mprogressdialog.dismiss();
                                    Toast.makeText(VerifyPhoneActivity.this,"Phone number verified. Now you can Sign In.",Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
                                    startActivity(intent);
                                  //  finish();

                                }
                                else{
                                    mprogressdialog.dismiss();
                                    Toast.makeText(VerifyPhoneActivity.this,"Some Error occured:\n It may be due to network or internet speed.",Toast.LENGTH_LONG).show();

                                }
                            }
                        });





                    }// user if customer




                }
                else{
                    Toast.makeText(VerifyPhoneActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
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
}
