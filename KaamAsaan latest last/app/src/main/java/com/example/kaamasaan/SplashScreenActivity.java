package com.example.kaamasaan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SplashScreenActivity extends AppCompatActivity {
    public static SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS LoginInfo(IsLoggedIn Varchar,UserName Varchar, UserType Varchar)");
        //  db.execSQL("Drop Table LoginInfo");
        /* Toast.makeText(MainActivity.this,"Table created",Toast.LENGTH_LONG).show();*/



        Cursor cursor= db.rawQuery("Select *From LoginInfo",null);


        if(cursor.moveToFirst()) {
            //cursor.moveToFirst();

            int count = cursor.getCount();
            /*  Toast.makeText(SplashScreenActivity.this, String.valueOf(count), Toast.LENGTH_SHORT).show();
             Toast.makeText(SplashScreenActivity.this, cursor.getString(cursor.getColumnIndex("IsLoggedIn")), Toast.LENGTH_SHORT).show();*/
            if(cursor.getString(cursor.getColumnIndex("IsLoggedIn")).equals("false")){
                // do nothing i-e stay on Main Activity
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        startActivity(new Intent(SplashScreenActivity.this,MainActivity.class));
                        finish();
                    }
                }, 3000);




                  //   Toast.makeText(SplashScreenActivity.this,"Going to main activity", Toast.LENGTH_SHORT).show();
            }
            else if(cursor.getString(cursor.getColumnIndex("IsLoggedIn")).equals("true")){ //i-e user has already logged in
             //   Toast.makeText(SplashScreenActivity.this, cursor.getString(cursor.getColumnIndex("UserName")), Toast.LENGTH_SHORT).show();
                if(cursor.getString(cursor.getColumnIndex("UserType")).equals("ServiceProvider")){


                    IfServiceProviderAlreadyLoggedIn(cursor.getString(cursor.getColumnIndex("UserName")));
                }
                else if(cursor.getString(cursor.getColumnIndex("UserType")).equals("Customer")){
                    IfCustomerAlreadyLoggedIn(cursor.getString(cursor.getColumnIndex("UserName")));
                }
            }
        }

        else{// i-e table has not initialized or populated
            //  Toast.makeText(SplashScreenActivity.this,"entering initial data into table", Toast.LENGTH_SHORT).show();
            db.execSQL("INSERT INTO LoginInfo VALUES('false','NoUserName','NoUserType')");
            startActivity(new Intent(SplashScreenActivity.this,MainActivity.class));
            finish();
            //   Toast.makeText(SplashScreenActivity.this, "count is 0", Toast.LENGTH_SHORT).show();
        }


    }

    public void IfServiceProviderAlreadyLoggedIn(final String serviceProviderUserName){
        final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        Query query = null;
        if(hasintegers(serviceProviderUserName).length()>0){
            query =  sp_ref  .orderByChild("userName").equalTo(serviceProviderUserName);
        }
        else{
            query =  sp_ref  .orderByChild("userName").equalTo(serviceProviderUserName.toLowerCase()) ;
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    sp_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ssn: dataSnapshot.getChildren()){
                               ServiceProvider serviceProvider = ssn.getValue(ServiceProvider.class);
                                if( serviceProvider.getUserName().equals(serviceProviderUserName)){
                                    MainActivity.mserviceProvider= serviceProvider;
                                 //   Toast.makeText(SplashScreenActivity.this,"Going to SP Home", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SplashScreenActivity.this,ServiceProviderHomeActivity.class));
                                    finish();

                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                else{
                    Toast.makeText(SplashScreenActivity.this,"Some Error Occured:\n" +
                            "Service Provider with this user Name has been deleted From the Database. " +
                            "Create a New Account.",Toast.LENGTH_LONG);
                    db.execSQL("UPDATE LoginInfo SET IsLoggedIn='false',UserName = 'NoUserName',UserType= 'NoUserType'");
                    startActivity(new Intent(SplashScreenActivity.this,MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void IfCustomerAlreadyLoggedIn(final String customerUserName){
      final  DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
        Query query = null;
        if(hasintegers(customerUserName).length()>0){
            query =  customer_ref.orderByChild("userName").equalTo(customerUserName);
        }
        else{
            query =  customer_ref  .orderByChild("userName").equalTo(customerUserName.toLowerCase()) ;
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    customer_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ssn: dataSnapshot.getChildren()){
                                Customer customer = ssn.getValue(Customer.class);
                                if( customer.getUserName().equals(customerUserName)){
                                    MainActivity.mcustomer= customer;

                                  //  Toast.makeText(SplashScreenActivity.this,"Going to Customer Home", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SplashScreenActivity.this,CustomerHomeActivity.class));
                                    finish();

                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    Toast.makeText(SplashScreenActivity.this,"Some Error Occured:\n" +
                            "Customer with this user Name has been deleted From the Database. " +
                            "Create a New Account.",Toast.LENGTH_LONG);
                    db.execSQL("UPDATE LoginInfo SET IsLoggedIn='false',UserName = 'NoUserName',UserType= 'NoUserType'");
                    startActivity(new Intent(SplashScreenActivity.this,MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
}
