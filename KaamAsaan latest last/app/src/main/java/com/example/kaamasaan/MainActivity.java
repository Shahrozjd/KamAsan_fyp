package com.example.kaamasaan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Typeface;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.kaamasaan.SplashScreenActivity.db;

public class MainActivity extends AppCompatActivity {
    TextView tv_title,tv_signup_title,lbl_forgot_password;
    EditText userName, password;
    Typeface mfont;
    Button btn_customer_signup,btn_sp_signup;
    Button btnSingIn;
    String userNameOfSP, userNameOfCustomer;
  //  public static SQLiteDatabase db;
    ProgressDialog mprogressdialog;

    boolean isUserNameAlreadyExistInCustomers = false;
    boolean isUserNameAlreadyExistInServiceProviders = false;
    public static ServiceProvider mserviceProvider = new ServiceProvider();
    public static Customer mcustomer = new Customer();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_title = findViewById(R.id.tv_title);
        tv_signup_title = findViewById(R.id.tv_title_signup);
        userName = findViewById(R.id.ed_UserName);
        password = findViewById(R.id.ed_password);
        lbl_forgot_password = findViewById(R.id.lbl_forgot_password);
        lbl_forgot_password.setPaintFlags( lbl_forgot_password.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btnSingIn = findViewById(R.id.btn_singnin);
       btn_customer_signup = findViewById(R.id.button_customer_signup);
       btn_sp_signup= findViewById(R.id.button_sp_signup);
        mfont = Typeface.createFromAsset(this.getAssets(),"fonts/KaushanScript-Regular.otf");
        tv_title.setTypeface(mfont);



        mprogressdialog = new ProgressDialog(this);
        mprogressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mprogressdialog.setMessage("Please wait...");
        mprogressdialog.setIndeterminate(true);
        mprogressdialog.setCancelable(false);

        lbl_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ForgotPasswordActivity.class));
            }
        });
        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                if(cm.getActiveNetworkInfo()== null) {
                    Toast.makeText(MainActivity.this,"Error: Connect to internet please",Toast.LENGTH_LONG).show();

                }
                else{

                    if(userName.getText().toString().isEmpty()){
                        userName.setError("User Name must be filled");
                        userName.requestFocus();
                    }
                    else if(password.getText().toString().isEmpty()){
                        password.setError("Password must be filled");
                        password.requestFocus();
                    }
                    mprogressdialog.show();
                    isUserNameAlreadyExistInCustomers(userName.getText().toString());


                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            if(isUserNameAlreadyExistInCustomers==true){

                                // check password accuracy
                                DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
                                customer_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ssn: dataSnapshot.getChildren()){
                                            Customer customer = ssn.getValue(Customer.class);
                                            if(userNameOfCustomer.equals(customer.getUserName())&&password.getText().toString().equals(customer.getPassword())){
                                                mcustomer = customer;
                                                mprogressdialog.dismiss();
                                                startActivity(new Intent(MainActivity.this,CustomerHomeActivity.class));
                                                finish();

                                                 break;
                                            }
                                            else if(userNameOfCustomer.equals(customer.getUserName())&& !password.getText().toString().equals(customer.getPassword())){
                                                mprogressdialog.dismiss();
                                                password.setError("Incorrect Password");
                                                password.requestFocus();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            else{// check if user exist as a service provider
                                isUserNameAlreadyExistInServiceProviders(userName.getText().toString());
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(isUserNameAlreadyExistInServiceProviders==true){
                                            // check password accuracy
                                            DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
                                            customer_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for(DataSnapshot ssn: dataSnapshot.getChildren()){
                                                        ServiceProvider serviceProvider = ssn.getValue(ServiceProvider.class);
                                                        if(userNameOfSP.equals(serviceProvider.getUserName())&&password.getText().toString().equals(serviceProvider.getPassword())){
                                                            mserviceProvider=serviceProvider;
                                                          //  Toast.makeText(MainActivity.this,mserviceProvider.getFullName(),Toast.LENGTH_LONG).show();
                                                            mprogressdialog.dismiss();

                                                            startActivity(new Intent(MainActivity.this,ServiceProviderHomeActivity.class));
                                                            finish();
                                                            break;



                                                        }
                                                        else if(userNameOfSP.equals(serviceProvider.getUserName())&& !password.getText().toString().equals(serviceProvider.getPassword())) {
                                                            mprogressdialog.dismiss();
                                                            password.setError("Incorrect Password");
                                                            password.requestFocus();

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                        else{
                                            mprogressdialog.dismiss();
                                            userName.setError("User Name does not exist");
                                            userName.requestFocus();
                                        }

                                    }
                                }, 4000);


                            }

                        }
                    }, 4000);


                }


            }
        });





       btn_sp_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(MainActivity.this,ServiceProviderSignUpPart1Activity.class));
               finish();


            }
        });

        btn_customer_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CustomerSignUpActivity.class));
                finish();
            }
        });

    }

    private boolean isUserNameAlreadyExistInCustomers(final String userName){

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
                   userNameOfCustomer = userName;
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

    private boolean isUserNameAlreadyExistInServiceProviders(final String userName){

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
                    userNameOfSP = userName;
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
    public void onBackPressed() {

       if(db!=null) {
           db.close();
           System.exit(0);
       }
       else {
           System.exit(0);
       }
    }



}

