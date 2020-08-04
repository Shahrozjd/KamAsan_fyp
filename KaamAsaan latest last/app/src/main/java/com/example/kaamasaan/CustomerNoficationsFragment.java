package com.example.kaamasaan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerNoficationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerNoficationsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FirebaseDatabase database;
    DatabaseReference broadcast_ref;
    ArrayList<BroadCastRequest> alBroadCastRequests;
    ArrayList<VisitRequest> alReceivedVisitRequests;
    ArrayList<VisitRequest> alPendingVisitRequests;
    ArrayList<VisitRequest> alCompletedVisitRequests;
    ArrayList<String>alBroadCastIds;
    ArrayList<WorkRequest>alReceivedWorkRequests;
    ArrayList<WorkRequest>alPendingWorks;
    ArrayList<WorkRequest>alCompletedWorks;
    ListView listViewBroadCasts,listViewReceivedVisitRequests,listViewPendingVisits,listViewCompletedVisits;
    ListView listViewReceivedWorkRequests,listViewPendingWorkRequests,listViewCompletedWorks;
    Typeface mfont;
    boolean isPendingVisitExpired = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerNoficationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerNoficationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerNoficationsFragment newInstance(String param1, String param2) {
        CustomerNoficationsFragment fragment = new CustomerNoficationsFragment();
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
        mfont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/KaushanScript-Regular.otf");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_customer_nofications, container, false);
        listViewBroadCasts = view.findViewById(R.id.lv_broadcasts);
        listViewReceivedVisitRequests = view.findViewById(R.id.lv_received_visit_requests);
        listViewPendingVisits = view.findViewById(R.id.lv_pending_visits_of_customers);
        listViewCompletedVisits = view.findViewById(R.id.lv_completed_visits);
        listViewReceivedWorkRequests = view.findViewById(R.id.lv_received_work_requests);
        listViewPendingWorkRequests = view.findViewById(R.id.lv_pending_works);
        listViewCompletedWorks= view.findViewById(R.id.lv_completed_works);



        alReceivedVisitRequests = MainActivity.mcustomer.getReceivedVisitRequestList();
        alPendingVisitRequests = MainActivity.mcustomer.getPendingVisitsList();
        alCompletedVisitRequests = MainActivity.mcustomer.getCompletedVisitsList();
        alReceivedWorkRequests = MainActivity.mcustomer.getReceivedWorkRequestList();
        alPendingWorks =  MainActivity.mcustomer.getPendingWorkList();
        alCompletedWorks = MainActivity.mcustomer.getCompletedWorkList();


        alReceivedVisitRequests = new ArrayList<>();
        alPendingVisitRequests = new ArrayList<>();
        alCompletedVisitRequests = new ArrayList<>();
        alReceivedWorkRequests = new ArrayList<>();
        alPendingWorks =new ArrayList<>();
        alCompletedVisitRequests = new ArrayList<>();

        listViewBroadCasts.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // scrollView.getParent().requestDisallowInterceptTouchEvent(false);
                if(listViewBroadCasts.getChildCount()==0){
                    // do nothing
                }
                else {
                    listViewBroadCasts.getParent().requestDisallowInterceptTouchEvent(true);

                }
                return false;
            }
        });
        listViewReceivedVisitRequests.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // scrollView.getParent().requestDisallowInterceptTouchEvent(false);
                if(listViewReceivedVisitRequests.getChildCount()==0){
                    // do nothing
                }
                else {
                    listViewReceivedVisitRequests.getParent().requestDisallowInterceptTouchEvent(true);

                }
                return false;
            }
        });
        listViewPendingVisits.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // scrollView.getParent().requestDisallowInterceptTouchEvent(false);
                if(listViewPendingVisits.getChildCount()==0){
                    //do nothing
                }
                else{
                    listViewPendingVisits.getParent().requestDisallowInterceptTouchEvent(true);
                }

                return false;
            }
        });

      listViewCompletedVisits.setOnTouchListener(new View.OnTouchListener() {
          @Override
          public boolean onTouch(View v, MotionEvent event) {
              if(listViewCompletedVisits.getChildCount()==0){
                  // do nothing
              }
              else{
                  listViewCompletedVisits.getParent().requestDisallowInterceptTouchEvent(true);
              }

              return false;
          }
      });

        listViewReceivedWorkRequests.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if( listViewReceivedWorkRequests.getChildCount()==0){
                    // do nothing
                }
                else{
                    listViewReceivedWorkRequests.getParent().requestDisallowInterceptTouchEvent(true);
                }

                return false;
            }
        });

        listViewPendingWorkRequests.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if( listViewPendingWorkRequests.getChildCount()==0){
                    // do nothing
                }
                else{
                    listViewPendingWorkRequests.getParent().requestDisallowInterceptTouchEvent(true);
                }

                return false;
            }
        });

        listViewCompletedWorks.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(  listViewCompletedWorks.getChildCount()==0){
                    // do nothing
                }
                else{
                    listViewCompletedWorks.getParent().requestDisallowInterceptTouchEvent(true);
                }

                return false;
            }
        });

// we are calling this function on lost so that no other method  could called before getting data from firebase in this function



              getBroadCastIdsListANdBroadCastsofCustomer();
                getLatestData();






        return  view;
    }

    class CustomAdapterBroadCastRequests extends BaseAdapter {

        @Override
        public int getCount() {
            return alBroadCastRequests.size();
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
            TextView lbl_title = convertView.findViewById(R.id.lbl_broadcast_req_id);
            TextView broadcast_id = convertView.findViewById(R.id.txt_broadcast_req_id);
            TextView lbl_broadcast_title = convertView.findViewById(R.id.lbl_broadcast_title);
            TextView broadcast_title = convertView.findViewById(R.id.txt_broadcast_title);
            TextView lbl_sp_category = convertView.findViewById(R.id.lbl_sp_category);
            TextView sp_category = convertView.findViewById(R.id.txt_sp_category);
            Button btn_details = convertView.findViewById(R.id.btn_details);
            Button btn_willing = convertView.findViewById(R.id.btn_willing);

            broadcast_id.setText(alBroadCastRequests.get(position).getId());
            broadcast_title.setText(alBroadCastRequests.get(position).getTitle());
            sp_category.setText(alBroadCastRequests.get(position).getCategory());

         btn_details.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 getDetailsOfBroadCast(alBroadCastRequests.get(position).getId());
             }
         });

            btn_willing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),WillingnessActivity.class);
                    intent.putExtra("broadCastId",alBroadCastRequests.get(position).getId().toString());
                    startActivity(intent);
                         }
            });
            return convertView;
        }
    }


    public void getBroadCastIdsListANdBroadCastsofCustomer(){
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
        customerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    Customer customer = ssn.getValue(Customer.class);
                    if(customer.getId().equals(MainActivity.mcustomer.getId())){

                        alBroadCastIds = customer.getBroadCastRequestsIdsList();
                        if(alBroadCastIds==null){
                            // do nothing
                        }
                        else{

                            getAllBroadCastRequests();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getAllBroadCastRequests(){
        alBroadCastRequests = new ArrayList<>();

        broadcast_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ssn: dataSnapshot.getChildren()){
                        BroadCastRequest broadCastRequest = ssn.getValue(BroadCastRequest.class);
                        for(int i=0;i<alBroadCastIds.size();i++){
                            if(alBroadCastIds.get(i).equals(broadCastRequest.getId())){
                                alBroadCastRequests.add(broadCastRequest);
                            }
                        }
                    }
                   CustomAdapterBroadCastRequests customAdapterBroadCastRequests = new CustomAdapterBroadCastRequests();
                    listViewBroadCasts.setAdapter(customAdapterBroadCastRequests);
                    alBroadCastIds.clear();

                    /*listViewBroadCasts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                          //  getDetailsOfBroadCast(alBroadCastIds.get(position));
                        }
                    });*/
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



    }

    private Dialog showBroadCastDetails(String id,String broadcastTitle, String description, String category, String broadcast_location,
    String numberOfSPVisiting, String imageUrl1,String imageUrl2,String imageUrl3,
                                        String imageUrl4,String imageUrl5,String imageUrl6){


        View view =  getActivity().getLayoutInflater().inflate(R.layout.sent_broadcast_detail_dialog,null);   // custom view

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Holo_Dialog_NoActionBar);
        final AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setView(view,0,0,0,0); // binding alert dialog with custom view


        final TextView txt_id = view.findViewById(R.id.txt_broadcast_req_id);
        TextView txt_title= view.findViewById(R.id.txt_broadcast_title);
        TextView txt_description = view.findViewById(R.id.txt_broadcast_description);
        TextView sp_category = view.findViewById(R.id.txt_sp_category);
        TextView txt_broadcast_location = view.findViewById(R.id.txt_broadcast_location);
        TextView no_of_sp_visiting = view.findViewById(R.id.txt_number_of_sp_visiting);
        final androidx.appcompat.widget.AppCompatImageView iv1 =  view.findViewById(R.id.iv1);
        final androidx.appcompat.widget.AppCompatImageView iv2 =  view.findViewById(R.id.iv2);
        final androidx.appcompat.widget.AppCompatImageView iv3 =  view.findViewById(R.id.iv3);
        final androidx.appcompat.widget.AppCompatImageView iv4 =  view.findViewById(R.id.iv4);
        final androidx.appcompat.widget.AppCompatImageView iv5 =  view.findViewById(R.id.iv5);
        final androidx.appcompat.widget.AppCompatImageView iv6 =  view.findViewById(R.id.iv6);
        Button btn_willingness = view.findViewById(R.id.btn_willingness);
        Button btn_ok =view.findViewById(R.id.btn_ok);

        txt_id.setText(id);
        txt_title.setText(broadcastTitle);
        txt_description.setText(description);
        sp_category.setText(category);
        txt_broadcast_location.setText(broadcast_location);
        no_of_sp_visiting.setText(numberOfSPVisiting);

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

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

       btn_willingness.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getActivity(),WillingnessActivity.class);
               intent.putExtra("broadCastId",txt_id.getText().toString());
               startActivity(intent);
           }
       });


        return alertDialog;
    }

    private  void getDetailsOfBroadCast(final String broadCastID){
        broadcast_ref.child(broadCastID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                BroadCastRequest broadCastRequest = dataSnapshot.getValue(BroadCastRequest.class);
                int visitRequestListSize;
                if(broadCastRequest.getVisitRequestsList()==null){
                    visitRequestListSize = 0;
                }
                else{
                    visitRequestListSize = broadCastRequest.getVisitRequestsList().size();
                }
                try {
                     Dialog detailsDialog = showBroadCastDetails(broadCastID,broadCastRequest.getTitle(),broadCastRequest.getDescription(),
                        broadCastRequest.getCategory(), getAddress(broadCastRequest.getLatitude(),
                                     broadCastRequest.getLongitude()),String.valueOf( visitRequestListSize),
                             broadCastRequest.getImageUrl1(),broadCastRequest.getImageUrl2(),
                             broadCastRequest.getImageUrl3(), broadCastRequest.getImageUrl4(),
                             broadCastRequest.getImageUrl5(), broadCastRequest.getImageUrl6());
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

    public String getAddress(double latitude,double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0);
        return address;
    }

    class CustomAdapterReceivedVisitRequests extends BaseAdapter {

        @Override
        public int getCount() {
            return alReceivedVisitRequests .size();
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
            convertView = getActivity().getLayoutInflater().inflate(R.layout.received_visit_request_list_row,null);
            TextView txt_sent_visit_request_Id = convertView.findViewById(R.id.txt_received_visit_request_Id);
            final TextView txt_to = convertView.findViewById(R.id.txt_from);
            TextView txt_potential_time = convertView.findViewById(R.id.txt_potential_time);
            TextView txt_duration = convertView.findViewById(R.id.txt_duration);
            TextView txt_visit_cost = convertView.findViewById(R.id.txt_visit_cost);
            Button btn_accept =  convertView.findViewById(R.id.btn_accept_visit_request);
            Button btn_reject = convertView.findViewById(R.id.btn_reject_visit_request);

            txt_sent_visit_request_Id.setText(alReceivedVisitRequests.get(position).getRequestId());
            txt_to.setText(alReceivedVisitRequests.get(position).getUserName());
            txt_potential_time.setText(alReceivedVisitRequests.get(position).getPotentialTimeAndDate());
            txt_duration.setText(String.valueOf(alReceivedVisitRequests.get(position).getDuration()));
            txt_visit_cost.setText(String.valueOf(alReceivedVisitRequests.get(position).getVisitCost()));

            btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 VisitRequest visitRequest = new VisitRequest(alReceivedVisitRequests.get(position).RequestId,
                        alReceivedVisitRequests.get(position).getUserId(),alReceivedVisitRequests.get(position).getUserName(),"Pending",
                         alReceivedVisitRequests.get(position).potentialTimeAndDate,alReceivedVisitRequests.get(position).duration,
                         alReceivedVisitRequests.get(position).getVisitCost(),alReceivedVisitRequests.get(position).getImageUrl(),
                         alReceivedVisitRequests.get(position).getRatedByServiceProvider());

                         System.out.println("UserName3: "+alReceivedVisitRequests.get(position).getUserName());
                 AddVisitRequestToPendingVisitInCustomerAndServiceProvider(visitRequest,MainActivity.mcustomer.getId(),position,txt_to.getText().toString());





                }
            });

            btn_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alReceivedVisitRequests.remove(position);
                    MainActivity.mcustomer.setReceivedVisitRequestList(alReceivedVisitRequests);
                    CustomAdapterReceivedVisitRequests customAdapterReceivedVisitRequests = new CustomAdapterReceivedVisitRequests();
                   listViewReceivedVisitRequests.setAdapter(customAdapterReceivedVisitRequests );
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Customers")
                            .child(MainActivity.mcustomer.getId());
                    databaseReference.setValue(MainActivity.mcustomer);
                }
            });

            return convertView;
        }
    }

    public void AddVisitRequestToPendingVisitInCustomerAndServiceProvider(final VisitRequest visitRequest, final String customerId, final int position,final String serviceProviderUserName){
        final Customer[] customer = new Customer[1];
        final DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
        customer_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    customer[0] = ssn.getValue(Customer.class);
                   if(customer[0].getId().equals(customerId)){
                         alPendingVisitRequests = customer[0].getPendingVisitsList();
                       if(alPendingVisitRequests==null){
                           alPendingVisitRequests= new ArrayList<>();
                           alPendingVisitRequests.add(visitRequest);


                           customer[0].setPendingVisitsList(alPendingVisitRequests);

                           alReceivedVisitRequests.remove(position);
                           customer[0].setReceivedVisitRequestList(alReceivedVisitRequests);
                           CustomAdapterReceivedVisitRequests customAdapterReceivedVisitRequests = new CustomAdapterReceivedVisitRequests();
                           listViewReceivedVisitRequests.setAdapter(customAdapterReceivedVisitRequests);
                           customer_ref.child(customer[0].getId()).setValue(customer[0]);
                       }
                       else{

                           alPendingVisitRequests.add(visitRequest);

                           customer[0].setPendingVisitsList(alPendingVisitRequests);

                           alReceivedVisitRequests.remove(position);
                           customer[0].setReceivedVisitRequestList(alReceivedVisitRequests);
                           CustomAdapterReceivedVisitRequests customAdapterReceivedVisitRequests = new CustomAdapterReceivedVisitRequests();
                           listViewReceivedVisitRequests.setAdapter(customAdapterReceivedVisitRequests);
                           customer_ref.child(customer[0].getId()).setValue(customer[0]);

                       }


                       visitRequest.setUserName(MainActivity.mcustomer.getUserName());
                       AddVisitRequestToPendingVisitInServiceProvider(visitRequest,serviceProviderUserName);
                     //  getLatestData();
                   }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void AddVisitRequestToPendingVisitInServiceProvider(final VisitRequest visitRequest, final String serviceProviderUserNme){
        final String[] id = new String[1];
        final ServiceProvider[] serviceProvider = new ServiceProvider[1];
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                     serviceProvider[0] = ssn.getValue(ServiceProvider.class);
                    if(serviceProvider[0].getUserName().equals(serviceProviderUserNme)){
                        id[0] = serviceProvider[0].getId();
                        ArrayList<VisitRequest>alPendingVisits = serviceProvider[0].getPendingVisitsList();
                        if(alPendingVisits==null){
                            alPendingVisits= new ArrayList<>();
                            alPendingVisits.add(visitRequest);
                            serviceProvider[0].setPendingVisitsList(alPendingVisits);
                            break;
                        }
                        else{
                            alPendingVisits.add(visitRequest);
                            serviceProvider[0].setPendingVisitsList(alPendingVisits);
                            break;
                        }


                    }
                }
                    DatabaseReference newref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").
                            child(serviceProvider[0].getId());
                newref.setValue(serviceProvider[0]);
                removeSentVisitRequestFromServiceProvider(visitRequest,serviceProviderUserNme);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removeSentVisitRequestFromServiceProvider(final VisitRequest visitRequest, final String serviceProviderUserNme){
        final String[] id = new String[1];
        final ServiceProvider[] serviceProvider = new ServiceProvider[1];
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    serviceProvider[0] = ssn.getValue(ServiceProvider.class);
                    if(serviceProvider[0].getUserName().equals(serviceProviderUserNme)){
                        id[0] = serviceProvider[0].getId();
                        ArrayList<VisitRequest>alSentVisitRequests = serviceProvider[0].getSentVisitRequestList();
                        for(int i=0;i<alSentVisitRequests.size();i++){
                            if(alSentVisitRequests.get(i).getRequestId().equals(visitRequest.getRequestId())){
                                alSentVisitRequests.remove(i);
                                serviceProvider[0].setSentVisitRequestList(alSentVisitRequests);
                                break;
                            }
                        }
                     break;
                    }
                }
                DatabaseReference newref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").
                        child(serviceProvider[0].getId());
                newref.setValue(serviceProvider[0]);

                getLatestData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    class PendingVisitRequestsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return alPendingVisitRequests.size();
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
            convertView = getActivity().getLayoutInflater().inflate(R.layout.customer_side_pending_visit_request_list_row,null);
            TextView txt_sent_visit_request_Id = convertView.findViewById(R.id.txt_pending_visit_request_Id);
            final TextView txt_from = convertView.findViewById(R.id.customer_side_txt_from);
            TextView txt_potential_time = convertView.findViewById(R.id.txt_potential_time);
            TextView txt_duration = convertView.findViewById(R.id.txt_duration);
            TextView txt_visit_cost = convertView.findViewById(R.id.txt_visit_cost);
            final TextView txt_days_left = convertView.findViewById(R.id.txt_days_left);
            final TextView txt_hours_left = convertView.findViewById(R.id.txt_hours_left);
            final TextView txt_mins_left = convertView.findViewById(R.id.txt_mins_left);
            Button btn_end_visit = convertView.findViewById(R.id.btn_end_visit);

            txt_sent_visit_request_Id.setText(alPendingVisitRequests.get(position).getRequestId());
            txt_from.setText(alPendingVisitRequests.get(position).getUserName());
            System.out.println("UserNamePosition :"+position+": "+alPendingVisitRequests.get(position).getUserName());
            txt_potential_time.setText(alPendingVisitRequests.get(position).getPotentialTimeAndDate());
            txt_duration.setText(String.valueOf(alPendingVisitRequests.get(position).getDuration()));
            txt_visit_cost.setText(String.valueOf(alPendingVisitRequests.get(position).getVisitCost()));

            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd-MMM-yyyy hh:mm:a");
            try {
                final java.util.Date potentialVisitTime = simpleDateFormat.parse(alPendingVisitRequests.get(position).getPotentialTimeAndDate());
                long date = System.currentTimeMillis();
                String formattedCurrentDate =  simpleDateFormat.format(date);
                final java.util.Date currentTime = simpleDateFormat.parse(formattedCurrentDate);
                txt_days_left.setText(getTimeLeft(potentialVisitTime,currentTime).get(0)) ;
                txt_hours_left.setText(getTimeLeft(potentialVisitTime,currentTime).get(1)) ;
                txt_mins_left.setText(getTimeLeft(potentialVisitTime,currentTime).get(2)) ;
                if(txt_mins_left.getText().toString().contains("-")){
                    // do nothing
                }
               else{
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        //    Toast.makeText(getActivity(),"code in handler",Toast.LENGTH_LONG).show();
                            txt_days_left.setText(getTimeLeft(potentialVisitTime,currentTime).get(0)) ;
                            txt_hours_left.setText(getTimeLeft(potentialVisitTime,currentTime).get(1)) ;
                            txt_mins_left.setText(getTimeLeft(potentialVisitTime,currentTime).get(2)) ;

                        }
                    }, 3000);
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }

            btn_end_visit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {




                    final VisitRequest visitRequest = new VisitRequest(alPendingVisitRequests.get(position).getRequestId(),
                            alPendingVisitRequests.get(position).getUserId(),alPendingVisitRequests.get(position).getUserName(),
                            "Expired",alPendingVisitRequests.get(position).potentialTimeAndDate,
                            alPendingVisitRequests.get(position).duration,alPendingVisitRequests.get(position).getVisitCost(),
                            alPendingVisitRequests.get(position).getImageUrl(),alPendingVisitRequests.get(position).getRatedByServiceProvider());

                            showRatingDialog(txt_from.getText().toString(),position,visitRequest);



                    // code for automatic rating
                    /*if (txt_mins_left.getText().toString().contains("-")) {
                           isPendingVisitExpired = true;

                           DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
                           if(MainActivity.mcustomer.getResponseTimeRatingList()==null){
                               ArrayList<ResponseTimeRating>listResponseTimeRating = new ArrayList();

                                 ResponseTimeRating  responseTimeRating
                                          = new ResponseTimeRating(alPendingVisitRequests.get(position).getImageUrl(),
                                         alPendingVisitRequests.get(position).getUserName(),5);
                                 listResponseTimeRating.add(responseTimeRating);
                                 customer_ref.child(MainActivity.mcustomer.getId()+"/"+"responseTimeRatingList").setValue(listResponseTimeRating);
                                 MainActivity.mcustomer.setResponseTimeRatingList(listResponseTimeRating);

                                 int sum = 0;
                                 for(int i=0;i<listResponseTimeRating.size();i++){
                                     sum = sum+ listResponseTimeRating.get(i).getResponseRating();
                                 }
                                 int averageResponseRating = sum/listResponseTimeRating.size();

                               customer_ref.child(MainActivity.mcustomer.getId()+"/"+"responseRating").setValue(averageResponseRating);

                           }
                           else{
                               ArrayList<ResponseTimeRating>listResponseTimeRating =  MainActivity.mcustomer.getResponseTimeRatingList();
                               ResponseTimeRating  responseTimeRating
                                       = new ResponseTimeRating(alPendingVisitRequests.get(position).getImageUrl(),
                                       alPendingVisitRequests.get(position).getUserName(),5);
                               listResponseTimeRating.add(responseTimeRating);
                               customer_ref.child(MainActivity.mcustomer.getId()+"/"+"responseTimeRatingList").setValue(listResponseTimeRating);
                               MainActivity.mcustomer.setResponseTimeRatingList(listResponseTimeRating);

                               int sum = 0;
                               for(int i=0;i<listResponseTimeRating.size();i++){
                                   sum = sum+ listResponseTimeRating.get(i).getResponseRating();
                               }
                               int averageResponseRating = sum/listResponseTimeRating.size();
                               customer_ref.child(MainActivity.mcustomer.getId()+"/"+"responseRating").setValue(averageResponseRating);
                           }


                                transferPendingVisitToCompletedVisitListOfCustomerAndServiceProvider(visitRequest,
                                        MainActivity.mcustomer.getUserName(),position,txt_from.getText().toString());

                    }
                    else{

                       showRatingDialog(txt_from.getText().toString(),position,visitRequest);
                    }*/
                }
            });


            return convertView;
        }
    }


    public void transferPendingVisitToCompletedVisitListOfCustomerAndServiceProvider(final VisitRequest visitRequest, final String customerUserName, final int position, final String serviceProviderUserName){
        final DatabaseReference cus_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
        final Customer customer[] = new Customer[1];
        cus_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn:dataSnapshot.getChildren()){
                    customer[0] = ssn.getValue(Customer.class);
                    if(customer[0].getUserName().equals(customerUserName)){
                        alCompletedVisitRequests = customer[0].getCompletedVisitsList();
                        if(alCompletedVisitRequests==null){
                            alCompletedVisitRequests = new ArrayList<>();
                            alCompletedVisitRequests.add(visitRequest);
                            customer[0].setCompletedVisitsList(alCompletedVisitRequests);
                            alPendingVisitRequests.remove(position);
                            customer[0].setPendingVisitsList(alPendingVisitRequests);

                            PendingVisitRequestsAdapter pendingVisitRequestsAdapter = new PendingVisitRequestsAdapter();
                            listViewPendingVisits.setAdapter(pendingVisitRequestsAdapter);
                            cus_ref.child(customer[0].getId()).setValue(customer[0]);

                            if(isPendingVisitExpired==true){
                                  System.out.println("is Expired "+ isPendingVisitExpired);
                                // now handling the responseTime Rating of Service Provider
                                automaticallyAddResponseRatingOfServiceProvider(serviceProviderUserName);
                            }

                        }
                        else{
                            alCompletedVisitRequests.add(visitRequest);
                            customer[0].setCompletedVisitsList(alCompletedVisitRequests);
                            alPendingVisitRequests.remove(position);
                            customer[0].setPendingVisitsList(alPendingVisitRequests);

                            PendingVisitRequestsAdapter pendingVisitRequestsAdapter = new PendingVisitRequestsAdapter();
                            listViewPendingVisits.setAdapter(pendingVisitRequestsAdapter);
                            cus_ref.child(customer[0].getId()).setValue(customer[0]);

                           /* // now handling the responseTime Rating of Service Provider
                            automaticallyAddResponseRatingOfServiceProvider(serviceProviderUserName);*/
                        }
                    }
                }
                visitRequest.setUserName(MainActivity.mcustomer.getUserName());
                addPendingVisitToCompletedVisitInServiceProvider(visitRequest,serviceProviderUserName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private ArrayList<String> getTimeLeft(java.util.Date potentialVisitTime, java.util.Date currentTime){
        ArrayList<String> listTimeLeft = new ArrayList<>();

        long diff = potentialVisitTime.getTime()- currentTime.getTime();

        int diffDays = (int)(diff/(24*60*60*1000));


        int diffHours = (int)(diff/(60*60*1000));

        if(diffHours<=-24){
            diffHours =  diffHours%24;

        }
        else if(diffHours>=24){
            diffHours = diffHours%24;
        }



        int diffMins = (int)(diff/(60*1000));
        if(diffMins<-60)
            diffMins = diffMins%60;
        if(diffMins>=60){
            diffMins = diffMins%60;
        }
        listTimeLeft.add(diffDays+" days");
        listTimeLeft.add(diffHours+" hours");
        listTimeLeft.add(diffMins+" min");



        return listTimeLeft;
    }
    public int countChar(String str, char c)
    {
        int count = 0;

        for(int i=0; i < str.length(); i++)
        {    if(str.charAt(i) == c)
            count++;
        }

        return count;
    }

    private void automaticallyAddResponseRatingOfServiceProvider( final String ServiceProviderUserName){
        Toast.makeText(getActivity(),"Inside automatic response rating of customer",Toast.LENGTH_LONG).show();
        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        final String[] ServiceProviderId = new String[1];
        final String[] ServiceProviderImageUrl = new String[1];
        final ServiceProvider[] serviceProvider = new ServiceProvider[1];

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ssn : dataSnapshot.getChildren()) {
                    serviceProvider[0] = ssn.getValue(ServiceProvider.class);

                    if (serviceProvider[0].getUserName().equals(ServiceProviderUserName)) {

                        ServiceProviderId[0] = serviceProvider[0].getId();
                        ServiceProviderImageUrl[0] = serviceProvider[0].getProfilePicUrl();
                        ResponseTimeRating responseTimeRating = new
                                ResponseTimeRating(MainActivity.mcustomer.getImageUrl(),
                                MainActivity.mcustomer.getUserName(), 5);

                        ArrayList<ResponseTimeRating> responseTimeRatingArrayListOfServiceProvider =
                                serviceProvider[0].getResponseTimeRatingList();

                        if (responseTimeRatingArrayListOfServiceProvider == null) {
                            responseTimeRatingArrayListOfServiceProvider = new ArrayList<>();
                            responseTimeRatingArrayListOfServiceProvider.add(responseTimeRating);

                            serviceProvider[0].setResponseTimeRatingList(responseTimeRatingArrayListOfServiceProvider);
                            int averageResponseTimeRatingOfServiceProvider = 0;
                            int sum = 0;
                            for (int i = 0; i < responseTimeRatingArrayListOfServiceProvider.size(); i++) {
                                sum = sum +
                                        responseTimeRatingArrayListOfServiceProvider.get(i).getResponseRating();
                            }

                            averageResponseTimeRatingOfServiceProvider = sum /
                                    responseTimeRatingArrayListOfServiceProvider.size();

                            serviceProvider[0].setResponseRating(averageResponseTimeRatingOfServiceProvider);




                        } else {
                            responseTimeRatingArrayListOfServiceProvider.add(responseTimeRating);
Toast.makeText(getActivity(),"size is: "+responseTimeRatingArrayListOfServiceProvider.size(),Toast.LENGTH_LONG).show();
                            serviceProvider[0].setResponseTimeRatingList(responseTimeRatingArrayListOfServiceProvider);
                            int averageResponseTimeRatingOfServiceProvider = 0;
                            int sum = 0;
                            for (int i = 0; i < responseTimeRatingArrayListOfServiceProvider.size(); i++) {
                                sum = sum +
                                        responseTimeRatingArrayListOfServiceProvider.get(i).getResponseRating();
                            }
                            averageResponseTimeRatingOfServiceProvider = sum /
                                    responseTimeRatingArrayListOfServiceProvider.size();

                            serviceProvider[0].setResponseRating(averageResponseTimeRatingOfServiceProvider);





                        }
                    }
                    break;
                }

                DatabaseReference newDatabaseReference =
                        FirebaseDatabase.getInstance().getReference("Users").
                                child("ServiceProviders").child(serviceProvider[0].getId()
                        );
                newDatabaseReference.setValue(serviceProvider[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void automaticallyAddResponseRatingOfCustomer(String ServiceProviderImageUrl,String ServiceProviderUserName){
        ResponseTimeRating responseTimeRating = new ResponseTimeRating(ServiceProviderImageUrl,
               ServiceProviderUserName, 5);
        ArrayList<ResponseTimeRating> responseTimeRatingArrayListOfCustomer = MainActivity.mcustomer.getResponseTimeRatingList();
        if (responseTimeRatingArrayListOfCustomer == null) {
            responseTimeRatingArrayListOfCustomer = new ArrayList<>();
            responseTimeRatingArrayListOfCustomer.add(responseTimeRating);
            MainActivity.mcustomer.setResponseTimeRatingList(responseTimeRatingArrayListOfCustomer);
            int averageResponseTimeRatingOfCustomer = 0;
            int sum = 0;
            for (int i = 0; i < responseTimeRatingArrayListOfCustomer.size(); i++) {
                sum = sum + responseTimeRatingArrayListOfCustomer.get(i).getResponseRating();
            }
            averageResponseTimeRatingOfCustomer = sum / responseTimeRatingArrayListOfCustomer.size();
            MainActivity.mcustomer.setResponseRating(averageResponseTimeRatingOfCustomer);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Customers")
                    .child(MainActivity.mcustomer.getId());

            databaseReference.setValue(MainActivity.mcustomer);
        } else {

            responseTimeRatingArrayListOfCustomer.add(responseTimeRating);
            MainActivity.mcustomer.setResponseTimeRatingList(responseTimeRatingArrayListOfCustomer);
            int averageResponseTimeRatingOfCustomer = 0;
            int sum = 0;
            for (int i = 0; i < responseTimeRatingArrayListOfCustomer.size(); i++) {
                sum = sum + responseTimeRatingArrayListOfCustomer.get(i).getResponseRating();
            }
            averageResponseTimeRatingOfCustomer = sum / responseTimeRatingArrayListOfCustomer.size();
            MainActivity.mcustomer.setResponseRating(averageResponseTimeRatingOfCustomer);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Customers")
                    .child(MainActivity.mcustomer.getId());

            databaseReference.setValue(MainActivity.mcustomer);

          //  getLatestData();

        }
    }

    public void showRatingDialog(final String ServiceProviderUserName, final int position, final VisitRequest visitRequest){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.response_rating_dialog);
        final RatingBar ratingBar = dialog.findViewById(R.id.response_ratingbar);
        Button button_ok = dialog.findViewById(R.id.response_rating_ok_button);
        dialog.setTitle("Response Rating to " + ServiceProviderUserName);
        dialog.setCancelable(false);

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ratingBar.getRating()==0){
                    Toast.makeText(getActivity(),"Please give rating first",Toast.LENGTH_LONG).show();
                }
                else {
                    manuallyGiveRatingToServiceProvider(ServiceProviderUserName, (int) ratingBar.getRating());

                    transferPendingVisitToCompletedVisitListOfCustomerAndServiceProvider(visitRequest, MainActivity.mcustomer.getUserName(),
                            position, ServiceProviderUserName);

                    dialog.dismiss();
                }



            }
        });

        dialog.show();
    }

    public void showWorkRatingDialog(final String ServiceProviderUserName, final int position, final WorkRequest workRequest){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.work_rating_dialog);
        final RatingBar ratingBar = dialog.findViewById(R.id.work_ratingbar);
        final EditText ed_review = dialog.findViewById(R.id.ed_review);
        Button button_ok = dialog.findViewById(R.id.work_rating_ok_button);
        dialog.setTitle("Give Review to " + ServiceProviderUserName);
        dialog.setCancelable(false);

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ed_review.getText().toString().isEmpty()||ratingBar.getRating()==0){
                   Toast.makeText(getActivity(),"Error: Rating and Review both must be given",Toast.LENGTH_LONG).show();
                }
                else{
                   System.out.println("myreview: "+ed_review.getText().toString());
                   WorkRating workRating = new WorkRating(MainActivity.mcustomer.getImageUrl(),
                           MainActivity.mcustomer.getUserName(),ed_review.getText().toString(),
                           (int)ratingBar.getRating());
                    giveWorkRatingToServiceProvider(ServiceProviderUserName,
                            workRating);

                    transferPendingWorkToCompletedworkListOfCustomerAndServiceProvider(workRequest,
                            MainActivity.mcustomer.getUserName(),position,ServiceProviderUserName);

                    dialog.dismiss();

                }
            }
        });

        dialog.show();

    }
   public void manuallyGiveRatingToServiceProvider(final String ServiceProviderUserName, final int rating){
       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
       final String[] ServiceProviderId = new String[1];
       final String[] ServiceProviderImageUrl = new String[1];
       final ServiceProvider[] serviceProvider = new ServiceProvider[1];
       databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for(DataSnapshot ssn:dataSnapshot.getChildren()){
                   serviceProvider[0] = ssn.getValue(ServiceProvider.class);
                   if(serviceProvider[0].getUserName().equals(ServiceProviderUserName)){
                       ServiceProviderId[0] = serviceProvider[0].getId();
                       ServiceProviderImageUrl[0] = serviceProvider[0].getProfilePicUrl();
                       ResponseTimeRating responseTimeRating = new ResponseTimeRating(MainActivity.mcustomer.getImageUrl(),
                               MainActivity.mcustomer.getUserName(),rating);
                       ArrayList<ResponseTimeRating> responseTimeRatingArrayListOfServiceProvider = serviceProvider[0].getResponseTimeRatingList();
                       if(responseTimeRatingArrayListOfServiceProvider==null){
                           responseTimeRatingArrayListOfServiceProvider = new ArrayList<>();
                           responseTimeRatingArrayListOfServiceProvider.add(responseTimeRating);
                           serviceProvider[0].setResponseTimeRatingList(responseTimeRatingArrayListOfServiceProvider);
                           int averageResponseTimeRatingOfServiceProvider = 0;
                           int sum = 0;
                           for (int i = 0; i < responseTimeRatingArrayListOfServiceProvider.size(); i++) {
                               sum = sum + responseTimeRatingArrayListOfServiceProvider.get(i).getResponseRating();
                           }
                           averageResponseTimeRatingOfServiceProvider = sum / responseTimeRatingArrayListOfServiceProvider.size();
                           serviceProvider[0].setResponseRating(averageResponseTimeRatingOfServiceProvider);
                           DatabaseReference newDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(ServiceProviderId[0]);
                           newDatabaseReference.setValue(serviceProvider[0]);

                           break;
                       }
                       else{
                           responseTimeRatingArrayListOfServiceProvider.add(responseTimeRating);
                           serviceProvider[0].setResponseTimeRatingList(responseTimeRatingArrayListOfServiceProvider);
                           int averageResponseTimeRatingOfServiceProvider = 0;
                           int sum = 0;
                           for (int i = 0; i < responseTimeRatingArrayListOfServiceProvider.size(); i++) {
                               sum = sum + responseTimeRatingArrayListOfServiceProvider.get(i).getResponseRating();
                           }
                           averageResponseTimeRatingOfServiceProvider = sum / responseTimeRatingArrayListOfServiceProvider.size();
                           serviceProvider[0].setResponseRating(averageResponseTimeRatingOfServiceProvider);
                           DatabaseReference newDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(ServiceProviderId[0]);
                           newDatabaseReference.setValue(serviceProvider[0]);
                           break;
                       }
                   }
               }


           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

   }

   public void giveWorkRatingToServiceProvider(final String serviceProviderUserName, final WorkRating workRating){
       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
   //    Toast.makeText(getActivity(),"inside rating function",Toast.LENGTH_LONG).show();
       final String[] ServiceProviderId = new String[1];
       final String[] ServiceProviderImageUrl = new String[1];
       final ServiceProvider[] serviceProvider = new ServiceProvider[1];
       databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for(DataSnapshot ssn:dataSnapshot.getChildren()) {
                   serviceProvider[0] = ssn.getValue(ServiceProvider.class);
                   if(serviceProvider[0].getUserName().equals(serviceProviderUserName)) {
                       ServiceProviderId[0] = serviceProvider[0].getId();

                       ArrayList<WorkRating>alWorkRating = serviceProvider[0].getWorkRatingList();
                       if(alWorkRating==null){
                           alWorkRating = new ArrayList<>();
                           alWorkRating.add(workRating);
                           serviceProvider[0].setWorkRatingList(alWorkRating);
                           int averageWorkRating = 0;
                           int sum = 0;
                           for(int i=0;i<alWorkRating.size();i++){
                               sum =sum+alWorkRating.get(i).getRating();
                           }
                           averageWorkRating = sum/alWorkRating.size();
                           serviceProvider[0].setAverageWorkRating(averageWorkRating);
                           break;
                       }
                       else{
                           alWorkRating.add(workRating);
                           serviceProvider[0].setWorkRatingList(alWorkRating);
                           int averageWorkRating = 0;
                           int sum = 0;
                           for(int i=0;i<alWorkRating.size();i++){
                               sum =sum+alWorkRating.get(i).getRating();
                           }
                           averageWorkRating = sum/alWorkRating.size();
                           serviceProvider[0].setAverageWorkRating(averageWorkRating);
                           break;
                       }
                   }
               }
               DatabaseReference newDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(ServiceProviderId[0]);
               newDatabaseReference.setValue(serviceProvider[0]);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
   }


    class CompletedVisitsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return alCompletedVisitRequests.size();
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
            convertView = getActivity().getLayoutInflater().inflate(R.layout.completed_visit_request_list_row,null);
            TextView txt_sent_visit_request_Id = convertView.findViewById(R.id.txt_completed_visit_request_Id);
            TextView txt_to = convertView.findViewById(R.id.txt_from);
            TextView txt_potential_time = convertView.findViewById(R.id.txt_potential_time);
            TextView txt_duration = convertView.findViewById(R.id.txt_duration);
            TextView txt_visit_cost = convertView.findViewById(R.id.txt_visit_cost);
            Button btn_delete_completed_visit = convertView.findViewById(R.id.btn_delete_completed_visit);

            txt_sent_visit_request_Id.setText(alCompletedVisitRequests.get(position).getRequestId());
            txt_to.setText(alCompletedVisitRequests.get(position).getUserName());
            txt_potential_time.setText(alCompletedVisitRequests.get(position).getPotentialTimeAndDate());
            txt_duration.setText(String.valueOf(alCompletedVisitRequests.get(position).getDuration()));
            txt_visit_cost.setText(String.valueOf(alCompletedVisitRequests.get(position).getVisitCost()));

            btn_delete_completed_visit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alCompletedVisitRequests.remove(position);
                    CompletedVisitsListAdapter completedVisitsListAdapter = new CompletedVisitsListAdapter();
                    listViewCompletedVisits.setAdapter(completedVisitsListAdapter);
                    DatabaseReference customer_ref  = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
                    customer_ref.child(MainActivity.mcustomer.getId()+"/"+"completedVisitsList").setValue(alCompletedVisitRequests);
                }
            });





            return convertView;
        }
    }

    public void addPendingVisitToCompletedVisitInServiceProvider(final VisitRequest visitRequest, final String serviceProviderUserNme){
        final String[] id = new String[1];
        final ServiceProvider[] serviceProvider = new ServiceProvider[1];
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    serviceProvider[0] = ssn.getValue(ServiceProvider.class);
                    if(serviceProvider[0].getUserName().equals(serviceProviderUserNme)){
                        id[0] = serviceProvider[0].getId();
                        ArrayList<VisitRequest>alCompletedVisits = serviceProvider[0].getCompletedVisitsList();
                        if(alCompletedVisits==null){
                            alCompletedVisits= new ArrayList<>();
                            alCompletedVisits.add(visitRequest);
                            serviceProvider[0].setCompletedVisitsList(alCompletedVisits);
                            break;
                        }
                        else{
                            alCompletedVisits.add(visitRequest);
                            serviceProvider[0].setCompletedVisitsList(alCompletedVisits);
                            break;
                        }


                    }
                }
                DatabaseReference newref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").
                        child(serviceProvider[0].getId());
                newref.setValue(serviceProvider[0]);

                removePendingVisitFromServiceProvider(visitRequest,serviceProviderUserNme);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removePendingVisitFromServiceProvider(final VisitRequest visitRequest, final String serviceProviderUserNme){
        final String[] id = new String[1];
        final ServiceProvider[] serviceProvider = new ServiceProvider[1];
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    serviceProvider[0] = ssn.getValue(ServiceProvider.class);
                    if(serviceProvider[0].getUserName().equals(serviceProviderUserNme)){
                        id[0] = serviceProvider[0].getId();
                        ArrayList<VisitRequest>alPendingVisitRequests = serviceProvider[0].getPendingVisitsList();
                        if(alPendingVisitRequests!=null){
                        for(int i=0;i<alPendingVisitRequests.size();i++) {
                            if (alPendingVisitRequests.get(i).getRequestId().equals(visitRequest.getRequestId())) {
                                alPendingVisitRequests.remove(i);
                                serviceProvider[0].setPendingVisitsList(alPendingVisitRequests);
                                break;
                            }
                        }
                        }
                        break;
                    }
                }
                DatabaseReference newref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").
                        child(serviceProvider[0].getId());
                newref.setValue(serviceProvider[0]);


              getLatestData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getLatestData(){
        DatabaseReference databaseReference  = FirebaseDatabase .getInstance().getReference("Users").child("Customers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    Customer customer = ssn.getValue(Customer.class);
                    if(customer.getId().equals(MainActivity.mcustomer.getId())){

                      //  Toast.makeText(getActivity(),"Inside getLatest Data",Toast.LENGTH_LONG).show();

                        if(customer.getReceivedVisitRequestList()==null){
                            // do nothing
                        }

                        else{
                            alReceivedVisitRequests = customer.getReceivedVisitRequestList();
                            CustomAdapterReceivedVisitRequests customAdapterReceivedVisitRequests = new CustomAdapterReceivedVisitRequests();
                            listViewReceivedVisitRequests.setAdapter(customAdapterReceivedVisitRequests);
                        }


                        if (customer.getPendingVisitsList()==null){
                            // do nothing
                        }
                        else{
                            alPendingVisitRequests = customer.getPendingVisitsList();
                            PendingVisitRequestsAdapter pendingVisitRequestsAdapter = new PendingVisitRequestsAdapter();
                            listViewPendingVisits.setAdapter(pendingVisitRequestsAdapter);
                        }


                        if(customer.getCompletedVisitsList()==null){
                            // do nothing
                        }
                        else{
                            alCompletedVisitRequests = customer.getCompletedVisitsList();
                            CompletedVisitsListAdapter  completedVisitsListAdapter = new CompletedVisitsListAdapter();
                            listViewCompletedVisits.setAdapter(completedVisitsListAdapter);
                        }


                        if(customer.getReceivedWorkRequestList()==null){
                            // do nothing
                        }
                        else{
                            alReceivedWorkRequests = customer.getReceivedWorkRequestList();
                            ReceivedWorkRequestsListAdapter receivedWorkRequestsListAdapter= new ReceivedWorkRequestsListAdapter();
                            listViewReceivedWorkRequests.setAdapter(receivedWorkRequestsListAdapter);
                        }


                        if(customer.getPendingWorkList()==null){
                            // do nothing
                        }

                        else{
                            alPendingWorks  = customer.getPendingWorkList();
                           PendingWorkRequestsListAdapter pendingWorkRequestsListAdapter = new PendingWorkRequestsListAdapter();
                           listViewPendingWorkRequests.setAdapter(pendingWorkRequestsListAdapter);
                        }


                        if(customer.getCompletedWorkList()==null){
                            // do nothing
                        }
                        else{
                            alCompletedWorks = customer.getCompletedWorkList();
                            CompletedWorkRequestsListAdapter completedWorkRequestsListAdapter = new CompletedWorkRequestsListAdapter();
                            listViewCompletedWorks.setAdapter(completedWorkRequestsListAdapter);
                        }


                    }// end outer most if
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    class ReceivedWorkRequestsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return alReceivedWorkRequests.size();
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
            convertView = getActivity().getLayoutInflater().inflate(R.layout.received_work_request_list_row,null);
            TextView txt_received_work_request_Id = convertView.findViewById(R.id.txt_received_visit_request_Id);
            TextView txt_from = convertView.findViewById(R.id.txt_from);
            TextView txt_start_date = convertView.findViewById(R.id.txt_startDate);
            TextView txt_end_date = convertView.findViewById(R.id.txt_endDate);
            TextView txt_mode = convertView.findViewById(R.id.txt_mode);
            TextView txt_estimated_cost = convertView.findViewById(R.id.txt_estimated_cost);
            Button btn_accept =  convertView.findViewById(R.id.btn_accept_work_request);
            Button btn_reject = convertView.findViewById(R.id.btn_reject_work_request);

            txt_received_work_request_Id.setText(alReceivedWorkRequests.get(position).getRequestId());
            txt_from.setText(alReceivedWorkRequests.get(position).getUserName());
            txt_start_date.setText(alReceivedWorkRequests.get(position).getStartDate());
            txt_end_date.setText(alReceivedWorkRequests.get(position).getEndDate());
            txt_mode .setText(alReceivedWorkRequests.get(position).getMode());
            txt_estimated_cost.setText(String.valueOf(alReceivedWorkRequests.get(position).getEstimatedCost()));
            btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WorkRequest workRequest = new WorkRequest(alReceivedWorkRequests.get(position).getRequestId(),
                            alReceivedWorkRequests.get(position).getUserName(),"Pending",alReceivedWorkRequests.get(position).getStartDate(),
                            alReceivedWorkRequests.get(position).getEndDate(),alReceivedWorkRequests.get(position).getMode(),
                            alReceivedWorkRequests.get(position).getEstimatedCost(),alReceivedWorkRequests.get(position).getRatedByServiceProvider());

                        AddReceivedWorkRequestToPendingWorksInCustomerAndServiceProvider(workRequest,MainActivity.mcustomer.getId(),
                            position,alReceivedWorkRequests.get(position).getUserName());
                }
            });
      btn_reject.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         alReceivedWorkRequests.remove(position);
         MainActivity.mcustomer.setReceivedWorkRequestList( alReceivedWorkRequests);
      ReceivedWorkRequestsListAdapter receivedWorkRequestsListAdapter = new  ReceivedWorkRequestsListAdapter();
         listViewReceivedWorkRequests.setAdapter(receivedWorkRequestsListAdapter );
         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Customers")
                 .child(MainActivity.mcustomer.getId());
         databaseReference.setValue(MainActivity.mcustomer);
     }
 });




            return convertView;
        }
    }


    class PendingWorkRequestsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return alPendingWorks.size();
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
            convertView = getActivity().getLayoutInflater().inflate(R.layout.pending_work_request_list_row,null);
            TextView txt_received_work_request_Id = convertView.findViewById(R.id.txt_received_work_request_Id);
            final TextView txt_from = convertView.findViewById(R.id.txt_from);
            TextView txt_start_date = convertView.findViewById(R.id.txt_startDate);
            TextView txt_end_date = convertView.findViewById(R.id.txt_endDate);
            TextView txt_mode = convertView.findViewById(R.id.txt_mode);
            TextView txt_estimated_cost = convertView.findViewById(R.id.txt_estimated_cost);
            Button btn_end_work =  convertView.findViewById(R.id.btn_end_work);


            txt_received_work_request_Id.setText(alPendingWorks.get(position).getRequestId());
            txt_from.setText(alPendingWorks.get(position).getUserName());
            txt_start_date.setText(alPendingWorks.get(position).getStartDate());
            txt_end_date.setText(alPendingWorks.get(position).getEndDate());
            txt_mode .setText(alPendingWorks.get(position).getMode());
            txt_estimated_cost.setText(String.valueOf(alPendingWorks.get(position).getEstimatedCost()));
            final WorkRequest workRequest = new WorkRequest(txt_received_work_request_Id.getText().toString(),txt_from.getText().toString(),
                    "Completed",txt_start_date.getText().toString(),txt_end_date.getText().toString(),
                    txt_mode.getText().toString(),Integer.parseInt(txt_estimated_cost.getText().toString()),
                    alPendingWorks.get(position).getRatedByServiceProvider());
            btn_end_work.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  showWorkRatingDialog(txt_from.getText().toString(),position,workRequest);

                 /* transferPendingWorkToCompletedworkListOfCustomerAndServiceProvider(workRequest,
                         MainActivity.mcustomer.getUserName(),position,txt_from.getText().toString());*/
                }
            });




            return convertView;
        }
    }

    class CompletedWorkRequestsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return alCompletedWorks.size();
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
            convertView = getActivity().getLayoutInflater().inflate(R.layout.completed_work_list_row,null);
            TextView txt_received_work_request_Id = convertView.findViewById(R.id.txt_received_visit_request_Id);
            TextView txt_from = convertView.findViewById(R.id.txt_from);
            TextView txt_start_date = convertView.findViewById(R.id.txt_startDate);
            TextView txt_end_date = convertView.findViewById(R.id.txt_endDate);
            TextView txt_mode = convertView.findViewById(R.id.txt_mode);
            TextView txt_estimated_cost = convertView.findViewById(R.id.txt_estimated_cost);
            Button btn_delete_completed_work =  convertView.findViewById(R.id.btn_delete_completed_work);


            txt_received_work_request_Id.setText(alCompletedWorks.get(position).getRequestId());
            txt_from.setText(alCompletedWorks.get(position).getUserName());
            txt_start_date.setText(alCompletedWorks.get(position).getStartDate());
            txt_end_date.setText(alCompletedWorks.get(position).getEndDate());
            txt_mode .setText(alCompletedWorks.get(position).getMode());
            txt_estimated_cost.setText(String.valueOf(alCompletedWorks.get(position).getEstimatedCost()));

            btn_delete_completed_work.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   alCompletedWorks.remove(position);
                    CompletedWorkRequestsListAdapter completedWorkRequestsListAdapter = new CompletedWorkRequestsListAdapter();
                    listViewCompletedWorks.setAdapter(completedWorkRequestsListAdapter);
                    DatabaseReference customer_ref  = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
                    customer_ref.child(MainActivity.mcustomer.getId()+"/"+"completedWorkList").setValue( alCompletedWorks);


                }
            });
                       return convertView;
        }
    }



    public void AddReceivedWorkRequestToPendingWorksInCustomerAndServiceProvider(final WorkRequest workRequest, final String customerId, final int position,final String serviceProviderUserName){
        final Customer[] customer = new Customer[1];
        final DatabaseReference customer_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
        customer_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    customer[0] = ssn.getValue(Customer.class);
                    if(customer[0].getId().equals(customerId)){
                       alPendingWorks= customer[0].getPendingWorkList();
                        if(alPendingWorks==null){
                            alPendingWorks= new ArrayList<>();
                            alPendingWorks.add(workRequest);


                            customer[0].setPendingWorkList(alPendingWorks);

                            alReceivedWorkRequests.remove(position);
                            customer[0].setReceivedWorkRequestList(alReceivedWorkRequests);
                            ReceivedWorkRequestsListAdapter receivedWorkRequestsListAdapter = new ReceivedWorkRequestsListAdapter();
                            listViewReceivedWorkRequests.setAdapter( receivedWorkRequestsListAdapter);
                            customer_ref.child(customer[0].getId()).setValue(customer[0]);
                        }
                        else{

                            alPendingWorks.add(workRequest);

                            customer[0].setPendingWorkList(alPendingWorks);

                            alReceivedWorkRequests.remove(position);
                            customer[0].setReceivedWorkRequestList(alReceivedWorkRequests);
                            ReceivedWorkRequestsListAdapter receivedWorkRequestsListAdapter = new ReceivedWorkRequestsListAdapter();
                            listViewReceivedWorkRequests.setAdapter( receivedWorkRequestsListAdapter);
                            customer_ref.child(customer[0].getId()).setValue(customer[0]);

                        }


                        workRequest.setUserName(MainActivity.mcustomer.getUserName());
                        AddWorkRequestToPendingWorkInServiceProvider(workRequest,serviceProviderUserName);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void AddWorkRequestToPendingWorkInServiceProvider(final WorkRequest workRequest, final String serviceProviderUserNme){
        final String[] id = new String[1];
        final ServiceProvider[] serviceProvider = new ServiceProvider[1];
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    serviceProvider[0] = ssn.getValue(ServiceProvider.class);
                    if(serviceProvider[0].getUserName().equals(serviceProviderUserNme)){
                        id[0] = serviceProvider[0].getId();
                        ArrayList<WorkRequest>alPendingWorks = serviceProvider[0].getPendingWorkList();
                        if(alPendingWorks==null){
                            alPendingWorks= new ArrayList<>();
                            alPendingWorks.add(workRequest);
                            serviceProvider[0].setPendingWorkList(alPendingWorks);
                            break;
                        }
                        else{
                            alPendingWorks.add(workRequest);
                            serviceProvider[0].setPendingWorkList(alPendingWorks);
                            break;
                        }


                    }
                }
                DatabaseReference newref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").
                        child(serviceProvider[0].getId());
                newref.setValue(serviceProvider[0]);
                removeSentWorkRequestFromServiceProvider(workRequest,serviceProviderUserNme);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removeSentWorkRequestFromServiceProvider(final WorkRequest workRequest, final String serviceProviderUserNme){
        final String[] id = new String[1];
        final ServiceProvider[] serviceProvider = new ServiceProvider[1];
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    serviceProvider[0] = ssn.getValue(ServiceProvider.class);
                    if(serviceProvider[0].getUserName().equals(serviceProviderUserNme)){
                        id[0] = serviceProvider[0].getId();
                        ArrayList<WorkRequest>alSentWoRkequests = serviceProvider[0].getSentWorkRequestList();
                        for(int i=0;i<alSentWoRkequests.size();i++){
                            if(alSentWoRkequests.get(i).getRequestId().equals(workRequest.getRequestId())){
                                alSentWoRkequests.remove(i);
                                serviceProvider[0].setSentWorkRequestList(alSentWoRkequests);
                                break;
                            }
                        }
                        break;
                    }
                }
                DatabaseReference newref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").
                        child(serviceProvider[0].getId());
                newref.setValue(serviceProvider[0]);

                getLatestData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void transferPendingWorkToCompletedworkListOfCustomerAndServiceProvider(final WorkRequest workRequest, final String customerUserName, final int position, final String serviceProviderUserName){
        final DatabaseReference cus_ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
        final Customer customer[] = new Customer[1];
        cus_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn:dataSnapshot.getChildren()){
                    customer[0] = ssn.getValue(Customer.class);
                    if(customer[0].getUserName().equals(customerUserName)){
                        alCompletedWorks = customer[0].getCompletedWorkList();
                        if(alCompletedWorks ==null){
                            alCompletedWorks  = new ArrayList<>();
                            alCompletedWorks .add(workRequest);
                            customer[0].setCompletedWorkList (alCompletedWorks);
                            alPendingWorks.remove(position);
                            customer[0].setPendingWorkList( alPendingWorks);

                            PendingWorkRequestsListAdapter pendingWorkRequestsAdapter = new PendingWorkRequestsListAdapter();
                            listViewPendingWorkRequests.setAdapter(pendingWorkRequestsAdapter);
                            cus_ref.child(customer[0].getId()).setValue(customer[0]);



                        }
                        else{
                            alCompletedWorks .add(workRequest);
                            customer[0].setCompletedWorkList (alCompletedWorks);
                            alPendingWorks.remove(position);
                            customer[0].setPendingWorkList( alPendingWorks);

                            PendingWorkRequestsListAdapter pendingWorkRequestsAdapter = new PendingWorkRequestsListAdapter();
                            listViewPendingWorkRequests.setAdapter(pendingWorkRequestsAdapter);
                            cus_ref.child(customer[0].getId()).setValue(customer[0]);



                        }
                    }
                }
                workRequest.setUserName(MainActivity.mcustomer.getUserName());
                addPendingWorkToCompletedWorkInServiceProvider(workRequest,serviceProviderUserName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addPendingWorkToCompletedWorkInServiceProvider(final WorkRequest workRequest, final String serviceProviderUserNme){
        final String[] id = new String[1];
        final ServiceProvider[] serviceProvider = new ServiceProvider[1];
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    serviceProvider[0] = ssn.getValue(ServiceProvider.class);
                    if(serviceProvider[0].getUserName().equals(serviceProviderUserNme)){
                        id[0] = serviceProvider[0].getId();
                        ArrayList<WorkRequest>alCompletedWorks = serviceProvider[0].getCompletedWorkList();
                        if(alCompletedWorks==null){
                            alCompletedWorks= new ArrayList<>();
                            alCompletedWorks.add(workRequest);
                            serviceProvider[0].setCompletedWorkList(alCompletedWorks);
                            break;
                        }
                        else{
                            alCompletedWorks.add(workRequest);
                            serviceProvider[0].setCompletedWorkList(alCompletedWorks);
                            break;
                        }


                    }
                }
                DatabaseReference newref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").
                        child(serviceProvider[0].getId());
                newref.setValue(serviceProvider[0]);

                removePendingWorkFromServiceProvider(workRequest,serviceProviderUserNme);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removePendingWorkFromServiceProvider(final WorkRequest workRequest, final String serviceProviderUserNme){
        final String[] id = new String[1];
        final ServiceProvider[] serviceProvider = new ServiceProvider[1];
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    serviceProvider[0] = ssn.getValue(ServiceProvider.class);
                    if(serviceProvider[0].getUserName().equals(serviceProviderUserNme)){
                        id[0] = serviceProvider[0].getId();
                        ArrayList<WorkRequest>alPendingWorks= serviceProvider[0].getPendingWorkList();
                        if(alPendingWorks!=null){
                            for(int i=0;i<alPendingWorks.size();i++) {
                                if (alPendingWorks.get(i).getRequestId().equals(workRequest.getRequestId())) {
                                    alPendingWorks.remove(i);
                                    serviceProvider[0].setPendingWorkList(alPendingWorks);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
                DatabaseReference newref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").
                        child(serviceProvider[0].getId());
                newref.setValue(serviceProvider[0]);


                getLatestData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
