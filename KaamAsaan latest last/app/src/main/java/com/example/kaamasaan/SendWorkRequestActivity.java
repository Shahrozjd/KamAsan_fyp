package com.example.kaamasaan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SendWorkRequestActivity extends AppCompatActivity {
    String customerName;
    String customerID;
    String requestId;
    DatePicker startDate, endDate;
    EditText  ed_cost;
    Button btn_send;
    String format;
    RadioGroup rg;
    RadioButton radio_Online, radio_Offline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_work_request);
        Intent intent = getIntent();
       customerName = intent.getStringExtra("customerName");
       customerID = intent.getStringExtra("customerId");
       requestId = intent.getStringExtra("requestId");

       System.out.println("ID Customer: "+customerID);
        System.out.println("ID Request: "+requestId);
        System.out.println("Name Customer: "+customerName);

        startDate = findViewById(R.id.start_date);
        endDate = findViewById(R.id.end_date);
        ed_cost  = findViewById(R.id.ed_cost);
        btn_send = findViewById(R.id.btn_send_work_request);
        rg = findViewById(R.id.radio_group);
        radio_Offline =findViewById(R.id.radio_offsite);
        radio_Online = findViewById(R.id.radio_onsite);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ed_cost.getText().toString().isEmpty()){
                    ed_cost.setError("Cost must be filled");
                }
                else{
                 //   Toast.makeText(SendWorkRequestActivity.this,"Work Request has sent successfully",Toast.LENGTH_LONG).show();
                     int startDay = startDate.getDayOfMonth();
                    int startMonth = startDate.getMonth();
                    int startYear = startDate.getYear();

                    String startDate = startDay+"-"+startMonth+"-"+startYear;

                    int endDay = endDate.getDayOfMonth();
                    int endMonth = endDate.getMonth();
                    int endYear = endDate.getYear();

                    String endDate =  endDay+"-"+endMonth+"-"+endYear;

                    int chkId = rg.getCheckedRadioButtonId();
                    View radioButton=null;
                    radioButton = rg.findViewById(chkId);
                    int idx = rg.indexOfChild(radioButton);
                    RadioButton r = (RadioButton) rg.getChildAt(idx);
                    String mode = r.getText().toString();

                    int cost = Integer.parseInt(ed_cost.getText().toString());

                    DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(MainActivity.mserviceProvider.getId());
                    String workRequestId= requestId;
                    WorkRequest workRequest = new WorkRequest(workRequestId,
                            customerName, "Sent",startDate,endDate,mode,cost);

                    sendWorkRequest(workRequest);
                    onBackPressed();





                }
            }
        });

    }

    public void sendWorkRequest(final WorkRequest workRequest){
        final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        final ServiceProvider[] serviceProvider = new ServiceProvider[1];
        final String[] serviceProviderId = new String[1];
        sp_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn:dataSnapshot.getChildren()){
                    serviceProvider[0] = ssn.getValue(ServiceProvider.class);
                    if(serviceProvider[0].getId().equals(MainActivity.mserviceProvider.getId())){
                        serviceProviderId[0] = serviceProvider[0].getId();
                        ArrayList<WorkRequest> listWorkRequestServiceProvider = serviceProvider[0].getSentWorkRequestList();

                        if(listWorkRequestServiceProvider==null){
                            listWorkRequestServiceProvider= new ArrayList<>();
                            listWorkRequestServiceProvider.add(workRequest);
                            serviceProvider[0].setSentWorkRequestList(listWorkRequestServiceProvider);
                            sp_ref.child(serviceProvider[0].getId()).setValue(serviceProvider[0]);
                            System.out.println("spid "+ serviceProvider[0].getId());

                        }
                        else{
                            listWorkRequestServiceProvider.add(workRequest);
                            serviceProvider[0].setSentWorkRequestList(listWorkRequestServiceProvider);
                            sp_ref.child(serviceProvider[0].getId()).setValue(serviceProvider[0]);
                            // Toast.makeText(SendVisitRequestActivity.this,"size: "+listVisitRequestServiceProvider.size(),

                        }

                        // manipulation of work request for customer

                        workRequest.setUserName(MainActivity.mserviceProvider.getUserName());
                        manipulateWorkRequestListOfCustomer(workRequest,customerName);



                    }// end outer most if


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void manipulateWorkRequestListOfCustomer(final WorkRequest workRequest, final String customerUserName) {
        final Customer[] customer = new Customer[1];
        final String[] customerId = new String[1];
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Customers");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ssn : dataSnapshot.getChildren()) {
                    customer[0] = ssn.getValue(Customer.class);
                    if (customer[0].getUserName().equals(customerUserName)) {
                        customerId[0] = customer[0].getId();
                        ArrayList<WorkRequest> alReceivedWorkRequest = customer[0].getReceivedWorkRequestList();
                        if (alReceivedWorkRequest  == null) {
                            alReceivedWorkRequest = new ArrayList<>();
                            alReceivedWorkRequest .add(workRequest);
                            customer[0].setReceivedWorkRequestList(alReceivedWorkRequest);
break;
                        } else {
                            alReceivedWorkRequest .add(workRequest);
                            customer[0].setReceivedWorkRequestList(alReceivedWorkRequest);
break;
                        }

                    }
                }
                Toast.makeText(getApplicationContext(), "Work Request sent successfully", Toast.LENGTH_LONG).show();
                DatabaseReference newdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(customerId[0]);
                newdatabaseReference.setValue(customer[0]);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
