package com.example.kaamasaan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.example.kaamasaan.SplashScreenActivity.db;


public class CustomerHomeActivity extends AppCompatActivity {
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("Chats");


        toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.cs_navigation);
        navigation.setItemIconTintList(null);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        if(db!=null) {
            db.execSQL("UPDATE LoginInfo SET IsLoggedIn='true',UserName = '" + MainActivity.mcustomer.getUserName() + "',UserType= 'Customer'");
        }
       Fragment fragment = new CustomerChatsFragment();
        loadFragment(fragment);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.customer_options_menu, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to log out ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          db.execSQL("UPDATE LoginInfo SET IsLoggedIn='false',UserName = 'NoUserName',UserType= 'NoUserType'");
                        //  db.close();
                            startActivity(new Intent(CustomerHomeActivity.this,MainActivity.class));
                            finish();

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
        } else if (item.getItemId() == android.R.id.home) {
           db.close();
           System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(db!=null) {
            db.close();
            System.exit(0);
        }
        else{
            System.exit(0);
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {

                case R.id.navigation_search_sp:
                    startActivity(new Intent(CustomerHomeActivity.this, SearchServiceProviderActivity.class));
                    finish();
                case R.id.navigation_chat:
                    toolbar.setTitle("Chats");
                    fragment = new CustomerChatsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_broadcast:
                    toolbar.setTitle(" Send Broadcast");
                    fragment = new CustomerBroadcastsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_notification:
                    toolbar.setTitle("Notifications");
                    fragment = new CustomerNoficationsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_profile:
                    toolbar.setTitle("Profile");
                    fragment = new CustomerProfileFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



}