package com.example.kaamasaan;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SendVisitRequestActivity extends AppCompatActivity {
    ProgressDialog mprogressdialog;
    TimePicker timePicker;
    DatePicker datePicker;
    EditText ed_duration, ed_cost;
    Button btn_send;
    String format;
  //  Customer customer;   // This is the customer to whome visit request is being sent
    String customerID;
    String customerUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_visit_request);
        timePicker = findViewById(R.id.visit_request_time_picker);
        datePicker  =findViewById(R.id.visit_request_date_picker);
        ed_duration = findViewById(R.id.ed_duration);
        ed_cost  = findViewById(R.id.ed_cost);
        btn_send = findViewById(R.id.btn_send_visit_request);
        mprogressdialog = new ProgressDialog(this);
        mprogressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mprogressdialog.setMessage("Sending Visit Request...");
        mprogressdialog.setIndeterminate(true);
        mprogressdialog.setCancelable(false);
        Intent intent = getIntent();
       // customer = intent.getParcelableExtra("customer");
        customerID = intent.getStringExtra("customerid");
        customerUserName = intent.getStringExtra("customerUserName");




        if(MainActivity.mserviceProvider.getVisitCost()==0){
            ed_cost.setText(0);
           ed_cost.setKeyListener(null);
        }
        else{
            ed_cost.setText(String.valueOf(MainActivity.mserviceProvider.getVisitCost()));
        }

        btn_send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
              //  Toast.makeText(SendVisitRequestActivity.this,timePicker.getCurrentHour()+"",Toast.LENGTH_LONG).show();


            if(ed_duration.getText().toString().isEmpty()){
                ed_duration.setError("Duration field must be filled");
                ed_duration.requestFocus();
            }
            else if(ed_cost.getText().toString().isEmpty()){
                ed_cost.setError("Cost field must be filled");
                ed_cost.requestFocus();
            }
            else {


                try {
                    DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(MainActivity.mserviceProvider.getId());
                    String visitRequestId = sp_ref.push().getKey();
                    int duration =  Integer.parseInt(ed_duration.getText().toString());
                    int cost  =  Integer.parseInt(ed_cost.getText().toString());
                    VisitRequest sentVisitRequest = new VisitRequest(visitRequestId,customerID,customerUserName,"Sent",getTime(),duration, cost,
                            MainActivity.mserviceProvider.getProfilePicUrl());
                    ArrayList<VisitRequest> listVisitRequestServiceProvider =MainActivity.mserviceProvider.getSentVisitRequestList();
                     sendVisitRequest(sentVisitRequest);

                     onBackPressed();



                  /*  VisitRequest receivedVisitRequest = new VisitRequest(visitRequestId,MainActivity.mserviceProvider.getId(),MainActivity.mserviceProvider.getUserName(),"Received",getTime(),duration, cost);
                   manipulateVisitRequestListOfCustomer(receivedVisitRequest,customerID);
*/

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }// end main else
            }// end onClick


        });

    }

    public String getTime() throws ParseException {
        int hour = timePicker.getCurrentHour();
        int min = timePicker.getCurrentMinute();

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        String DateAndTime  = FormateDateAndTIme(hour, min,day,month, year);


   return DateAndTime;

    }
 private String FormateDateAndTIme(int hour, int min,int day, int month, int year) throws ParseException {

       if(hour==0){
           format = "am";
       }
       else if(hour>=1&&hour<=12){
           format = "am";
       }
       else if(hour>=13&&hour<=24){
           format = "pm";
       }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(hour).append(" : ").append(min)
                .append(" ").append(format);


      // Toast.makeText(SendVisitRequestActivity.this,stringBuilder.toString(),Toast.LENGTH_LONG).show();

        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(day).append("-").append(month).append("-").append(year);

        String dateTime = stringBuilder2.toString()+" "+stringBuilder.toString();
      //  Toast.makeText(SendVisitRequestActivity.this,dateTime,Toast.LENGTH_LONG).show();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day,hour,min,00);
       SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd-MMM-yyyy hh:mm:a");
      String formatedDate =  simpleDateFormat.format(calendar.getTime());
      //java.util.Date date1 = simpleDateFormat.parse(formatedDate);


       return formatedDate;
    }

    public void manipulateVisitRequestListOfCustomer(final VisitRequest visitRequest, final String customerId){
        final Customer[] customer = new Customer[1];
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Customers");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    customer[0] = ssn.getValue(Customer.class);
                    if(customer[0].getId().equals(customerId)){
                        ArrayList<VisitRequest>alReceivedVisitRequest = customer[0].getReceivedVisitRequestList();
                        if(alReceivedVisitRequest==null){
                            alReceivedVisitRequest = new ArrayList<>();
                            alReceivedVisitRequest.add(visitRequest);
                            customer[0].setReceivedVisitRequestList(alReceivedVisitRequest);
                            break;
                        }
                        else{
                            alReceivedVisitRequest.add(visitRequest);
                            customer[0].setReceivedVisitRequestList(alReceivedVisitRequest);
                             break;
                        }

                    }
                }
                Toast.makeText(getApplicationContext(),"Visit Request sent successfully",Toast.LENGTH_LONG).show();
                DatabaseReference newdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(customerId);
                newdatabaseReference.setValue(customer[0]);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendVisitRequest(final VisitRequest visitRequest){
        final DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
        final ServiceProvider[] serviceProvider = new ServiceProvider[1];
        sp_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn:dataSnapshot.getChildren()){
                     serviceProvider[0] = ssn.getValue(ServiceProvider.class);
                    if(serviceProvider[0].getId().equals(MainActivity.mserviceProvider.getId())){
                        ArrayList<VisitRequest> listVisitRequestServiceProvider = serviceProvider[0].getSentVisitRequestList();

                        if(listVisitRequestServiceProvider==null){
                            listVisitRequestServiceProvider= new ArrayList<VisitRequest>();
                            listVisitRequestServiceProvider.add(visitRequest);
                            serviceProvider[0].setSentVisitRequestList(listVisitRequestServiceProvider);
                            sp_ref.child(serviceProvider[0].getId()).setValue(serviceProvider[0]);
                        }
                        else{
                            listVisitRequestServiceProvider.add(visitRequest);
                            serviceProvider[0].setSentVisitRequestList(listVisitRequestServiceProvider);
                            sp_ref.child(serviceProvider[0].getId()).setValue(serviceProvider[0]);
                            // Toast.makeText(SendVisitRequestActivity.this,"size: "+listVisitRequestServiceProvider.size(),

                        }

                        // manipulation of visit request for customer
                        visitRequest.setUserId(MainActivity.mserviceProvider.getId());
                        visitRequest.setUserName(MainActivity.mserviceProvider.getUserName());
                        manipulateVisitRequestListOfCustomer(visitRequest,customerID);



                    }// end outer most if
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
