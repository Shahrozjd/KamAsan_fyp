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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerChatsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ProgressDialog mprogressdialog;
    ListView lv;
    ArrayList<String> keysList = new ArrayList();
    ArrayList<ChatThumbNail>chatThumbNailList = new ArrayList();
    String imageUrl;
    HashMap<String, String> mapProfileImages = new HashMap<>();
    boolean doeschatHashmapOfServiceProviderExist;
    ArrayList<Message>chatList = new ArrayList();

    String mMessage;
    public static ArrayList<ServiceProvider>alServiceProviders = new ArrayList<>();
    ListView lv_chats;
    boolean isDialogOpened = false;
    String globalSPUserName;

  //  final Handler handler = new Handler();
    Runnable  r = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustmerChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerChatsFragment newInstance(String param1, String param2) {
        CustomerChatsFragment fragment = new CustomerChatsFragment();
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
        View view = inflater.inflate(R.layout.fragment_customer_chats, container, false);
        lv = view.findViewById(R.id.lv_service_provider_chat_thumbnails);
        mprogressdialog = new ProgressDialog(getActivity());
        mprogressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mprogressdialog.setMessage("Loading Chats...");
        mprogressdialog.setIndeterminate(true);
        mprogressdialog.setCancelable(false);
/*
        mprogressdialog.show();
        if(doesChatHashmapOfCustomerExist()==false){
           Toast.makeText(getActivity(),"No chats found",Toast.LENGTH_LONG).show();
            mprogressdialog.dismiss();
        }
        else{

            for ( String key : MainActivity.mcustomer.getChatHashmap().keySet() ) {
                keysList.add(key);
            }

            for(int i=0;i<keysList.size();i++){
                getAllServiceProviderswhomeWithChatExists(keysList.get(i));
            }



            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                    for(int i=0;i<keysList.size();i++){
                        getImageUrlsOfServiceProviders(keysList.get(i));
                    }
                    // Toast.makeText(getActivity(),"size of images Map: "+ mapProfileImages.size(), Toast.LENGTH_LONG).show();
                    for(int i=0;i<keysList.size();i++){
                        ArrayList<Message>almsg = MainActivity.mcustomer.getChatHashmap().get(keysList.get(i));
                        Message msg = almsg.get(almsg.size()-1);
                        String imageUrl = mapProfileImages.get(keysList.get(i));

                        ChatThumbNail chatThumbNail = new ChatThumbNail(imageUrl,msg.getUserName(),msg.getMsg(),msg.getCurrentDate());
                        chatThumbNailList.add(chatThumbNail);
                    }
                   CustomAdapter customAdapter = new CustomAdapter();
                    lv.setAdapter(customAdapter);
                    mprogressdialog.dismiss();

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                         //   showChatDialog(keysList.get(position));
                            String spname = keysList.get(position);
                            Intent intent = new Intent(getActivity(),CustomerMessageActivity.class);
                            intent.putExtra("serviceprovider",spname);
                          //  intent.putParcelableArrayListExtra("splist",alServiceProviders);
                            startActivity(intent);

                        }
                    });

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
                                    MainActivity.mcustomer.getChatHashmap().remove(keysList.get(position));
                                    keysList.remove(position);
                                    chatThumbNailList.remove(position);
                                    CustomAdapter customAdapter1 = new CustomAdapter();
                                    lv.setAdapter(customAdapter1);

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(MainActivity.mcustomer.getId());
                                    databaseReference.setValue( MainActivity.mcustomer);
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
            }, 5000);
        }*/

   getLatestChatThumbnails();

        return  view;
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
    private void getImageUrlsOfServiceProviders(final String SP_UserName) {
        String imageUrl = "";
         for(int i=0;i<alServiceProviders.size();i++){
             if(alServiceProviders.get(i).getUserName().equals(SP_UserName)){
                imageUrl =  alServiceProviders.get(i).getProfilePicUrl();
                 mapProfileImages.put(SP_UserName,imageUrl);
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
            final CustomAdapter.ViewHolder holder = new CustomAdapter.ViewHolder();
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
            AppCompatImageView iv = convertView.findViewById(R.id.iv_prof_pic);
            TextView txt_user_name =  convertView.findViewById(R.id.txt_user_name);
            TextView txt_message =  convertView.findViewById(R.id.txt_message);
            TextView txt_date_time =  convertView.findViewById(R.id.txt_date_time);

            Glide.with(getActivity()).load(chatThumbNailList.get(position).getImageUrl()).into(iv);
            txt_user_name.setText(keysList.get(position));
            txt_message.setText(chatThumbNailList.get(position).getLastMessage());
            txt_date_time.setText(chatThumbNailList.get(position).getDateAndTime());


            return convertView;
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

    private class ChatThumbNail{
        String imageUrl,sp_UserName,lastMessage,dateAndTime;

        public ChatThumbNail(String imageUrl, String sp_UserName, String lastMessage, String dateAndTime) {
            this.imageUrl = imageUrl;
            this.sp_UserName = sp_UserName;
            this.lastMessage = lastMessage;
            this.dateAndTime = dateAndTime;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getSP_UserName() {
            return sp_UserName;
        }

        public void setSP_UserName(String customer_UserName) {
            sp_UserName = customer_UserName;
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

    // functions for real time chat

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

    private void showChatDialog(final String sp_UserName) {
        final Dialog dialog = new Dialog(getActivity());
        globalSPUserName = sp_UserName;
        isDialogOpened=true;
        dialog.setContentView(R.layout.message_dialog);
        dialog.setTitle("Chat with " + sp_UserName);
        dialog.setCancelable(false);





        lv_chats = dialog.findViewById(R.id.lv_chats);
        Button send = dialog.findViewById(R.id.btn_send);
        Button cancel = dialog.findViewById(R.id.btn_cancel);
        final EditText ed_messge = dialog.findViewById(R.id.ed_message);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   handler.removeCallbacks(r);
                isDialogOpened=false;
                dialog.cancel();
            }
        });

        if (doesChatHashmapOfCustomerExist() == true) {

            if (doesChatListWithThisServiceProviderExistInHasmapOfCustomer(sp_UserName) == true) {
               // Toast.makeText(getActivity(), "ChatList With This ServiceProvider Exists In Hashmap Of Customer ", Toast.LENGTH_LONG).show();
                HashMap hashMap = MainActivity.mcustomer.getChatHashmap();
                ArrayList<Message> alMessage = (ArrayList<Message>) hashMap.get(sp_UserName);
                chatList = alMessage;
               // Toast.makeText(getActivity(), "size of chatlist: "+chatList.size(), Toast.LENGTH_LONG).show();
               CustomAdapterChatList customAdapter = new CustomAdapterChatList();
                lv_chats.setAdapter(customAdapter);
                lv_chats.setSelection(customAdapter.getCount()-1);
            }
        }
        lv_chats.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        getActivity());

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


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ed_messge.getText().toString().isEmpty()) {
                    mMessage = ed_messge.getText().toString();
                    ed_messge.setText("");
                    if (doesChatHashmapOfCustomerExist() == true) {

                        if (doesChatListWithThisServiceProviderExistInHasmapOfCustomer(sp_UserName) == true) {
                          //  Toast.makeText(getActivity(), "ChatList With This ServiceProvider Exists In Hashmap Of Customer ", Toast.LENGTH_LONG).show();
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
                                        //    Toast.makeText(getActivity(), "size of message array is null", Toast.LENGTH_LONG).show();

                                            Message message = new Message(MainActivity.mcustomer.getUserName(), mMessage,getCurrentDate());
                                            almsg.add(message);
                                       //     Toast.makeText(getActivity(), "size of message array after updating is: " + almsg.size(), Toast.LENGTH_LONG).show();
                                            map.put(MainActivity.mcustomer.getUserName(), almsg);

                                           serviceProvider.setChatHashmap(map);

                                            final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(spID);
                                            sp_ref.setValue(serviceProvider);


                                        } else {//i-e arrayList is null
                                        //    Toast.makeText(getActivity(), "array is null ", Toast.LENGTH_LONG).show();
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
                         //  Toast.makeText(getActivity(), "declaration of array", Toast.LENGTH_LONG).show();
                            ArrayList<Message> alMessage = new ArrayList<>();
                            Message message1 = new Message("You", mMessage,getCurrentDate());
                            alMessage.add(message1);
                            chatList = alMessage;
                            CustomAdapter customAdapter = new CustomAdapter();
                            lv_chats.setAdapter(customAdapter);
                            lv_chats.setSelection(customAdapter.getCount()-1);
                            HashMap hashMap = MainActivity.mcustomer.getChatHashmap();
                            hashMap.put(sp_UserName, alMessage);
                            Customer customer = new Customer(MainActivity.mcustomer.getId(), MainActivity.mcustomer.getFullName(),
                                    MainActivity.mcustomer.getUserName(), MainActivity.mcustomer.getPhone(), MainActivity.mcustomer.getPassword(),
                                    MainActivity.mcustomer.getImageUrl(), MainActivity.mcustomer.getBroadCastRequestsIdsList(), hashMap
                                    ,MainActivity.mcustomer.getReceivedVisitRequestList(),
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

            }// end onClick
        });




       dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
      getLatestMessageFromServiceProider(sp_UserName);



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

    private ServiceProvider getServiceProvider(String spID){
        ServiceProvider serviceProvider = new ServiceProvider();
        for(int i = 0;i<alServiceProviders.size();i++) {
            if (spID.equals(alServiceProviders.get(i).getId())) {
                serviceProvider = alServiceProviders.get(i);
            }
        }
        return serviceProvider ;
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

    public String getCurrentDate(){
        long date = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss:a");
        String formattedCurrentDate = df.format(date);

        return formattedCurrentDate;
    }

    public void getAllServiceProviderswhomeWithChatExists(final String userName){
      DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
      sp_ref.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              for(DataSnapshot ssn: dataSnapshot.getChildren()){
                  ServiceProvider serviceProvider = ssn.getValue(ServiceProvider.class);
                  if(serviceProvider.getUserName().equals(userName)){
                      alServiceProviders.add(serviceProvider);
                  }
              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });

    }

    public void getLatestMessageFromServiceProider(final String sp_UserName){
        final HashMap<String, ArrayList<Message>>[] map = new HashMap[1];
       // Toast.makeText(getActivity(),"in on change ",Toast.LENGTH_LONG).show();
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


    public void getLatestChatThumbnails(){
        DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
        sp_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                   Customer customer= ssn.getValue(Customer.class);
                    if(customer.getId().equals(MainActivity.mcustomer.getId())){
                        HashMap<String, ArrayList<Message>> map = customer.getChatHashmap();
                        if(map==null){
                           // Toast.makeText(getActivity(),"No Chats Found",Toast.LENGTH_LONG).show();
                        }
                        else{
                             keysList.clear();
                            for ( String key :map.keySet() ) {
                                keysList.add(key);
                            }
                            for(int i=0;i<keysList.size();i++){
                               getAllServiceProviderswhomeWithChatExists(keysList.get(i));
                            }
                            for(int i=0;i<keysList.size();i++){
                                getImageUrlsOfServiceProviders(keysList.get(i));
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
                                    Intent intent = new Intent(getActivity(),CustomerMessageActivity.class);
                                    intent.putExtra("serviceprovider",keysList.get(position));
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

}
