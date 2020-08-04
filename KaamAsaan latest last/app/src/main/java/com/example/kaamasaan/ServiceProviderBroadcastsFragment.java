package com.example.kaamasaan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceProviderBroadcastsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceProviderBroadcastsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FirebaseDatabase database;
    DatabaseReference broadcast_ref;
    ArrayList<BroadCastRequest>broadCastList;
    ArrayList<Customer>customerList = new ArrayList<>();
    ListView lv_broadcasts;
    boolean doeschatHashmapOfCustomerExist;
    ArrayList<Message>chatList = new ArrayList();
    String mMessage;
    ProgressDialog mprogressdialog;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServiceProviderBroadcastsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceProviderBroadcastsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceProviderBroadcastsFragment newInstance(String param1, String param2) {
        ServiceProviderBroadcastsFragment fragment = new ServiceProviderBroadcastsFragment();
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

        database = FirebaseDatabase.getInstance();
        broadcast_ref = database.getReference("Broadcasts");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_provider_broadcasts, container, false);
        lv_broadcasts = view.findViewById(R.id.list_broadcasts);
        mprogressdialog = new ProgressDialog(getActivity());
        mprogressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mprogressdialog.setMessage("Loading broadcasts...");
        mprogressdialog.setIndeterminate(true);
        mprogressdialog.setCancelable(false);

        broadCastList =  getBroadCasts();

        mprogressdialog.show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
              //  Toast.makeText(getActivity(),"size is: "+broadCastList.size(),Toast.LENGTH_LONG).show();
                if(broadCastList.size()<1){
                 //   Toast.makeText(getActivity(),"No broad casts found according to your profile",Toast.LENGTH_LONG).show();
                    mprogressdialog.dismiss();
                }
                else {
                    final CustomAdapter customAdapter = new CustomAdapter();
                    lv_broadcasts.setAdapter(customAdapter);

                    // now populating the arrayList of customers who sent these broadcasts
                    for (int i = 0; i < broadCastList.size(); i++) {
                        getCustomersWhoSentBroadCasts(broadCastList.get(i).getCustomerUserName());
                    }

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                         //   Toast.makeText(getActivity(), "size of customers: " + customerList.size(), Toast.LENGTH_LONG).show();

                        }
                    }, 3000);

                }
                mprogressdialog.dismiss();
            }
        }, 4000);





        return view;
    }

    private ArrayList<BroadCastRequest> getBroadCasts(){
        final ArrayList<BroadCastRequest> alBroadCasts=  new ArrayList<>();
        broadcast_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                     BroadCastRequest broadCastRequest = ssn.getValue(BroadCastRequest.class);
                     if(broadCastRequest.getCategory().equals(MainActivity.mserviceProvider.getCategory())&&getDistanceBetweenLocations(broadCastRequest.getLatitude(),broadCastRequest.getLongitude(),
                             MainActivity.mserviceProvider.getLatititude(),MainActivity.mserviceProvider.getLongitude())<5001
                             && broadCastRequest.getWorkRating()==MainActivity.mserviceProvider.getAverageWorkRating()
                      &&broadCastRequest.getMaxVisitCost()>=MainActivity.mserviceProvider.visitCost){
                             alBroadCasts.add(broadCastRequest);

                     }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return alBroadCasts;
    }

    private float getDistanceBetweenLocations(double blat,double blng,double sp_lat,double sp_lng ){
        float distance;
        Location blocation = new Location("broadcastlocation");
        blocation.setLatitude(blat);
        blocation.setLongitude(blng);

        Location splocation = new Location("splocation");
        splocation.setLatitude(sp_lat);
        splocation.setLongitude(sp_lng);

        distance = splocation.distanceTo(blocation);

        return distance;
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return broadCastList.size();
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
            convertView = getActivity().getLayoutInflater().inflate(R.layout.broadcast_request_list_row,null);
            final TextView  txt_broadcast_req_id = convertView.findViewById(R.id.txt_broadcast_req_id);
            TextView  txt_broadcast_title= convertView.findViewById(R.id.txt_broadcast_title);
            TextView  txt_sp_category= convertView.findViewById(R.id. txt_sp_category);
            Button btn_detail = convertView.findViewById(R.id.btn_details);

            txt_broadcast_req_id.setText(broadCastList.get(position).getId());
            txt_broadcast_title.setText(broadCastList.get(position).getTitle());
            txt_sp_category.setText(broadCastList.get(position).getCategory());

           btn_detail.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  getDetailsOfBroadCast(broadCastList.get(position).getId());
               }
           });



            return convertView;
        }
    }



    private  void getDetailsOfBroadCast(final String broadCastID){
        broadcast_ref.child(broadCastID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                BroadCastRequest broadCastRequest = dataSnapshot.getValue(BroadCastRequest.class);
                try {
                    int visitRequestListSize;
                    if(broadCastRequest.getVisitRequestsList()==null){
                        visitRequestListSize = 0;
                    }
                    else{
                        visitRequestListSize = broadCastRequest.getVisitRequestsList().size();
                    }


                    Dialog detailsDialog = showBroadCastDetails(broadCastID,broadCastRequest.getTitle(),broadCastRequest.getDescription(),
                            broadCastRequest.getCategory(), getAddress(broadCastRequest.getLatitude(),broadCastRequest.getLongitude())
                            ,String.valueOf(visitRequestListSize),broadCastRequest.getCustomerUserName(),
                            broadCastRequest.getImageUrl1(),broadCastRequest.getImageUrl2(),broadCastRequest.getImageUrl3(),
                            broadCastRequest.getImageUrl4(),broadCastRequest.getImageUrl5(),broadCastRequest.getImageUrl6());
                    detailsDialog.show();
                    Window window = detailsDialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private Dialog showBroadCastDetails(String id, String broadcastTitle, String description, String category, String broadcast_location,
                                        String numberOfSPVisiting , final String customerUserName,
                                        String imageUrl1,String imageUrl2,String imageUrl3
    ,String imageUrl4,String imageUrl5,String imageUrl6){
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.found_broadcast_detail_dialog);


        final TextView txt_id = dialog.findViewById(R.id.txt_broadcast_req_id);
        TextView txt_title= dialog.findViewById(R.id.txt_broadcast_title);
        TextView txt_description = dialog.findViewById(R.id.txt_broadcast_description);
        TextView sp_category = dialog.findViewById(R.id.txt_sp_category);
        TextView txt_broadcast_location = dialog.findViewById(R.id.txt_broadcast_location);
        TextView no_of_sp_visiting = dialog.findViewById(R.id.txt_number_of_sp_visiting);
        TextView txt_customername  = dialog.findViewById(R.id.txt_customername);
        final androidx.appcompat.widget.AppCompatImageView iv1 =  dialog.findViewById(R.id.iv1);
        final androidx.appcompat.widget.AppCompatImageView iv2 =  dialog.findViewById(R.id.iv2);
        final androidx.appcompat.widget.AppCompatImageView iv3 =  dialog.findViewById(R.id.iv3);
        final androidx.appcompat.widget.AppCompatImageView iv4 =  dialog.findViewById(R.id.iv4);
        final androidx.appcompat.widget.AppCompatImageView iv5 =  dialog.findViewById(R.id.iv5);
        final androidx.appcompat.widget.AppCompatImageView iv6 =  dialog.findViewById(R.id.iv6);
        Button btn_send_visit_request = dialog.findViewById(R.id.btn_send_visit_request);
        androidx.appcompat.widget.AppCompatImageButton btn_send_message = dialog.findViewById(R.id.btn_send_message);
        Button btn_close = dialog.findViewById(R.id.btn_close);

        txt_id.setText(id);
        txt_title.setText(broadcastTitle);
        txt_description.setText(description);
        sp_category.setText(category);
        txt_broadcast_location.setText(broadcast_location);
        no_of_sp_visiting.setText(numberOfSPVisiting);
        txt_customername.setText(customerUserName);

        if(imageUrl1!=null&&imageUrl1!=""){
            Glide.with(getActivity()).load(imageUrl1).into(iv1);
        }
        if(imageUrl2!=null&&imageUrl2!=""){
            Glide.with(getActivity()).load(imageUrl2).into(iv2);
        }
        if(imageUrl3!=null&&imageUrl3!=""){
            Glide.with(getActivity()).load(imageUrl3).into(iv3);
        }
        if(imageUrl4!=null&&imageUrl4!=""){
            Glide.with(getActivity()).load(imageUrl4).into(iv4);
        }
        if(imageUrl5!=null&&imageUrl5!=""){
            Glide.with(getActivity()).load(imageUrl5).into(iv5);
        }
        if(imageUrl6!=null&&imageUrl6!=""){
            Glide.with(getActivity()).load(imageUrl6).into(iv6);
        }

        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iv1.getDrawable()!=null) {
                    showLargeImage(iv1.getDrawable());
                }
        }
        });
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iv2.getDrawable()!=null) {
                    showLargeImage(iv2.getDrawable());
                }
            }
        });
        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iv3.getDrawable()!=null) {
                    showLargeImage(iv3.getDrawable());
                }
            }
        });
        iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iv4.getDrawable()!=null) {
                    showLargeImage(iv4.getDrawable());
                }
            }
        });
        iv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iv5.getDrawable()!=null) {
                    showLargeImage(iv5.getDrawable());
                }
            }
        });
        iv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iv6.getDrawable()!=null) {
                    showLargeImage(iv6.getDrawable());
                }
            }
        });

        btn_send_visit_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* String customerID = getIDOfCustomer(customerUserName);
                Customer customer = getCustomer(customerID);
                */
                //   Toast.makeText(getActivity(),"customer id before sending intent"+ customerID, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getActivity(), SendVisitRequestActivityForBroadCast.class);
                intent.putExtra("broadcastId",txt_id.getText().toString());
                startActivity(intent);
            }
        });

         btn_send_message.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
             showChatDialog(customerUserName);

             }
         });

    btn_close.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dialog.cancel();
        }
    });


        return dialog;
    }
    public String getAddress(double latitude,double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0);
        return address;
    }

    private void showChatDialog(final String customer_UserName){
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.message_dialog);
        dialog.setTitle("Chat with "+customer_UserName);

        final ListView lv = dialog.findViewById(R.id.lv_chats);
        Button send = dialog.findViewById(R.id.btn_send);
        Button cancel = dialog.findViewById(R.id.btn_cancel);
        final EditText ed_messge = dialog.findViewById(R.id.ed_message);

        if( doesChatHashmapOfServiceProviderExist()==true) {
          //  Toast.makeText(getActivity(), "Chatmap of Service Provider exists ", Toast.LENGTH_LONG).show();

            if (doesChatListWithThisCustomerExistInHasmapOfServiceProvider(customer_UserName) == true) {
            //    Toast.makeText(getActivity(), "ChatList With This Customer Exists In Hashmap Of Service Provider ", Toast.LENGTH_LONG).show();
                HashMap hashMap = MainActivity.mserviceProvider.getChatHashmap();
                ArrayList<Message> alMessage = (ArrayList<Message>) hashMap.get(customer_UserName);
                chatList = alMessage;
                CustomAdapterChatList customAdapterChatList = new CustomAdapterChatList();
                lv.setAdapter(customAdapterChatList);
                lv.setSelection(customAdapterChatList.getCount()-1);
            }
        }

        lv.setLongClickable(true);
       lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {

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
                       lv.setAdapter(customAdapterChatList);
                       lv.setSelection(customAdapterChatList.getCount()-1);

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
              //  Toast.makeText(getActivity(),"button pressed",Toast.LENGTH_LONG).show();
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
                            final CustomAdapterChatList customAdapterChatList =new CustomAdapterChatList();
                            lv.setAdapter( customAdapterChatList );
                            lv.setSelection( customAdapterChatList .getCount() - 1);
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
                            lv.setAdapter(customAdapterChatList);
                            lv.setSelection(customAdapterChatList.getCount() - 1);
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
                        lv.setAdapter(customAdapterChatList);
                        lv.setSelection(customAdapterChatList.getCount()-1);
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

                                  customer.setChatHashmap(hashMap);
                                final DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(customerID);
                                customer_ref.setValue(customer);

                            }
                            else{// i-e almsg==null
                                almsg = new ArrayList<>();
                                Message message1 = new Message(MainActivity.mserviceProvider.getUserName(), mMessage,getCurrentDate());
                                almsg.add(message1);
                                hashMap.put(MainActivity.mserviceProvider.getUserName(), almsg);
                              //  Toast.makeText(getActivity(), "size of message array after updating is: " + almsg.size(), Toast.LENGTH_LONG).show();

                               customer.setChatHashmap(hashMap);

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

                           customer.setChatHashmap(hashMap);
                            final DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(customerID);
                            customer_ref.setValue(customer);
                        }// end manipulation of customer
            }

        }// end if ed_Message is not empty

            }// end OnClick
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_messge.setText("");
            }
        });




        dialog.show();

    }

    private boolean doesChatHashmapOfServiceProviderExist(){
        boolean bool;
        HashMap<String,ArrayList<Message>> map = MainActivity.mserviceProvider.getChatHashmap();
        if(map==null){
            bool =false;
        }
        else{
            bool = true;
        }
        return bool;
    }
    private boolean doesChatListWithThisCustomerExistInHasmapOfServiceProvider(String customer_userName){
        final boolean[] bool = {false};
        HashMap<String,ArrayList<Message>> map = MainActivity.mserviceProvider.getChatHashmap();
        ArrayList<Message>alMessage = map.get(customer_userName);
        if(alMessage==null){
            bool[0]=false;
        }
        else{
            bool[0]=true;
        }
      //  Toast.makeText(getActivity(),"checking chat array existence of customer",Toast.LENGTH_LONG).show();
        return bool[0];
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

    private String getIDOfCustomer(String customer_UserName){
        String customerID="";
        for(int i = 0;i<customerList.size();i++){
            if(customer_UserName.equals(customerList.get(i).getUserName())){
                customerID = customerList.get(i).getId();
            }
        }
        return customerID;
    }

    private void getCustomersWhoSentBroadCasts(final String customer_UserName){

        final DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
        customer_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for(DataSnapshot ssn:dataSnapshot.getChildren()){
                   Customer customer = ssn.getValue(Customer.class);
                   if (customer.getUserName().equals(customer_UserName)) {

                       customerList.add(customer);
                   }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private Customer getCustomer(String customerID){
        Customer customer = new Customer();
        for(int i = 0;i<customerList.size();i++) {
            if (customerID.equals(customerList.get(i).getId())) {
                customer = customerList.get(i);
            }
        }
        return customer ;
    }

    public String getCurrentDate(){
        long date = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss:a");
        String formattedCurrentDate = df.format(date);

        return formattedCurrentDate;
    }
    public void showLargeImage(Drawable clone){
        View view = getActivity().getLayoutInflater().inflate(R.layout.largeimagedialog, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity(), android.R.style.Theme_Holo_Dialog_NoActionBar);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        alertDialog.setView(view, 0, 0, 0, 0); // binding alert dialog with custom view
        androidx.appcompat.widget.AppCompatImageView iv = view.findViewById(R.id.iv_large);
        iv.setImageDrawable(clone);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

    }
}
