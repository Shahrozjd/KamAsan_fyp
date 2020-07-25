package com.example.kaamasaan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WillingnessActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference broadcast_ref;
    ArrayList<VisitRequest> alReceivedVisitRequests;   // recieved requests on this broadcast
    ArrayList<VisitRequest>alPendingVisitRequests;
    ListView listViewReceivedVisitRequests;
    String broadCastId;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_willingness);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Willingness");
        listViewReceivedVisitRequests = findViewById(R.id.lv_received_visit_requests);
        Intent intent = getIntent();
        broadCastId = intent.getStringExtra("broadCastId");

        getVisitRequestsOnthisBroadCast(broadCastId);

    }

    class CustomAdapterReceivedVisitRequests extends BaseAdapter {

        @Override
        public int getCount() {
            return alReceivedVisitRequests.size();
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
            convertView = getLayoutInflater().inflate(R.layout.received_visit_request_list_row, null);
            TextView txt_sent_visit_request_Id = convertView.findViewById(R.id.txt_received_visit_request_Id);
            TextView txt_to = convertView.findViewById(R.id.txt_from);
            TextView txt_potential_time = convertView.findViewById(R.id.txt_potential_time);
            TextView txt_duration = convertView.findViewById(R.id.txt_duration);
            TextView txt_visit_cost = convertView.findViewById(R.id.txt_visit_cost);
            Button btn_accept = convertView.findViewById(R.id.btn_accept_visit_request);
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
                            alReceivedVisitRequests.get(position).getUserId(), alReceivedVisitRequests.get(position).getUserName(),"Pending",
                            alReceivedVisitRequests.get(position).potentialTimeAndDate,alReceivedVisitRequests.get(position).duration,
                            alReceivedVisitRequests.get(position).getVisitCost(),alReceivedVisitRequests.get(position).getImageUrl());
                    alPendingVisitRequests = MainActivity.mcustomer.getPendingVisitsList();
                    if(alPendingVisitRequests==null) {
                        alPendingVisitRequests = new ArrayList<>();
                        alPendingVisitRequests.add(visitRequest);

                        MainActivity.mcustomer.setPendingVisitsList(alPendingVisitRequests);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                .child("Customers").child(MainActivity.mcustomer.getId());
                        databaseReference.setValue(MainActivity.mcustomer);

                        visitRequest.setUserName(MainActivity.mcustomer.getUserName());
                        AddVisitRequestToPendingVisitInServiceProvider(visitRequest,alReceivedVisitRequests.get(position).getUserName());
                        alReceivedVisitRequests.remove(position);
                        removeVisitRequestFromBroadCast(broadCastId,alReceivedVisitRequests);
                    }
                    else{
                        alPendingVisitRequests.add(visitRequest);

                        MainActivity.mcustomer.setPendingVisitsList(alPendingVisitRequests);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                .child("Customers").child(MainActivity.mcustomer.getId());
                        databaseReference.setValue(MainActivity.mcustomer);

                        visitRequest.setUserName(MainActivity.mcustomer.getUserName());
                        AddVisitRequestToPendingVisitInServiceProvider(visitRequest,alReceivedVisitRequests.get(position).getUserName());
                        alReceivedVisitRequests.remove(position);
                        removeVisitRequestFromBroadCast(broadCastId,alReceivedVisitRequests);
                    }
                }
            });
            btn_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alReceivedVisitRequests.remove(position);
                    removeVisitRequestFromBroadCast(broadCastId,alReceivedVisitRequests);
                }
            });


            return convertView;
        }

    }

    private void getVisitRequestsOnthisBroadCast(final String broadCastId){
        broadcast_ref = FirebaseDatabase.getInstance().getReference("Broadcasts");
        broadcast_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn:dataSnapshot.getChildren()){
                    BroadCastRequest broadCastRequest = ssn.getValue(BroadCastRequest.class);
                    if(broadCastRequest.getId().equals(broadCastId)){
                        alReceivedVisitRequests = broadCastRequest.getVisitRequestsList();
                        if(alReceivedVisitRequests==null){
                            Toast.makeText(WillingnessActivity.this,"No Visit Request " +
                                    "has been received on this broadcast yet.",Toast.LENGTH_LONG).show();
                        }
                        else{
                            CustomAdapterReceivedVisitRequests customAdapterReceivedVisitRequests = new CustomAdapterReceivedVisitRequests();
                            listViewReceivedVisitRequests.setAdapter(customAdapterReceivedVisitRequests);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void removeVisitRequestFromBroadCast(final String broadCastId,final ArrayList<VisitRequest>listReceivedVisitRequests){
        broadcast_ref = FirebaseDatabase.getInstance().getReference("Broadcasts");
        broadcast_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn:dataSnapshot.getChildren()){
                    BroadCastRequest broadCastRequest = ssn.getValue(BroadCastRequest.class);
                    if(broadCastRequest.getId().equals(broadCastId)){
                        broadCastRequest.setVisitRequestsList(listReceivedVisitRequests);
                        DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference("Broadcasts")
                                .child(broadCastId);
                        databaseReference.setValue(broadCastRequest);
                        CustomAdapterReceivedVisitRequests customAdapterReceivedVisitRequests = new CustomAdapterReceivedVisitRequests();
                        listViewReceivedVisitRequests.setAdapter(customAdapterReceivedVisitRequests);

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
                        child(id[0]);
                newref.setValue(serviceProvider[0]);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

