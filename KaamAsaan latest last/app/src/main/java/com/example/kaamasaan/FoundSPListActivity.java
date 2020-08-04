package com.example.kaamasaan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.kaamasaan.FoundSPMapActivity.alServiceProviders;

public class FoundSPListActivity extends AppCompatActivity {
    public static boolean returnedFromListActivity = false;
    ListView lv;
    Typeface mfont;
    Button btn_view_map;
    ArrayList<Message>chatList = new ArrayList();
    boolean doeschatHashmapOfCustomerExist;
    boolean doeschatHashmapOfServiceProviderExist;
    String mMessage;
    ArrayList<WorkRating>alWorkRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_s_p_list);
        lv = findViewById(R.id.lv_found_sp);
        mfont = Typeface.createFromAsset(this.getAssets(), "fonts/KaushanScript-Regular.otf");
        btn_view_map = findViewById(R.id.btn_view_in_map);
        CustomAdapter customAdapter = new CustomAdapter();
        lv.setAdapter(customAdapter);
        btn_view_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        returnedFromListActivity = true;
        alServiceProviders.clear();
        super.onBackPressed();

    }
    private void showProfileDialog(final String sp_UserName,String imageUrl,int rating,int noOfReviews,int noOfJobs,
                                   ServiceProvider serviceProvider){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_serviceprovider_profile);
        dialog.setTitle(sp_UserName);
        // Toast.makeText(FoundSPMapActivity.this,sp_UserName,Toast.LENGTH_LONG).show();
        de.hdodenhof.circleimageview.CircleImageView iv = dialog.findViewById(R.id.iv_prof_pic);
        TextView tv = dialog.findViewById(R.id.txt_name_dialog);
        TextView txt_reviews = dialog.findViewById(R.id.txt_reviews);
        TextView txt_jobs = dialog.findViewById(R.id.txt_jobs);
        TextView ratingView = dialog.findViewById(R.id.rating_view);
        ListView lv_reviews = dialog.findViewById(R.id.lv_reviews);
        RatingBar ratingBar5;
        txt_reviews.setText("Reviews:"+noOfReviews);
        txt_jobs.setText("Jobs:"+noOfJobs);
        ratingBar5 = dialog.findViewById(R.id.ratingbar5);
        tv.setText(sp_UserName);
ratingView.setText(rating+"");
            ratingBar5.setRating(rating);
        txt_jobs.setVisibility(View.GONE);
        Glide.with(getApplicationContext()).load(imageUrl).into(iv);
        alWorkRating = serviceProvider.getWorkRatingList();
        if(alWorkRating==null){
            // do nothing
        }
        else{
           ReviewsAdapter reviewsAdapter = new ReviewsAdapter();
            lv_reviews.setAdapter(reviewsAdapter);
        }

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }



    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return alServiceProviders.size();
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
            convertView = getLayoutInflater().inflate(R.layout.found_sp_list_row, null);
            TextView name = convertView.findViewById(R.id.txt_name);
            TextView profession = convertView.findViewById(R.id.txt_profession);
            TextView bio = convertView.findViewById(R.id.txt_bio);
            TextView lbl_jobs = convertView.findViewById(R.id.lbl_job);
            TextView lbl_rating = convertView.findViewById(R.id.lbl_rating);
            Button btn_visit_profile = convertView.findViewById(R.id.btn_visit_profile);
            ImageButton btnSendMessage = convertView.findViewById(R.id.btn_chat_with_sp);
            name.setTypeface(mfont);
            profession.setTypeface(mfont);
            bio.setTypeface(mfont);
            lbl_jobs.setTypeface(mfont);
            lbl_rating.setTypeface(mfont);
            int rating=0,noOfReviews=0, noOfJobs=0;
            name.setText(alServiceProviders.get(position).getUserName());
            profession.setText(alServiceProviders.get(position).getCategory());
            bio.setText(alServiceProviders.get(position).getBio());
            rating = (alServiceProviders.get(position).responseRating+alServiceProviders.get(position).getAverageWorkRating())/2;
           lbl_rating.setText("Rating: "+rating);
            if(alServiceProviders.get(position).getWorkRatingList()!=null) {
                noOfReviews = alServiceProviders.get(position).getWorkRatingList().size();
            }
            if(alServiceProviders.get(position).getCompletedWorkList()!=null) {
                noOfJobs = alServiceProviders.get(position).getCompletedWorkList().size();
                lbl_jobs.setText("Jobs: "+noOfJobs);
            }




            btnSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showChatDialog(alServiceProviders.get(position).getUserName());
                }
            });

            final int finalRating = rating;
            final int finalNoOfReviews = noOfReviews;
            final int finalNoOfJobs = noOfJobs;
            btn_visit_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProfileDialog(alServiceProviders.get(position).getUserName(),alServiceProviders.get(position).getProfilePicUrl(), finalRating, finalNoOfReviews,
                            finalNoOfJobs,alServiceProviders.get(position));
                }
            });

            return convertView;
        }
    }


    private Dialog showCustomDialog(double lat, double longi) {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.message_dialog);
        dialog.setTitle("Details");


        final TextView name = dialog.findViewById(R.id.txt_name);
        TextView profession = dialog.findViewById(R.id.txt_profession);
        TextView bio = dialog.findViewById(R.id.txt_bio);
        TextView lbl_jobs = dialog.findViewById(R.id.lbl_job);
        TextView lbl_rating = dialog.findViewById(R.id.lbl_rating);
        ImageButton sendMessage = dialog.findViewById(R.id.btn_chat_with_sp);

        name.setTypeface(mfont);
        profession.setTypeface(mfont);
        bio.setTypeface(mfont);
        lbl_jobs.setTypeface(mfont);
        lbl_rating.setTypeface(mfont);
        for (int i = 0; i < alServiceProviders.size(); i++) {
            if (lat == alServiceProviders.get(i).latititude && longi == alServiceProviders.get(i).longitude) {
                name.setText(alServiceProviders.get(i).getUserName());
                profession.setText(alServiceProviders.get(i).getCategory());
                bio.setText(alServiceProviders.get(i).getBio());
            }
        }
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChatDialog(name.getText().toString());

            }
        });

        return dialog;
    }


    private void showChatDialog(final String sp_UserName) {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.message_dialog);
        dialog.setTitle(sp_UserName);

        final ListView lv_chat = dialog.findViewById(R.id.lv_chats);
        Button send = dialog.findViewById(R.id.btn_send);
        Button cancel = dialog.findViewById(R.id.btn_cancel);
        final EditText ed_messge = dialog.findViewById(R.id.ed_message);


                if (doesChatHashmapOfCustomerExist() == true) {

                    if (doesChatListWithThisServiceProviderExistInHasmapOfCustomer(sp_UserName) == true) {
                      //  Toast.makeText(FoundSPListActivity.this, "ChatList With This ServiceProvider Exists In Hashmap Of Customer ", Toast.LENGTH_LONG).show();
                        HashMap hashMap = MainActivity.mcustomer.getChatHashmap();
                        ArrayList<Message> alMessage = (ArrayList<Message>) hashMap.get(sp_UserName);
                        chatList = alMessage;
                        CustomAdapterChatList customAdapter = new CustomAdapterChatList();
                        lv_chat.setAdapter(customAdapter);
                        lv_chat.setSelection(customAdapter.getCount()-1);
                    }
                }

        lv_chat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(
                                FoundSPListActivity.this);
                        alert.setTitle("Alert!!");
                        alert.setMessage("Are you sure to delete message?");
                        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Yes Pressed

                                chatList.remove(position);

                              CustomAdapterChatList customAdapterChatList = new CustomAdapterChatList();
                                lv_chat.setAdapter(customAdapterChatList);
                                lv_chat.setSelection(customAdapterChatList.getCount()-1);

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
                           // Toast.makeText(FoundSPListActivity.this, "ChatList With This ServiceProvider Exists In Hashmap Of Customer ", Toast.LENGTH_LONG).show();
                            HashMap hashMap = MainActivity.mcustomer.getChatHashmap();
                            ArrayList<Message> alMessage = (ArrayList<Message>) hashMap.get(sp_UserName);


                            Message message1 = new Message("You", mMessage,getCurrentDate());

                            alMessage.add(message1);
                            chatList = alMessage;
                            CustomAdapterChatList customAdapter = new CustomAdapterChatList();
                            lv_chat.setAdapter(customAdapter);
                            lv_chat.setSelection(customAdapter.getCount() - 1);
                           // Toast.makeText(FoundSPListActivity.this, chatList.size() + "", Toast.LENGTH_LONG).show();

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
                                         //   Toast.makeText(FoundSPListActivity.this, "size of message array is not null", Toast.LENGTH_LONG).show();

                                            Message message = new Message(MainActivity.mcustomer.getUserName(), mMessage,getCurrentDate());
                                            almsg.add(message);
                                         //   Toast.makeText(FoundSPListActivity.this, "size of message array after updating is: " + almsg.size(), Toast.LENGTH_LONG).show();
                                            map.put(MainActivity.mcustomer.getUserName(), almsg);

                                           serviceProvider.setChatHashmap(map);
                                            final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(spID);
                                            sp_ref.setValue(serviceProvider);

                                        } else {//i-e arrayList is null
                                         //   Toast.makeText(FoundSPListActivity.this, "array is null ", Toast.LENGTH_LONG).show();
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
                          //  Toast.makeText(FoundSPListActivity.this, "declaration of array", Toast.LENGTH_LONG).show();
                            ArrayList<Message> alMessage = new ArrayList<>();
                            Message message1 = new Message("You",mMessage,getCurrentDate());

                            alMessage.add(message1);
                            chatList = alMessage;
                            CustomAdapterChatList customAdapterChatList = new CustomAdapterChatList();
                            lv_chat.setAdapter(customAdapterChatList);
                            lv_chat.setSelection(customAdapterChatList.getCount() - 1);
                            HashMap hashMap = MainActivity.mcustomer.getChatHashmap();
                            hashMap.put(sp_UserName, alMessage);
                            Customer customer = new Customer(MainActivity.mcustomer.getId(), MainActivity.mcustomer.getFullName(),
                                    MainActivity.mcustomer.getUserName(), MainActivity.mcustomer.getPhone(), MainActivity.mcustomer.getPassword(),
                                    MainActivity.mcustomer.getImageUrl(), MainActivity.mcustomer.getBroadCastRequestsIdsList(), hashMap,MainActivity.mcustomer.getReceivedVisitRequestList(),
                                    MainActivity.mcustomer.getPendingVisitsList(),MainActivity.mcustomer.getCompletedVisitsList()
                                    ,MainActivity.mcustomer.getResponseTimeRatingList(),MainActivity.mcustomer.getReceivedWorkRequestList(), MainActivity.mcustomer.getPendingWorkList(),
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
                    }
                    else if (doesChatHashmapOfCustomerExist() == false) {
                      //  Toast.makeText(FoundSPListActivity.this, "false else if part", Toast.LENGTH_LONG).show();
                        HashMap<String, ArrayList<Message>> map = new HashMap<>();
                        ArrayList<Message> almsg = new ArrayList<>();
                        Message message = new Message("You", mMessage,getCurrentDate());
                        almsg.add(message);
                        chatList = almsg;
                        CustomAdapterChatList customAdapterChatList = new CustomAdapterChatList();
                        lv_chat.setAdapter(customAdapterChatList);
                        lv_chat.setSelection(customAdapterChatList.getCount()-1);

                        map.put(sp_UserName, almsg);
                        Customer customer = new Customer(MainActivity.mcustomer.getId(), MainActivity.mcustomer.getFullName(), MainActivity.mcustomer.getUserName(),
                                MainActivity.mcustomer.getPhone(), MainActivity.mcustomer.getPassword(), MainActivity.mcustomer.getImageUrl(),
                                MainActivity.mcustomer.getBroadCastRequestsIdsList(), map,MainActivity.mcustomer.getReceivedVisitRequestList(),
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
                            HashMap<String, ArrayList<Message>> hashmap = serviceProvider.getChatHashmap();
                            ArrayList<Message> almessage = map.get(MainActivity.mcustomer.getUserName());
                            if (almessage != null) {
                                Message msg = new Message(MainActivity.mcustomer.getUserName(), mMessage,getCurrentDate());
                                almessage.add(msg);


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
       // Toast.makeText(FoundSPListActivity.this,"checking hashMap existence of customer",Toast.LENGTH_LONG).show();


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
       // Toast.makeText(FoundSPListActivity.this,"checking chat array existence of customer",Toast.LENGTH_LONG).show();
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

    class CustomAdapterChatList extends BaseAdapter {
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
            de.hdodenhof.circleimageview.CircleImageView iv = convertView.findViewById(R.id.iv);
            TextView txt_name = convertView.findViewById(R.id.txt_username);
            final TextView txt_review = convertView.findViewById(R.id.txt_review);
            RatingBar ratingBar = convertView.findViewById(R.id.ratingbar);


            txt_name.setText(alWorkRating.get(position).getUserNameOfUser());
            txt_review.setText(alWorkRating.get(position).getReview());
            ratingBar.setRating(alWorkRating.get(position).getRating());
            Glide.with(FoundSPListActivity.this).load(alWorkRating.get(position).getImageUrlOfUser()).into(iv);



            return convertView;

        }

        public void showReviewDetailsDialog(int position){
            View view =  getLayoutInflater().inflate(R.layout.review_detail_dialog,null);

            AlertDialog.Builder builder = new AlertDialog.Builder(FoundSPListActivity.this,android.R.style.Theme_Holo_Dialog_NoActionBar);
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
            Glide.with(FoundSPListActivity.this).load(alWorkRating.get(position).getImageUrlOfUser()).into(iv);

            button_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });



            alertDialog.show();


        }




    }
}