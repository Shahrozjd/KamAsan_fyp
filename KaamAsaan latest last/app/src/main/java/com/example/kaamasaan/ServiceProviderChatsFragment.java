package com.example.kaamasaan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceProviderChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceProviderChatsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ProgressDialog mprogressdialog;
    ListView lv;
    public static ArrayList<String>keysList = new ArrayList();
    public static ArrayList<ChatThumbNail>chatThumbNailList = new ArrayList();
    String imageUrl;
    HashMap<String, String> mapProfileImages = new HashMap<>();
    ArrayList<Message>chatList = new ArrayList();
    public static ArrayList<Customer> alCustomers = new ArrayList();
    String mMessage;
    boolean doeschatHashmapOfCustomerExist;
    ListView lv_chats;




    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServiceProviderChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceProviderChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceProviderChatsFragment newInstance(String param1, String param2) {
        ServiceProviderChatsFragment fragment = new ServiceProviderChatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_service_provider_chats, container, false);
        lv = view.findViewById(R.id.lv_customer_chat_thumbnails);
        mprogressdialog = new ProgressDialog(getActivity());
        mprogressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mprogressdialog.setMessage("Loading Chats...");
        mprogressdialog.setIndeterminate(true);
        mprogressdialog.setCancelable(false);

        /*mprogressdialog.show();
            if(doesChatHashmapOfServiceProviderExist()==false){
                Toast.makeText(getActivity(),"No chats found",Toast.LENGTH_LONG).show();
                mprogressdialog.dismiss();
            }
            else{
                //  Toast.makeText(getActivity(),"size of hashmap: "+MainActivity.mserviceProvider.getChatHashmap().size(),Toast.LENGTH_LONG).show();

                for ( String key : MainActivity.mserviceProvider.getChatHashmap().keySet() ) {
                    keysList.add(key);
                }
                for(int i=0;i<keysList.size();i++){
                    getAllCustomerswhomeWithChatExists(keysList.get(i));
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        for(int i=0;i<keysList.size();i++){
                            getImageUrlOfCustomer(keysList.get(i));
                        }


                        for(int i=0;i<keysList.size();i++){
                            ArrayList<Message>almsg = MainActivity.mserviceProvider.getChatHashmap().get(keysList.get(i));
                            Message msg = almsg.get(almsg.size()-1);
                            String imageUrl = mapProfileImages.get(keysList.get(i));

                            ChatThumbNail chatThumbNail = new ChatThumbNail(imageUrl,msg.getUserName(),msg.getMsg(),msg.getCurrentDate());
                            chatThumbNailList.add(chatThumbNail);
                        }
                        CustomAdapter customAdapter = new CustomAdapter();
                        lv.setAdapter(customAdapter);

                        mprogressdialog.dismiss();

                        // item Click Listener
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            *//* Toast.makeText(getActivity(),"list clicked",Toast.LENGTH_LONG).show();
                             Toast.makeText(getActivity(),"name of customer: "+keysList.get(position),Toast.LENGTH_LONG).show();*//*
                                //  showChatDialog(keysList.get(position));
                                Intent intent = new Intent(getActivity(),ServiceProviderMessageActivity.class);
                                intent.putExtra("customer",keysList.get(position));
                                startActivity(intent);


                            }
                        });
                        // long click Listener
                        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(
                                        getActivity());
                                alert.setTitle("Alert!!");
                                alert.setMessage("Are you sure to delete chat with "+keysList.get(position)+" ?");
                                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MainActivity.mserviceProvider.getChatHashmap().remove(keysList.get(position));
                                        keysList.remove(position);
                                        chatThumbNailList.remove(position);
                                        CustomAdapter customAdapter1 = new CustomAdapter();
                                        lv.setAdapter(customAdapter1);

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(MainActivity.mserviceProvider.getId());
                                        databaseReference.setValue( MainActivity.mserviceProvider);
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
                }, 4000);

        }*/

        getLatestChatThumbnails();

        return view;
    }


    private void showChatDialog(final String customer_UserName) {
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.service_provider_message_dialog);
        dialog.setTitle("Chat with " + customer_UserName);






        lv_chats = dialog.findViewById(R.id.lv_chats);
        Button send = dialog.findViewById(R.id.btn_send);
        Button cancel = dialog.findViewById(R.id.btn_cancel);
        Button sendVisitRequest = dialog.findViewById(R.id.btn_send_visit_request);
        final EditText ed_messge = dialog.findViewById(R.id.ed_message);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        sendVisitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customerID = getIDOfCustomer(customer_UserName);
                Customer customer = getCustomer(customerID);
            //   Toast.makeText(getActivity(),"customer id before sending intent"+ customer.getReceivedVisitRequestList().size(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), SendVisitRequestActivity.class);
               intent.putExtra("customerid",customerID);
               intent.putExtra("customerUserName",customer.getUserName());
                startActivity(intent);
            }
        });



        lv_chats.setLongClickable(true);
        lv_chats.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {

                AlertDialog.Builder alert = new AlertDialog.Builder(
                        getActivity());
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

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentDate();
                if(!ed_messge.getText().toString().isEmpty()){

                    mMessage=ed_messge.getText().toString();
                    ed_messge.setText("");

                    //  Toast.makeText(getActivity(),"button pressed",Toast.LENGTH_LONG).show();
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
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        getLatestMessageFromCustomer(customer_UserName);
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

    public void getLatestChatThumbnails(){
        DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        sp_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    ServiceProvider serviceProvider = ssn.getValue(ServiceProvider.class);
                    if(serviceProvider.getId().equals(MainActivity.mserviceProvider.getId())){
                        HashMap<String, ArrayList<Message>> map = serviceProvider.getChatHashmap();
                        if(map==null){
                         //   Toast.makeText(getActivity(),"No Chats Found",Toast.LENGTH_LONG).show();
                        }
                        else{
                            keysList.clear();
                            for ( String key :map.keySet() ) {
                                keysList.add(key);
                            }
                            for(int i=0;i<keysList.size();i++){
                                getAllCustomerswhomeWithChatExists(keysList.get(i));
                            }
                            for(int i=0;i<keysList.size();i++){
                                getImageUrlOfCustomer(keysList.get(i));
                            }

                            chatThumbNailList.clear();
                            for(int i=0;i<keysList.size();i++){
                                ArrayList<Message>almsg = map.get(keysList.get(i));
                                Message msg = almsg.get(almsg.size()-1);
                                String imageUrl = mapProfileImages.get(keysList.get(i));

                                ChatThumbNail chatThumbNail = new ChatThumbNail(imageUrl,msg.getUserName(),msg.getMsg(),msg.getCurrentDate());

                                chatThumbNailList.add(chatThumbNail);

                            }
                            CustomAdapter customAdapter = new CustomAdapter();
                            lv.setAdapter(customAdapter);

                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            /* Toast.makeText(getActivity(),"list clicked",Toast.LENGTH_LONG).show();
                             Toast.makeText(getActivity(),"name of customer: "+keysList.get(position),Toast.LENGTH_LONG).show();*/
                                    //  showChatDialog(keysList.get(position));
                                    Intent intent = new Intent(getActivity(),ServiceProviderMessageActivity.class);
                                    intent.putExtra("customer",keysList.get(position));
                                    startActivity(intent);


                                }
                            });
                            // long click Listener
                            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(
                                            getActivity());
                                    alert.setTitle("Alert!!");
                                    alert.setMessage("Are you sure to delete chat with "+keysList.get(position)+" ?");
                                    alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            MainActivity.mserviceProvider.getChatHashmap().remove(keysList.get(position));
                                            keysList.remove(position);
                                            chatThumbNailList.remove(position);
                                            CustomAdapter customAdapter1 = new CustomAdapter();
                                            lv.setAdapter(customAdapter1);

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(MainActivity.mserviceProvider.getId());
                                            databaseReference.setValue( MainActivity.mserviceProvider);
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




                        }// end else
                    }
                }
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


    private void getImageUrlOfCustomer(final String customer_UserName) {

        String imageUrl = "";
        for(int i=0;i<alCustomers.size();i++){
            if(alCustomers.get(i).getUserName().equals(customer_UserName)){
                imageUrl =  alCustomers.get(i).getImageUrl();
                mapProfileImages.put(customer_UserName,imageUrl);
            }
        }

    }



        class CustomAdapter extends BaseAdapter {

            private class ViewHolder {
                RelativeLayout container;
                GestureDetectorCompat mDetector;
            }

        @Override
        public int getCount() {
            return chatThumbNailList.size();
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
            convertView = getActivity().getLayoutInflater().inflate(R.layout.lv_customer_chats_list_row,null);

            final ViewHolder holder = new ViewHolder();
            holder.container = (RelativeLayout) convertView
                    .findViewById(R.id.relativelayout_container);
            holder.mDetector = new GestureDetectorCompat(getActivity(),
                    new MyGestureListener(getActivity(), convertView));
            convertView.setTag(holder);
           /* holder.container.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    holder.mDetector.onTouchEvent(event);
                    return true;
                }
            });*/
            CircleImageView iv = convertView.findViewById(R.id.iv_prof_pic);
            final TextView txt_user_name =  convertView.findViewById(R.id.txt_user_name);
            TextView txt_message =  convertView.findViewById(R.id.txt_message);
            TextView txt_date_time =  convertView.findViewById(R.id.txt_date_time);

            Glide.with(getActivity()).load(chatThumbNailList.get(position).getImageUrl()).into(iv);
            txt_user_name.setText(keysList.get(position));
            txt_message.setText(chatThumbNailList.get(position).getLastMessage());
            txt_date_time.setText(chatThumbNailList.get(position).getDateAndTime());



            return convertView;
        }
    }





    class CustomAdapterChatList extends BaseAdapter {

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
            convertView = getLayoutInflater().inflate(R.layout.chat_list_row,null);
            RelativeLayout relativeLayout = convertView.findViewById(R.id.chat_list_relative_layout);
            TextView txt_userName = convertView.findViewById(R.id.txt_user_name);
            TextView txt_msg =convertView.findViewById(R.id.txt_message);
            TextView txt_date_time = convertView.findViewById(R.id.txt_date_time);
            txt_userName.setText(chatList.get(position).getUserName()+":");
            txt_msg.setText(chatList.get(position).getMsg());
            txt_date_time.setText(chatList.get(position).getCurrentDate());

            if(chatList.get(position).getUserName().equals("You")){
                txt_userName.setTextColor(getResources().getColor(R.color.colorAccent));
                txt_msg.setTextColor(getResources().getColor(R.color.colorAccent));
                txt_date_time.setTextColor(getResources().getColor(R.color.colorAccent));


            }
            else{
                txt_userName.setTextColor(getResources().getColor(R.color.colorBlue));
                txt_msg.setTextColor(getResources().getColor(R.color.colorBlue));
                txt_date_time.setTextColor(getResources().getColor(R.color.colorBlue));
            }




            return convertView;
        }
    }

    private class ChatThumbNail{
        String imageUrl,Customer_UserName,lastMessage,dateAndTime;

        public ChatThumbNail(String imageUrl, String customer_UserName, String lastMessage, String dateAndTime) {
            this.imageUrl = imageUrl;
            Customer_UserName = customer_UserName;
            this.lastMessage = lastMessage;
            this.dateAndTime = dateAndTime;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getCustomer_UserName() {
            return Customer_UserName;
        }

        public void setCustomer_UserName(String customer_UserName) {
            Customer_UserName = customer_UserName;
        }

        public String getLastMessage() {
            return lastMessage;
        }

        public void setLastMessage(String lastMessage) {
            this.lastMessage = lastMessage;
        }

        public String getDateAndTime() {
            return dateAndTime;
        }

        public void setDateAndTime(String dateAndTime) {
            this.dateAndTime = dateAndTime;
        }
    }


    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int MIN_DISTANCE = 50;
        private static final String TAG = "MyGestureListener";
        private RelativeLayout backLayout;
        private LinearLayout frontLayout;
        private Animation inFromRight,outToRight,outToLeft,inFromLeft;
        public MyGestureListener(Context ctx, View convertView) {
            backLayout = (RelativeLayout) convertView.findViewById(R.id.relativelayout_container);
          //  frontLayout = (LinearLayout) convertView.findViewById(R.id.layout_front);
            inFromRight = AnimationUtils.loadAnimation(ctx, R.anim.in_from_right);
            outToRight = AnimationUtils.loadAnimation(ctx, R.anim.out_to_right);
            outToLeft = AnimationUtils.loadAnimation(ctx, R.anim.out_to_left);
            inFromLeft = AnimationUtils.loadAnimation(ctx, R.anim.in_from_left);
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > MIN_DISTANCE) {
                    if(diffX<0){
                        Log.v(TAG, "Swipe Right to Left");
                        if(backLayout.getVisibility()!=View.GONE){
                            backLayout.startAnimation(outToLeft);
                            backLayout.setVisibility(View.GONE);

                        }

                    }
                }
            }


            return true;
        }
    }


    private Customer getCustomer(String customerID){
        Customer customer = new Customer();
        for(int i = 0;i<alCustomers.size();i++) {
            if (customerID.equals(alCustomers.get(i).getId())) {
                customer = alCustomers.get(i);
            }
        }
        return customer ;
    }

    private String getIDOfCustomer(String customer_UserName){
        String customerID="";
        for(int i = 0;i<alCustomers.size();i++){
            if(customer_UserName.equals(alCustomers.get(i).getUserName())){
                customerID= alCustomers.get(i).getId();
            }
        }
        return customerID;
    }

    public String getCurrentDate(){
        long date = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss:a");
        String formattedCurrentDate = df.format(date);

        return formattedCurrentDate;
    }

    public void getAllCustomerswhomeWithChatExists(final String userName){
        DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
        sp_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                   Customer customer= ssn.getValue(Customer.class);
                    if(customer.getUserName().equals(userName)){
                        alCustomers.add(customer);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    @Override
    public void onResume() {
       super.onResume();



    }
} // end main class i-e chats Fragment



