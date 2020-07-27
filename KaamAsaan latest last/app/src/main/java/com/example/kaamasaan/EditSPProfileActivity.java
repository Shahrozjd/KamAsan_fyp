package com.example.kaamasaan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditSPProfileActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference cat_ref,sp_ref;
    ArrayList<String> al_cat =new ArrayList();
    ListView lv;
    ArrayAdapter<String> arrayAdapter;
    TextView tv_lat,tv_lng;
    EditText ed_name,ed_phone,ed_bio,ed_password,start_hours,start_mins,end_hours,end_mins,ed_visitcost;
    ScrollView scrollView;
    Button btnSave,btnChangeLocation;
    RadioGroup radioGroup;
    RadioButton radioButton;
    AppCompatImageButton btn_pic_gallery;
    AppCompatImageButton btn_pic_camera;
    AppCompatImageButton btn_rotate;
    CircleImageView iv;
    String selectedCategory = "";
    LocationManager lm;
    int REQ_Code = 100;
    private  static final int SELECT_FROM_GALLERY = 1;
    private static final int  CAPTURE_FROM_CAMERA = 2;
    Uri capturedImageUri;
    StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_s_p_profile);
        database = FirebaseDatabase.getInstance();
        cat_ref = database.getReference("Categories");
        sp_ref = database.getReference("Users").child("ServiceProviders");
        btnSave = findViewById(R.id.btn_save);
        btnChangeLocation = findViewById(R.id.btn_changelocation);
        ed_name = findViewById(R.id.ed_name);
        ed_phone = findViewById(R.id.ed_phone);
        ed_bio= findViewById(R.id.ed_bio);
        ed_password = findViewById(R.id.ed_password);
        start_hours = findViewById(R.id.start_hours);
        start_mins = findViewById(R.id.start_minutes);
        end_hours = findViewById(R.id.end_hours);
        end_mins = findViewById(R.id.end_minutes);
        radioGroup = findViewById(R.id.radio_group);
        ed_bio = findViewById(R.id.ed_bio);
        ed_visitcost = findViewById(R.id.ed_visit_cost);
        tv_lat = findViewById(R.id.tv_latitude);
        tv_lng =findViewById(R.id.tv_longitude);
        scrollView = findViewById(R.id.item_scrollvw);
        iv = findViewById(R.id.iv_prof_pic);
        btn_pic_gallery = findViewById(R.id.btn_pic_gallery);
        btn_pic_camera = findViewById(R.id.btn_pic_camera);
        btn_rotate = findViewById(R.id.btn_rotate);

        ed_name.setText(MainActivity.mserviceProvider.getFullName());
        ed_phone.setText(MainActivity.mserviceProvider.getPhone());
        tv_lat.setText(String.valueOf(MainActivity.mserviceProvider.getLatititude()));
        tv_lng.setText(String.valueOf(MainActivity.mserviceProvider.getLongitude()));
        ed_bio.setText("Bio:\n"+MainActivity.mserviceProvider.getBio());
        ed_password.setText(MainActivity.mserviceProvider.getPassword());
        ed_visitcost.setText(String.valueOf(MainActivity.mserviceProvider.getVisitCost()));
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mStorageRef = FirebaseStorage.getInstance().getReference();


        lv = findViewById(R.id.list_categories);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        Glide.with(EditSPProfileActivity.this).load(Uri.parse(MainActivity.mserviceProvider.getProfilePicUrl())).into(iv);

        getAllCategories();
        getAvailabilityTime();


        btn_pic_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                //
                startActivityForResult(i,SELECT_FROM_GALLERY);
            }
        });

        btn_pic_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        ActivityCompat.requestPermissions(EditSPProfileActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 110);

                    }
                    else if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED){
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent,CAPTURE_FROM_CAMERA);
                    }
                }
                else{
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent,CAPTURE_FROM_CAMERA);
                }

            }
        });

        btn_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(capturedImageUri==null){

                }
                else {
                    iv.setRotation(iv.getRotation() + 90);
                }
            }
        });

        btnChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if(cm.getActiveNetworkInfo()== null) {
                    Toast.makeText(EditSPProfileActivity.this,"Connect to internet please",Toast.LENGTH_LONG).show();

                }

                else if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){

                    buildAlertMessageNoGPS();
                }

                else{
                    Intent intent = new Intent(EditSPProfileActivity.this,MapsActivitySPEditProfile.class);
                    intent.putExtra("lat",Double.parseDouble(tv_lat.getText().toString()));
                    intent.putExtra("lng",Double.parseDouble(tv_lng.getText().toString()));
                    startActivityForResult(intent,REQ_Code);


                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if(cm.getActiveNetworkInfo()== null) {
                    Toast.makeText(EditSPProfileActivity.this,"Connect to internet please",Toast.LENGTH_LONG).show();

                }

                else  if(ed_name.getText().toString().isEmpty()||ed_phone.getText().toString().isEmpty()||ed_bio.getText().toString().isEmpty()
              ||ed_password.getText().toString().isEmpty()||start_hours.getText().toString().isEmpty()
              ||start_mins.getText().toString().isEmpty()||end_hours.getText().toString().isEmpty()||end_mins.getText().toString().isEmpty()
              ){
                  Toast.makeText(EditSPProfileActivity.this,"Error: All fields must be filled",Toast.LENGTH_LONG).show();
              }
              else if(ed_phone.getText().toString().length()>10||ed_phone.getText().toString().length()<10){
                  ed_phone.setError("Phone must have 10 digits");
                  ed_phone.requestFocus();
              }
              else if(ed_bio.getText().toString().length()>500||ed_bio.getText().toString().length()<50){
                  ed_bio.setError("Min: 50 chars   Max: 500 chars");
                  ed_bio.requestFocus();
              }
              else if(ed_password.getText().toString().length()<9){
                  ed_password.setError("Minimum length: 9");
                  ed_password.requestFocus();
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

              else{
                    int chkId = radioGroup.getCheckedRadioButtonId();
                    View radioButton=null;
                    radioButton = radioGroup.findViewById(chkId);
                    int idx = radioGroup.indexOfChild(radioButton);
                    RadioButton r = (RadioButton) radioGroup.getChildAt(idx);
                    final String selectedtext = r.getText().toString();
                    final String selectedtext2=r.getText().toString(); // this is for inner class i-e in case of capturedImageUri !=n ull


                    if(capturedImageUri==null){
                        sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"fullName").setValue(ed_name.getText().toString());
                        sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"phone").setValue(ed_phone.getText().toString());
                        sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"latititude").setValue(Double.parseDouble(tv_lat.getText().toString()));
                        sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"longitude").setValue(Double.parseDouble(tv_lng.getText().toString()));
                        sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"bio").setValue(ed_bio.getText().toString());
                        sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"password").setValue(ed_password.getText().toString());
                        sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"availaibilityTime").setValue(start_hours.getText().toString()+" "+start_mins.getText().toString()+"     "+end_hours.getText().toString()+" "+end_mins.getText().toString());
                        sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"responseTime").setValue(selectedtext);
                        sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"category").setValue(selectedCategory);
                        sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"visitCost").setValue(Double.parseDouble(ed_visitcost.getText().toString()));

                        sp_ref.child(MainActivity.mserviceProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                MainActivity.mserviceProvider = dataSnapshot.getValue(ServiceProvider.class);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });




                        Toast.makeText(EditSPProfileActivity.this,"Profile updated successfully",Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                    else{ // i-e                        capturedImageUri!=null

                        final StorageReference storageReference = mStorageRef.child("ServiceProviders" + "/" + MainActivity.mserviceProvider.getFullName() + ".jpg");
                        final String[] mDurl = {""};

                        UploadTask utask = (UploadTask) storageReference.putFile(Uri.parse(capturedImageUri.toString()));

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
                                    // here write your business logic
                                    sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"fullName").setValue(ed_name.getText().toString());
                                    sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"phone").setValue(ed_phone.getText().toString());
                                    sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"latititude").setValue(Double.parseDouble(tv_lat.getText().toString()));
                                    sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"longitude").setValue(Double.parseDouble(tv_lng.getText().toString()));
                                    sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"bio").setValue(ed_bio.getText().toString());
                                    sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"password").setValue(ed_password.getText().toString());
                                    sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"availaibilityTime").setValue(start_hours.getText().toString()+" "+start_mins.getText().toString()+"     "+end_hours.getText().toString()+" "+end_mins.getText().toString());
                                    sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"responseTime").setValue(selectedtext2);
                                    sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"category").setValue(selectedCategory);
                                    sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"profilePicUrl").setValue(mDurl[0]);
                                    sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"visitCost").setValue(Double.parseDouble(ed_visitcost.getText().toString()));


                                    sp_ref.child(MainActivity.mserviceProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            MainActivity.mserviceProvider = dataSnapshot.getValue(ServiceProvider.class);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });




                                    Toast.makeText(EditSPProfileActivity.this,"Profile updated successfully",Toast.LENGTH_LONG).show();
                                    onBackPressed();




                                }// end if

                                else{// i-e if task is not successfull

                                    // mprogressdialog.dismiss();
                                    Toast.makeText(EditSPProfileActivity.this,"Some Error occured:\n It may be due to network or internet"
                                            + "speed.",Toast.LENGTH_LONG).show();


                                }// end else



                            }// end onComplete

                        });// end addOnCompleteListener





                        //  sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"profilePicUrl").setValue(capturedImageUri.toString());
                    }// end outer  else i-e capturedImageUri!=null






              }

            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView v = (CheckedTextView) view;
                selectedCategory = lv.getItemAtPosition(position).toString();



            }
        });


        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ed_bio.getParent().requestDisallowInterceptTouchEvent(false);

                return false;
            }
        });

        ed_bio.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ed_bio.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });

     lv.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            // scrollView.getParent().requestDisallowInterceptTouchEvent(false);
             lv.getParent().requestDisallowInterceptTouchEvent(true);
             return false;
         }
     });








    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return al_cat.size();
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
            convertView = getLayoutInflater().inflate(R.layout.category_list_row,null);
            TextView cat_name = convertView.findViewById(R.id.txt_category);
            CheckBox chk = convertView.findViewById(R.id.check_box);
            //   cat_name.setText(al_cat.get(position).getCategory());

            return convertView;
        }
    }

    private void getAllCategories(){
        cat_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ssn : dataSnapshot.getChildren()) {
                    Category category =ssn.getValue(Category.class);
                    al_cat.add(category.getCategory());

                }
               /* final CustomAdapter customAdapter = new CustomAdapter();
                lv.setAdapter(customAdapter);*/

                arrayAdapter = new ArrayAdapter<String>(EditSPProfileActivity.this,android.R.layout.simple_list_item_checked , al_cat);
                lv.setAdapter(arrayAdapter);
                for (int i = 0; i < lv.getChildCount(); i++) {
                    ((TextView)lv.getChildAt(i)).setTextColor(getResources().getColor(R.color.colorBlack));

                }

                for(int i = 0;i<al_cat.size();i++){
                    if(al_cat.get(i).equals(MainActivity.mserviceProvider.getCategory())){
                        lv.setItemChecked(i,true);
                        selectedCategory = al_cat.get(i);

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }



        });

    }

    public void getAvailabilityTime(){
        String tokens[] = MainActivity.mserviceProvider.getAvailaibilityTime().split("     ");
        String startTime = tokens[0];
        String endTime = tokens[1];

        String startTimeTokens[] = startTime.split(" ");
        String endTimeTokens[] = endTime.split(" ");
        start_hours.setText(startTimeTokens[0]);
        start_mins.setText(startTimeTokens[1]);
        end_hours.setText(endTimeTokens[0]);
        end_mins.setText(endTimeTokens[1]);


    }

    protected void buildAlertMessageNoGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please turn on your location")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_Code) {
            if (resultCode == RESULT_OK) {
                tv_lat.setText(String.valueOf(data.getDoubleExtra("lat",MainActivity.mserviceProvider.getLatititude())));
              tv_lng.setText(String.valueOf(data.getDoubleExtra("lng",MainActivity.mserviceProvider.getLongitude())));

            }
        }

        if (requestCode == SELECT_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {

                capturedImageUri = data.getData();
                if (capturedImageUri != null) {


                    Glide.with(this).load(capturedImageUri).into(iv);

                }


            }
        }

        else if(requestCode == CAPTURE_FROM_CAMERA){
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                iv.setImageBitmap(photo);
                // METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getApplicationContext(), photo);



                capturedImageUri = tempUri;
                //    Toast.makeText(ServiceProviderSignUpPart1Activity.this,capturedImageUri.toString(),Toast.LENGTH_LONG).show();
                //   Glide.with(this).load(capturedImageUri).into(iv);

            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
