package com.example.kaamasaan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



public class ServiceProviderSignUpPart3Activity extends AppCompatActivity {
    //TextView txt_title;
    Typeface mfont;
    Button signup;
    EditText start_hours,start_mins,end_hours,end_mins,ed_bio,ed_visitcost;
    RadioGroup radioGroup;
    RadioButton radioButton;
    FirebaseDatabase database;
    DatabaseReference user_ref;
    ProgressDialog mprogressdialog;
    StorageReference mStorageRef;
    Spinner spinner1,spinner2;
    ArrayList<String> alTime = new ArrayList<>();
    ArrayAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_sign_up_part3);
        mfont = Typeface.createFromAsset(this.getAssets(),"fonts/KaushanScript-Regular.otf");
       /* txt_title = findViewById(R.id.tv_title_signup3);
        txt_title.setTypeface(mfont);*/
        start_hours = findViewById(R.id.start_hours);
        start_mins = findViewById(R.id.start_minutes);
        end_hours = findViewById(R.id.end_hours);
        end_mins = findViewById(R.id.end_minutes);
        spinner1 =findViewById(R.id.spinnerFrom);
        spinner2 = findViewById(R.id.spinnerTo);
        radioGroup = findViewById(R.id.radio_group);
        ed_bio = findViewById(R.id.ed_bio);
        ed_visitcost = findViewById(R.id.ed_visitcost);
        mprogressdialog = new ProgressDialog(this);
        mprogressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mprogressdialog.setMessage("Signing Up...");
        mprogressdialog.setIndeterminate(true);
        mprogressdialog.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        user_ref = database.getReference("Users").child("ServiceProviders");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        signup = findViewById(R.id.btn_signup);
        alTime.add("am");
        alTime.add("pm");
        adapter = new ArrayAdapter(ServiceProviderSignUpPart3Activity.this,R.layout.spinner_item,alTime);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if(cm.getActiveNetworkInfo()== null) {
                    Toast.makeText(ServiceProviderSignUpPart3Activity.this,"Error: Connect to internet please",Toast.LENGTH_LONG).show();

                }

               else if(start_hours.getText().toString().isEmpty()||start_mins.getText().toString().isEmpty()||
                        end_hours.getText().toString().isEmpty()||end_mins.getText().toString().isEmpty()){
                    Toast.makeText(ServiceProviderSignUpPart3Activity.this,"Error: Time must be filled",Toast.LENGTH_SHORT).show();
                }
                else if(Double.parseDouble(start_hours.getText().toString())<1||Double.parseDouble(start_hours.getText().toString())>12){
                    start_hours.setError("Min : 1, Max : 12");
                    start_hours.requestFocus();
                }
                else if(Double.parseDouble(end_hours.getText().toString())<1||Double.parseDouble(end_hours.getText().toString())>12){
                    end_hours.setError("Min : 1, Max : 12");
                    end_hours.requestFocus();
                }
                else if(Double.parseDouble(start_mins.getText().toString())<0||Double.parseDouble(start_mins.getText().toString())>59){
                    start_mins.setError("Min : 0, Max : 59");
                    start_mins.requestFocus();
                }
                else if(Double.parseDouble(end_mins.getText().toString())<0||Double.parseDouble(end_mins.getText().toString())>59){
                    end_mins.setError("Min : 0, Max : 59");
                    end_mins.requestFocus();
                }
                else if(ed_visitcost.getText().toString().isEmpty()){
                    ed_visitcost.setError("Visit cost must be filled");
                    ed_visitcost.requestFocus();
                }
                else if(ed_bio.getText().toString().isEmpty()){
                    ed_bio.setError("must be filled");
                    ed_bio.requestFocus();
                }
                else if(ed_bio.getText().toString().length()<50){
                    ed_bio.setError("Minimum characters : 50");
                    ed_bio.requestFocus();
                }
                else if(ed_bio.getText().toString().length()>300){
                    ed_bio.setError("Maximum characters : 300");
                    ed_bio.requestFocus();
                }
                else {
                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    radioButton = (RadioButton) findViewById(selectedId);

                    SharedPreferences.Editor editor =ServiceProviderSignUpPart1Activity. sharedpreferences.edit();
                    editor.putString(ServiceProviderSignUpPart1Activity.availabilitytimekey,start_hours.getText().toString()+" "+start_mins.getText().toString()+" "+spinner1.getSelectedItem().toString()+"     "+end_hours.getText().toString()+" "+end_mins.getText().toString()+" "+spinner2.getSelectedItem().toString());
                    editor.putInt(ServiceProviderSignUpPart1Activity.visitcostkey,Integer.parseInt(ed_visitcost.getText().toString()));
                    editor.putString(ServiceProviderSignUpPart1Activity.responsetimekey,radioButton.getText().toString());
                    editor.putString(ServiceProviderSignUpPart1Activity.biokey,ed_bio.getText().toString());

                    editor.commit();




                    final String userName =   ServiceProviderSignUpPart1Activity.sharedpreferences.getString("userNamekey","");
                    String capturedImageUri = ServiceProviderSignUpPart1Activity.sharedpreferences.getString("urikey","");
                    Toast.makeText(getApplicationContext(),"pic uri: "+capturedImageUri,Toast.LENGTH_LONG).show();
                    final StorageReference storageReference = mStorageRef.child("ServiceProviders" + "/" + userName + ".jpg");
                    System.out.println("muri: "+capturedImageUri);
                    final String[] mDurl = {""};
                    mprogressdialog.show();
                    UploadTask utask = (UploadTask) storageReference.putFile(Uri.parse("content://media/external/images/media/6050"));
                    Task<Uri> urlTas= utask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            return storageReference.getDownloadUrl();

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                //  Toast.makeText(getApplicationContext(),"Task  is successful",Toast.LENGTH_LONG).show();
                                Uri downloadUri = task.getResult();
                                final String downloadURL = downloadUri.toString();
                                mDurl[0] = downloadURL;

                                String fullname =  ServiceProviderSignUpPart1Activity.sharedpreferences.getString("fullnamekey","");
                                String phone =   ServiceProviderSignUpPart1Activity.sharedpreferences.getString("phonekey","");
                                String password =   ServiceProviderSignUpPart1Activity.sharedpreferences.getString("passwordkey","");
                                double latitude =   Double.longBitsToDouble(ServiceProviderSignUpPart1Activity.sharedpreferences.getLong("latitudekey",Double.doubleToLongBits(0.0)));
                                double longitude =   Double.longBitsToDouble(ServiceProviderSignUpPart1Activity.sharedpreferences.getLong("longitudekey",Double.doubleToLongBits(0.0)));
                                String category =   ServiceProviderSignUpPart1Activity.sharedpreferences.getString("categorykey","");
                                int radius =   ServiceProviderSignUpPart1Activity.sharedpreferences.getInt("radiuskey",1);
                                String availabiltiytime =   ServiceProviderSignUpPart1Activity.sharedpreferences.getString("availabilitytimekey","");
                                //  Toast.makeText(ServiceProviderSignUpPart3Activity.this,availabiltiytime,Toast.LENGTH_LONG).show();
                                String responseTime =   ServiceProviderSignUpPart1Activity.sharedpreferences.getString("responsetimekey","");
                                String bio  = ServiceProviderSignUpPart1Activity.sharedpreferences.getString("biokey","");
                                int visitCost = ServiceProviderSignUpPart1Activity.sharedpreferences.getInt("visitcostkey",0);

                                String id = user_ref.push().getKey();
                                HashMap<String,ArrayList<Message>> chatHashMap = new HashMap<>();
                                int responseTimeRating = 0;
                                int averageWorkRating  = 1;
                                ArrayList<VisitRequest>sentVisitRequestList  = new ArrayList<>();
                                ArrayList<VisitRequest>pendingVisitsList  = new ArrayList<>();
                                ArrayList<VisitRequest>completedVisitsList  = new ArrayList<>();
                                ArrayList<ResponseTimeRating>responseTimeRatingList  = new ArrayList<>();
                                ArrayList<WorkRequest>sentWorkRequestList  = new ArrayList<>();
                                ArrayList<WorkRequest>pendingWorkList  = new ArrayList<>();
                                ArrayList<WorkRequest>completedWorkList  = new ArrayList<>();
                                ArrayList<WorkRating>workRatingList  = new ArrayList<>();


                                ServiceProvider sp = new ServiceProvider(id,fullname,userName,phone,password,category,availabiltiytime,responseTime,
                                        bio,"ServiceProvider",mDurl[0],latitude,longitude,radius,chatHashMap,responseTimeRating,averageWorkRating,visitCost,sentVisitRequestList
                                        ,pendingVisitsList,completedVisitsList,responseTimeRatingList,sentWorkRequestList,pendingWorkList,
                                        completedWorkList,workRatingList);
                                user_ref.child(id).setValue(sp);
                                mprogressdialog.dismiss();


                                Toast.makeText(ServiceProviderSignUpPart3Activity.this,"Signed up successfully. Now you can Sign in.",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ServiceProviderSignUpPart3Activity.this, MainActivity.class));
                                //finish();
                            }
                            else{
                                mprogressdialog.dismiss();
                                Toast.makeText(ServiceProviderSignUpPart3Activity.this,"Some Error occured:\n It may be due to network or internet speed.",Toast.LENGTH_LONG).show();

                            }
                        }
                    });




                }
            }
        });
    }
}
