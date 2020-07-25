package com.example.kaamasaan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomerMessageActivity extends AppCompatActivity {
    ListView lv_chats;
    ArrayList<Message>chatList = new ArrayList();
    String mMessage;
    boolean doeschatHashmapOfServiceProviderExist;
    Toolbar toolbar;
   // ArrayList<ServiceProvider>alServiceProviders = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_message);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        lv_chats = findViewById(R.id.lv_chats);
        Button send = findViewById(R.id.btn_send);
        final EditText ed_messge =findViewById(R.id.ed_message);


        final String sp_UserName;
        ed_messge.requestFocusFromTouch();
        Intent i = getIntent();

       // alServiceProviders = i.getParcelableArrayListExtra("splist");
        sp_UserName = i.getStringExtra("serviceprovider");
        actionbar.setTitle( sp_UserName  );




        getLatestMessageFromServiceProider(sp_UserName);



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ed_messge.getText().toString().isEmpty()) {
                    mMessage = ed_messge.getText().toString();
                    ed_messge.setText("");
                    if (doesChatHashmapOfCustomerExist() == true) {

                        if (doesChatListWithThisServiceProviderExistInHasmapOfCustomer(sp_UserName) == true) {
                           //   Toast.makeText(CustomerMessageActivity.this, "ChatList With This ServiceProvider Exists In Hashmap Of Customer ", Toast.LENGTH_LONG).show();
                            HashMap<String,ArrayList<Message>> hashMap = MainActivity.mcustomer.getChatHashmap();
                            ArrayList<Message> alMessage = (ArrayList<Message>) hashMap.get(sp_UserName);


                            Message message1 = new Message("You", mMessage,getCurrentDate());

                            alMessage.add(message1);
                            chatList = alMessage;
                            CustomAdapterChatList customAdapter = new CustomAdapterChatList();
                            lv_chats.setAdapter(customAdapter);
                            lv_chats.setSelection(customAdapter.getCount() - 1);
                            //  Toast.makeText(getActivity(), chatList.size() + "", Toast.LENGTH_LONG).show();
                            //  Toast.makeText(getActivity(), "alsize :"+ chatList.size(), Toast.LENGTH_LONG).show();

                            hashMap.put(sp_UserName, alMessage);

                            MainActivity.mcustomer.setChatHashmap(hashMap);
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(MainActivity.mcustomer.getId());
                            ref.setValue(MainActivity.mcustomer);


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
                                              //  Toast.makeText(CustomerMessageActivity.this, "almsg is not null", Toast.LENGTH_LONG).show();

                                            Message message = new Message(MainActivity.mcustomer.getUserName(), mMessage,getCurrentDate());
                                            almsg.add(message);
                                            //     Toast.makeText(getActivity(), "size of message array after updating is: " + almsg.size(), Toast.LENGTH_LONG).show();
                                            map.put(MainActivity.mcustomer.getUserName(), almsg);


                                            serviceProvider.setChatHashmap(map);
                                            final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(spID);
                                            sp_ref.setValue(serviceProvider);


                                        } else {//i-e arrayList is null
                                             //   Toast.makeText(CustomerMessageActivity.this, "almsg is null ", Toast.LENGTH_LONG).show();
                                            almsg = new ArrayList<>();
                                            Message message = new Message(MainActivity.mcustomer.getUserName(), mMessage,getCurrentDate());
                                            almsg.add(message);
                                            map.put(MainActivity.mcustomer.getUserName(), almsg);

                                            serviceProvider.setChatHashmap(map);
                                           final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(spID);
                                            sp_ref.setValue(serviceProvider);

                                        }
                                    } else if (doeschatHashmapOfServiceProviderExist == false) {
                                          //  Toast.makeText(CustomerMessageActivity.this,"map of sp does not exist",Toast.LENGTH_LONG).show();
                                        ServiceProvider serviceProvider = getServiceProvider(spID);
                                       // Toast.makeText(CustomerMessageActivity.this,"sp user name: "+serviceProvider.getUserName(),Toast.LENGTH_LONG).show();
                                       // Toast.makeText(CustomerMessageActivity.this,"sp visit cost: "+serviceProvider.getVisitCost()+"",Toast.LENGTH_LONG).show();

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
                            //  Toast.makeText(getActivity(), "declaration of array", Toast.LENGTH_LONG).show();
                            ArrayList<Message> alMessage = new ArrayList<>();
                            Message message1 = new Message("You", mMessage,getCurrentDate());
                            alMessage.add(message1);
                            chatList = alMessage;
                            CustomAdapterChatList customAdapter = new CustomAdapterChatList();
                            lv_chats.setAdapter(customAdapter);
                            lv_chats.setSelection(customAdapter.getCount()-1);
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

                                serviceProvider.setChatHashmap(map);
                                final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(spID);
                                sp_ref.setValue(serviceProvider);

                            }// end manipulation of service provider

                        }
                    } else if (doesChatHashmapOfCustomerExist() == false) {
                        //   Toast.makeText(getActivity(), "false else if part", Toast.LENGTH_LONG).show();
                        HashMap<String, ArrayList<Message>> map = new HashMap<>();
                        ArrayList<Message> almsg = new ArrayList<>();
                        Message message = new Message("You",mMessage,getCurrentDate());
                        almsg.add(message);
                        chatList = almsg;
                        CustomAdapterChatList customAdapter = new CustomAdapterChatList();
                        lv_chats.setAdapter(customAdapter);
                        lv_chats.setSelection(customAdapter.getCount()-1);

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


        lv_chats.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        CustomerMessageActivity.this);

                alert.setTitle("Alert!!");
                alert.setMessage("Are you sure to delete message?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Yes Pressed

                        chatList.remove(position);

                        CustomAdapterChatList customAdapterChatList = new CustomAdapterChatList();
                        lv_chats.setAdapter(customAdapterChatList);
                        lv_chats.setSelection(customAdapterChatList.getCount()-1);

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




    }

    private boolean doesChatHashmapOfCustomerExist(){
        boolean bool;
        HashMap<String, ArrayList<Message>> map = MainActivity.mcustomer.getChatHashmap();
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
            //   Toast.makeText(getActivity(),"size of list in method:  "+alMessage.size(),Toast.LENGTH_LONG).show();
            bool[0]=true;
        }
        //  Toast.makeText(getActivity(),"checking chat array existence of customer",Toast.LENGTH_LONG).show();
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
    private ServiceProvider getServiceProvider(String spID){
        ServiceProvider serviceProvider = new ServiceProvider();
        for(int i = 0;i<CustomerChatsFragment.alServiceProviders.size();i++) {
            if (spID.equals(CustomerChatsFragment.alServiceProviders.get(i).getId())) {
                serviceProvider =CustomerChatsFragment.alServiceProviders.get(i);
            }
        }
        return serviceProvider ;
    }

    private String getIDOfServiceProvider(String sp_UserName){
        String spID="";
        for(int i = 0;i<CustomerChatsFragment.alServiceProviders.size();i++){
            if(sp_UserName.equals(CustomerChatsFragment.alServiceProviders.get(i).getUserName())){
                spID = CustomerChatsFragment.alServiceProviders.get(i).getId();
            }
        }
        return spID;
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

                    CustomAdapterChatList customAdapterChatList = new CustomAdapterChatList();
                    lv_chats.setAdapter(customAdapterChatList);
                    lv_chats.setSelection(customAdapterChatList.getCount() - 1);
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home){
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
