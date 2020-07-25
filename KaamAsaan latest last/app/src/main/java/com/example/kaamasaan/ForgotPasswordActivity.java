package com.example.kaamasaan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText ed_userName, ed_PhoneNumber;
    Button btn_recover_password;
    boolean isUserNameAlreadyExistInCustomers = false;
    boolean isUserNameAlreadyExistInServiceProviders = false;
    FirebaseDatabase database;
    DatabaseReference customer_ref;
    DatabaseReference sp_ref;
    String userId;
    ProgressDialog mprogressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ed_userName = findViewById(R.id.ed_userName);
      //  ed_PhoneNumber = findViewById(R.id.ed_phone) ;
        btn_recover_password = findViewById(R.id.btn_recover_password);
        database = FirebaseDatabase.getInstance();
        customer_ref = database.getReference("Users").child("Customers");
        sp_ref = database.getReference("Users").child("ServiceProviders");
        mprogressdialog = new ProgressDialog(this);
        mprogressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mprogressdialog.setMessage("Generating new Password...");
        mprogressdialog.setIndeterminate(true);
        mprogressdialog.setCancelable(false);



        final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        btn_recover_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ed_userName.getText().toString().isEmpty()){
                    Toast.makeText(ForgotPasswordActivity.this,"Error: UserName must be filled",Toast.LENGTH_LONG).show();
                }

                else if(cm.getActiveNetworkInfo()== null){
                    Toast.makeText(ForgotPasswordActivity.this,"Error: Connect to internet please",Toast.LENGTH_LONG).show();
                }
                else{

                    isUserNameAlreadyExistInCustomers(ed_userName.getText().toString());
                    isUserNameAlreadyExistInServiceProviders(ed_userName.getText().toString());
                    mprogressdialog.show();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms


                            if(isUserNameAlreadyExistInCustomers==true){
                              getIdOfCustomer(ed_userName.getText().toString()) ;

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Do something after 5s = 5000ms
                                         setNewPasswordtoCustomer(userId);
                                         mprogressdialog.dismiss();

                                      // Toast.makeText(ForgotPasswordActivity.this,"Password set",Toast.LENGTH_LONG).show();

                                    }
                                }, 5000);



                            }
                            else if(isUserNameAlreadyExistInServiceProviders==true){
                                getIdOfServiceProvider(ed_userName.getText().toString());
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Do something after 5s = 5000ms
                                        setNewPasswordtoServiceProvider(userId);
                                        mprogressdialog.dismiss();
                                        // Toast.makeText(ForgotPasswordActivity.this,"Password set",Toast.LENGTH_LONG).show();

                                    }
                                }, 5000);




                            }
                            else{
                                mprogressdialog.dismiss();
                                Toast.makeText(ForgotPasswordActivity.this,"Error: This user Name does not exist",Toast.LENGTH_LONG).show();
                            }

                        }
                    }, 5000);




                }


            }
        });
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

        char[] chars = userName.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(char c : chars) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private void getIdOfCustomer(final String userName){

         DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
         ref.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn:dataSnapshot.getChildren()){
                    Customer customer = ssn.getValue(Customer.class);
                    if(customer.getUserName().equals(userName)) {

                        userId = customer.getId();


                        break;
                    }
                }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

    }

    private void getIdOfServiceProvider(final String userName){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn:dataSnapshot.getChildren()){
                    ServiceProvider serviceProvider = ssn.getValue(ServiceProvider.class);
                    if(serviceProvider.getUserName().equals(userName)) {

                        userId = serviceProvider.getId();


                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }





    String getAlphaNumericString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public void setNewPasswordtoCustomer(String id){
        String newpassword = getAlphaNumericString(9);
        customer_ref.child(id+"/"+"password").setValue(newpassword);
        buildAlertMessageNewPassword(newpassword);

    }
    public void setNewPasswordtoServiceProvider(String id){
        String newpassword = getAlphaNumericString(9);
        sp_ref.child(id+"/"+"password").setValue(newpassword);
        buildAlertMessageNewPassword(newpassword);

    }



    protected void buildAlertMessageNewPassword(String password){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your new password is: "+password+"\n"+
        "Note: Save it in a safe location")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();

                    }
                });


        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}
