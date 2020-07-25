package com.example.kaamasaan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SendVisitRequestActivityForBroadCast extends AppCompatActivity {
    ProgressDialog mprogressdialog;
    TimePicker timePicker;
    DatePicker datePicker;
    EditText ed_duration, ed_cost;
    Button btn_send;
    String format;
    String BroadCastId;
    //  Customer customer;   // This is the customer to whome visit request is being sent
    String customerID;
    String customerUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_visit_request_for_broad_cast);
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
        BroadCastId = intent.getStringExtra("broadcastId");

        if(MainActivity.mserviceProvider.getVisitCost()==0){
            ed_cost.setText(0);
            ed_cost.setKeyListener(null);
        }
        else{
            ed_cost.setText(String.valueOf(MainActivity.mserviceProvider.getVisitCost()));
        }

     btn_send.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             if(ed_duration.getText().toString().isEmpty()){
                 ed_duration.setError("Duration field must be filled");
                 ed_duration.requestFocus();
             }
             else if(ed_cost.getText().toString().isEmpty()){
                 ed_cost.setError("Cost field must be filled");
                 ed_cost.requestFocus();
             }

             else{
                 try {
                     DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").child(MainActivity.mserviceProvider.getId());
                     String visitRequestId = sp_ref.push().getKey();
                     int duration =  Integer.parseInt(ed_duration.getText().toString());
                     int cost  =  Integer.parseInt(ed_cost.getText().toString());
                     VisitRequest sentVisitRequest = new VisitRequest(visitRequestId,MainActivity.mserviceProvider.getId(),MainActivity.mserviceProvider.getUserName(),
                             "Received",getTime(),duration, cost,MainActivity.mserviceProvider.getProfilePicUrl());
                     ArrayList<VisitRequest> listVisitRequestServiceProvider =MainActivity.mserviceProvider.getSentVisitRequestList();
                     if(listVisitRequestServiceProvider==null){
                         Toast.makeText(SendVisitRequestActivityForBroadCast.this,
                                 "Visit Request send successfully",Toast.LENGTH_LONG).show();
                         listVisitRequestServiceProvider= new ArrayList<VisitRequest>();
                         listVisitRequestServiceProvider.add(sentVisitRequest);
                         MainActivity.mserviceProvider.setSentVisitRequestList(listVisitRequestServiceProvider);
                         addVisitRequesInVisitRequestListOfBroadCast(sentVisitRequest,BroadCastId);
                         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                 .child("ServiceProviders").child(MainActivity.mserviceProvider.getId());
                         databaseReference.setValue(MainActivity.mserviceProvider);

                         onBackPressed();
                     }
                     else{
                         Toast.makeText(SendVisitRequestActivityForBroadCast.this,
                                 "Visit Request send successfully",Toast.LENGTH_LONG).show();
                         listVisitRequestServiceProvider.add(sentVisitRequest);
                         MainActivity.mserviceProvider.setSentVisitRequestList(listVisitRequestServiceProvider);
                         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                 .child("ServiceProviders").child(MainActivity.mserviceProvider.getId());
                         databaseReference.setValue(MainActivity.mserviceProvider);
                         addVisitRequesInVisitRequestListOfBroadCast(sentVisitRequest,BroadCastId);
                         onBackPressed();
                         // Toast.makeText(SendVisitRequestActivity.this,"size: "+listVisitRequestServiceProvider.size(),

                     }


                 } catch (ParseException e) {
                     e.printStackTrace();
                 }
             }


         }
     });
    }
    public void addVisitRequesInVisitRequestListOfBroadCast(final VisitRequest visitRequest, final String broadCastId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Broadcasts");
        final BroadCastRequest[] broadCastRequest = new BroadCastRequest[1];
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    broadCastRequest[0] = ssn.getValue(BroadCastRequest.class);

                    if(broadCastRequest[0].getId().equals(broadCastId)){
                        ArrayList<VisitRequest> alVisitRequest = broadCastRequest[0].getVisitRequestsList();
                        if(alVisitRequest==null){
                            alVisitRequest  = new ArrayList<>();
                            alVisitRequest.add(visitRequest);
                            broadCastRequest[0].setVisitRequestsList(alVisitRequest);
                            DatabaseReference new_ref  = FirebaseDatabase.getInstance().getReference("Broadcasts").child(broadCastId);
                            new_ref.setValue(broadCastRequest[0]);
                            Toast.makeText(getApplicationContext(),"Visit request has sent successfully",Toast.LENGTH_LONG).show();

                        }
                        else{
                            alVisitRequest.add(visitRequest);
                            broadCastRequest[0].setVisitRequestsList(alVisitRequest);
                            DatabaseReference new_ref  = FirebaseDatabase.getInstance().getReference("Broadcasts").child(broadCastId);
                            new_ref.setValue(broadCastRequest[0]);
                            Toast.makeText(getApplicationContext(),"Visit request has sent successfully",Toast.LENGTH_LONG).show();

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
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
        if (hour == 0) {
            hour += 12;
            format = "am";
        } else if (hour == 12) {
            format = "pm";
        } else if (hour > 12) {
            hour -= 12;
            format = "pm";
        } else {
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


}
