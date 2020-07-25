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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;



public class ServiceProviderMessageActivity extends AppCompatActivity {
    ListView lv_chats;
    ArrayList<Message> chatList = new ArrayList();
    String mMessage;
    boolean doeschatHashmapOfCustomerExist;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_message);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        lv_chats = findViewById(R.id.lv_chats);
        Button send = findViewById(R.id.btn_send);
        Button cancel = findViewById(R.id.btn_cancel);
        Button btnSendVisitRequest = findViewById(R.id.btn_send_visit_request);
        final EditText ed_messge =findViewById(R.id.ed_message);
        Intent intent  = getIntent();
        final String customer_UserName;
        customer_UserName = intent.getStringExtra("customer");
        actionbar.setTitle( customer_UserName );

        getLatestMessageFromCustomer(customer_UserName);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentDate();
                if(!ed_messge.getText().toString().isEmpty()){

                    mMessage=ed_messge.getText().toString();
                    ed_messge.setText("");


                    if(doesChatHashmapOfServiceProviderExist()==true){

                        if(doesChatListWithThisCustomerExistInHasmapOfServiceProvider(customer_UserName)==true){
                            HashMap hashMap = MainActivity.mserviceProvider.getChatHashmap();
                            ArrayList<Message> alMessage = (ArrayList<Message>) hashMap.get(customer_UserName);
                            Message message1 = new Message("You", mMessage,getCurrentDate());
                            alMessage.add(message1);
                            chatList = alMessage;
                            CustomAdapterChatList customAdapterChatList =new CustomAdapterChatList();
                            lv_chats.setAdapter( customAdapterChatList );
                            lv_chats.setSelection( customAdapterChatList .getCount() - 1);
                            //  Toast.makeText(getActivity(), chatList.size() + "", Toast.LENGTH_LONG).show();

                            hashMap.put(customer_UserName, alMessage);
                            MainActivity.mserviceProvider.setChatHashmap(hashMap);
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(MainActivity.mserviceProvider.getId());
                            ref.setValue(MainActivity.mserviceProvider);

                            // now manipulate Customer
                            final String customerID = getIDOfCustomer(customer_UserName);
                            doeschatHashmapOfCustomerExist(customerID);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(doeschatHashmapOfCustomerExist==true){
                                        Customer customer = getCustomer(customerID);
                                        HashMap<String, ArrayList<Message>> map = customer.getChatHashmap();
                                        ArrayList<Message> almsg = map.get(MainActivity.mserviceProvider.getUserName());
                                        if(almsg!=null){
                                            Message message = new Message(MainActivity.mserviceProvider.getUserName(), mMessage,getCurrentDate());
                                            almsg.add(message);
                                            map.put(MainActivity.mserviceProvider.getUserName(), almsg);
                                            //    Toast.makeText(getActivity(), "size of message array after updating is: " + almsg.size(), Toast.LENGTH_LONG).show();
                                           /* Customer newCustomer = new Customer(customer.getId(),customer.getFullName(),customer.getUserName()
                                                    ,customer.getPhone(),customer.getPassword(),customer.getImageUrl()
                                                    ,customer.getBroadCastRequestsIdsList(),map);*/
                                            customer.setChatHashmap(map);
                                            final DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(customerID);
                                            customer_ref.setValue(customer);

                                        }
                                        else{// i-e almsg==null
                                            almsg = new ArrayList<>();
                                            Message message = new Message(MainActivity.mserviceProvider.getUserName(), mMessage,getCurrentDate());
                                            almsg.add(message);
                                            map.put(MainActivity.mserviceProvider.getUserName(), almsg);
                                            //    Toast.makeText(getActivity(), "size of message array after updating is: " + almsg.size(), Toast.LENGTH_LONG).show();


                                            customer.setChatHashmap(map);
                                            final DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(customerID);
                                            customer_ref.setValue(customer);

                                        }

                                    }// end if doeschatHashmapOfCustomerExist==true
                                    else if(doeschatHashmapOfCustomerExist==false){
                                        Customer customer = getCustomer(customerID);
                                        HashMap<String, ArrayList<Message>> map = new HashMap<>();
                                        Message message = new Message(MainActivity.mserviceProvider.getUserName(), mMessage,getCurrentDate());
                                        ArrayList<Message> almsg =new ArrayList<>();
                                        almsg.add(message);
                                        map.put(MainActivity.mserviceProvider.getUserName(), almsg);
                                       /* Customer newCustomer = new Customer(customer.getId(),customer.getFullName(),customer.getUserName()
                                                ,customer.getPhone(),customer.getPassword(),customer.getImageUrl()
                                                ,customer.getBroadCastRequestsIdsList(),map);*/
                                        customer.setChatHashmap(map);
                                        final DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(customerID);
                                        customer_ref.setValue(customer);
                                    }// end manipulation of customer

                                }
                            }, 4000);

                        }//end if doesChatListWithThisCustomerExistInHasmapOfServiceProvider(customer_UserName)==true
                        else if(doesChatListWithThisCustomerExistInHasmapOfServiceProvider(customer_UserName)==false){
                            //  Toast.makeText(getActivity(), "declaration of array", Toast.LENGTH_LONG).show();
                            ArrayList<Message> alMessage = new ArrayList<>();
                            Message message1 = new Message("You",mMessage,getCurrentDate());

                            alMessage.add(message1);
                            chatList = alMessage;
                            CustomAdapterChatList customAdapterChatList = new CustomAdapterChatList();
                            lv_chats.setAdapter(customAdapterChatList);
                            lv_chats.setSelection(customAdapterChatList.getCount() - 1);
                            HashMap hashMap = MainActivity.mserviceProvider.getChatHashmap();
                            hashMap.put(customer_UserName, alMessage);
                            MainActivity.mserviceProvider.setChatHashmap(hashMap);
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(MainActivity.mserviceProvider.getId());
                            ref.setValue(MainActivity.mserviceProvider);

                            //now manipulate Customer
                            final String customerID = getIDOfCustomer(customer_UserName);
                            if(doeschatHashmapOfCustomerExist==true){
                                Customer customer = getCustomer(customerID);
                                HashMap<String, ArrayList<Message>> map = customer.getChatHashmap();
                                ArrayList<Message> almsg = map.get(MainActivity.mserviceProvider.getUserName());
                                if(almsg!=null){
                                    Message message = new Message(MainActivity.mserviceProvider.getUserName(), mMessage,getCurrentDate());
                                    almsg.add(message);
                                    map.put(MainActivity.mserviceProvider.getUserName(), almsg);
                                    //   Toast.makeText(getActivity(), "size of message array after updating is: " + almsg.size(), Toast.LENGTH_LONG).show();
                                   /* Customer newCustomer = new Customer(customer.getId(),customer.getFullName(),customer.getUserName()
                                            ,customer.getPhone(),customer.getPassword(),customer.getImageUrl()
                                            ,customer.getBroadCastRequestsIdsList(),map);*/
                                    customer.setChatHashmap(map);
                                    final DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(customerID);
                                    customer_ref.setValue(customer);

                                }
                                else{// i-e almsg==null
                                    almsg = new ArrayList<>();
                                    Message message = new Message(MainActivity.mserviceProvider.getUserName(), mMessage,getCurrentDate());
                                    almsg.add(message);
                                    map.put(MainActivity.mserviceProvider.getUserName(), almsg);
                                    //    Toast.makeText(getActivity(), "size of message array after updating is: " + almsg.size(), Toast.LENGTH_LONG).show();
                                   /* Customer newCustomer = new Customer(customer.getId(),customer.getFullName(),customer.getUserName()
                                            ,customer.getPhone(),customer.getPassword(),customer.getImageUrl()
                                            ,customer.getBroadCastRequestsIdsList(),map);*/
                                    customer.setChatHashmap(map);
                                    final DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(customerID);
                                    customer_ref.setValue(customer);

                                }

                            }// end if doeschatHashmapOfCustomerExist==true
                            else if(doeschatHashmapOfCustomerExist==false){
                                Customer customer = getCustomer(customerID);
                                HashMap<String, ArrayList<Message>> map = new HashMap<>();
                                Message message = new Message(MainActivity.mserviceProvider.getUserName(), mMessage,getCurrentDate());
                                ArrayList<Message> almsg =new ArrayList<>();
                                almsg.add(message);
                                map.put(MainActivity.mserviceProvider.getUserName(), almsg);
                                /*Customer newCustomer = new Customer(customer.getId(),customer.getFullName(),customer.getUserName()
                                        ,customer.getPhone(),customer.getPassword(),customer.getImageUrl()
                                        ,customer.getBroadCastRequestsIdsList(),map);*/
                                customer.setChatHashmap(map);
                                final DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(customerID);
                                customer_ref.setValue(customer);
                            }// end manipulation of customer

                        }

                    }// end if doesChatHashmapOfServiceProviderExist()==true
                    else if(doesChatHashmapOfServiceProviderExist()==false){
                        HashMap<String, ArrayList<Message>> map = new HashMap<>();
                        ArrayList<Message> almsg = new ArrayList<>();
                        Message message = new Message("You", mMessage,getCurrentDate());
                        almsg.add(message);
                        chatList = almsg;
                        CustomAdapterChatList customAdapterChatList = new CustomAdapterChatList();
                        lv_chats.setAdapter(customAdapterChatList);
                        lv_chats.setSelection(customAdapterChatList.getCount()-1);
                        map.put(customer_UserName, almsg);
                        MainActivity.mserviceProvider.setChatHashmap(map);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(MainActivity.mcustomer.getId());
                        ref.setValue(MainActivity.mserviceProvider);


                        //now manipulate Customer
                        final String customerID = getIDOfCustomer(customer_UserName);
                        if(doeschatHashmapOfCustomerExist==true){
                            Customer customer = getCustomer(customerID);
                            HashMap<String, ArrayList<Message>> hashMap = customer.getChatHashmap();
                            ArrayList<Message> almsg1 = hashMap.get(MainActivity.mserviceProvider.getUserName());
                            if(almsg1!=null){
                                Message message1 = new Message(MainActivity.mserviceProvider.getUserName(), mMessage,getCurrentDate());
                                almsg1.add(message1);
                                hashMap.put(MainActivity.mserviceProvider.getUserName(), almsg1);
                                //   Toast.makeText(getActivity(), "size of message array after updating is: " + almsg1.size(), Toast.LENGTH_LONG).show();
                               /* Customer newCustomer = new Customer(customer.getId(),customer.getFullName(),customer.getUserName()
                                        ,customer.getPhone(),customer.getPassword(),customer.getImageUrl()
                                        ,customer.getBroadCastRequestsIdsList(),hashMap);*/
                                customer.setChatHashmap(hashMap);
                                final DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(customerID);
                                customer_ref.setValue(customer);

                            }
                            else{// i-e almsg==null
                                almsg = new ArrayList<>();
                                Message message1 = new Message(MainActivity.mserviceProvider.getUserName(), mMessage,getCurrentDate());
                                almsg.add(message1);
                                map.put(MainActivity.mserviceProvider.getUserName(), almsg);
                                //  Toast.makeText(getActivity(), "size of message array after updating is: " + almsg.size(), Toast.LENGTH_LONG).show();
                              /*  Customer newCustomer = new Customer(customer.getId(),customer.getFullName(),customer.getUserName()
                                        ,customer.getPhone(),customer.getPassword(),customer.getImageUrl()
                                        ,customer.getBroadCastRequestsIdsList(),map);*/
                                customer.setChatHashmap(map);
                                final DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(customerID);
                                customer_ref.setValue(customer);

                            }

                        }// end if doeschatHashmapOfCustomerExist==true
                        else if(doeschatHashmapOfCustomerExist==false){
                            Customer customer = getCustomer(customerID);
                            HashMap<String, ArrayList<Message>> hashMap = new HashMap<>();
                            Message message1 = new Message(MainActivity.mserviceProvider.getUserName(), mMessage,getCurrentDate());
                            ArrayList<Message> almsg1 =new ArrayList<>();
                            almsg1.add(message1);
                            hashMap.put(MainActivity.mserviceProvider.getUserName(), almsg1);
                            /*Customer newCustomer = new Customer(customer.getId(),customer.getFullName(),customer.getUserName()
                                    ,customer.getPhone(),customer.getPassword(),customer.getImageUrl()
                                    ,customer.getBroadCastRequestsIdsList(),hashMap);*/
                            customer.setChatHashmap(hashMap);
                            final DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(customerID);
                            customer_ref.setValue(customer);
                        }// end manipulation of customer
                    }

                }// end if ed_Message is not empty
            }
        });
   cancel.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        ed_messge.setText("");
    }
});

        lv_chats.setLongClickable(true);
        lv_chats.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {

                AlertDialog.Builder alert = new AlertDialog.Builder(
                       ServiceProviderMessageActivity.this);
                alert.setTitle("Alert!!");
                alert.setMessage("Are you sure to delete message?");
                alert.setPositiveButton("YES",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Yes Pressed

                        chatList.remove(position);

                       CustomAdapterChatList customAdapterChatList = new CustomAdapterChatList();
                        lv_chats.setAdapter(customAdapterChatList);
                        lv_chats.setSelection(customAdapterChatList.getCount()-1);

                        HashMap hashMap = MainActivity.mserviceProvider.getChatHashmap();
                        hashMap.put(customer_UserName,chatList);
                        MainActivity.mserviceProvider.setChatHashmap(hashMap);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(MainActivity.mserviceProvider.getId());
                        ref.setValue(MainActivity.mserviceProvider);
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

        btnSendVisitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customerID = getIDOfCustomer(customer_UserName);
                Customer customer = getCustomer(customerID);
                //   Toast.makeText(getActivity(),"customer id before sending intent"+ customer.getReceivedVisitRequestList().size(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ServiceProviderMessageActivity.this, SendVisitRequestActivity.class);
                intent.putExtra("customerid",customerID);
                intent.putExtra("customerUserName",customer.getUserName());
                startActivity(intent);
            }
        });

    }


    private Customer getCustomer(String customerID){
        Customer customer = new Customer();
        for(int i = 0;i<ServiceProviderChatsFragment.alCustomers.size();i++) {
            if (customerID.equals(ServiceProviderChatsFragment.alCustomers.get(i).getId())) {
                customer = ServiceProviderChatsFragment.alCustomers.get(i);
            }
        }
        return customer ;
    }

    private String getIDOfCustomer(String customer_UserName){
        String customerID="";
        for(int i = 0;i<ServiceProviderChatsFragment.alCustomers.size();i++){
            if(customer_UserName.equals(ServiceProviderChatsFragment.alCustomers.get(i).getUserName())){
                customerID= ServiceProviderChatsFragment.alCustomers.get(i).getId();
            }
        }
        return customerID;
    }

    class CustomAdapterChatList extends BaseAdapter {
        public static final int you =0;
        public static  final int customer = 1;

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
            else if(getItemViewType(position)==customer){
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
               return customer;
           }

        }

    }


    public void getLatestMessageFromCustomer(final String customer_UserName){
        final HashMap<String, ArrayList<Message>>[] map = new HashMap[1];
        final DatabaseReference chat_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        chat_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    ServiceProvider serviceProvider = ssn.getValue(ServiceProvider.class);
                    if(serviceProvider.getId().equals(MainActivity.mserviceProvider.getId())){
                        map[0] = serviceProvider.getChatHashmap();
                        if(map[0]==null){
                            // do nothing
                            //   Toast.makeText(getActivity(),"map is null",Toast.LENGTH_LONG).show();
                        }
                        else{
                            MainActivity.mserviceProvider.setChatHashmap(map[0]);
                            chatList = map[0].get(customer_UserName);

                        }
                    }
                }// end  for loop
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

    public String getCurrentDate(){
        long date = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss:a");
        String formattedCurrentDate = df.format(date);

        return formattedCurrentDate;
    }

    private boolean doesChatHashmapOfServiceProviderExist(){
        boolean bool;
        HashMap<String, ArrayList<Message>> map = MainActivity.mserviceProvider.getChatHashmap();
        if(map==null){
            bool =false;
        }
        else{
            bool = true;
        }
        return bool;
    }

    public void doeschatHashmapOfCustomerExist(String customerId){

        final DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(customerId);
        customer_ref.orderByKey().equalTo("chatHashmap").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    doeschatHashmapOfCustomerExist=true;
                    // Toast.makeText(FoundSPMapActivity.this,"hashmap of service provider exists ",Toast.LENGTH_LONG).show();
                }
                else{
                    doeschatHashmapOfCustomerExist=false;
                    // Toast.makeText(FoundSPMapActivity.this,"hashmap of service provider does not exist ",Toast.LENGTH_LONG).show();
                }
                customer_ref.orderByKey().equalTo("chatHashmap").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private boolean doesChatListWithThisCustomerExistInHasmapOfServiceProvider(String customer_UserName){
        final boolean[] bool = {false};

        HashMap<String,ArrayList<Message>> map = MainActivity.mserviceProvider.getChatHashmap();
        ArrayList<Message>alMessage = map.get(customer_UserName);
        if(alMessage==null){
            bool[0]=false;
        }
        else{
            bool[0]=true;
        }
        return bool[0];
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
