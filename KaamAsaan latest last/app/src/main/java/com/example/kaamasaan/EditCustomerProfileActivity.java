package com.example.kaamasaan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class EditCustomerProfileActivity extends AppCompatActivity {
    EditText ed_name,ed_phone,ed_password;
    Button btn_save;
    AppCompatImageView iv;
    FirebaseDatabase database;
    DatabaseReference customer_ref;
    AppCompatImageButton btn_pic_gallery;
    AppCompatImageButton btn_pic_camera;
    AppCompatImageButton btn_rotate;
    int REQ_Code = 100;
    private  static final int SELECT_FROM_GALLERY = 1;
    private static final int  CAPTURE_FROM_CAMERA = 2;
    Uri capturedImageUri;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer_profile);
        ed_name = findViewById(R.id.ed_name);
        ed_phone = findViewById(R.id.ed_phone);
        ed_password = findViewById(R.id.ed_password);
        btn_save =findViewById(R.id.btn_save);
        iv = findViewById(R.id.iv);
        btn_pic_gallery = findViewById(R.id.btn_pic_gallery);
        btn_pic_camera = findViewById(R.id.btn_pic_camera);
        btn_rotate = findViewById(R.id.btn_rotate);

        ed_name.setText(MainActivity.mcustomer.getFullName());
        ed_phone.setText(MainActivity.mcustomer.getPhone());
        ed_password.setText(MainActivity.mcustomer.getPassword());
        Glide.with(EditCustomerProfileActivity.this).load(Uri.parse(MainActivity.mcustomer.getImageUrl())).into(iv);
        database = FirebaseDatabase.getInstance();
        customer_ref = database.getReference("Users").child("Customers");
        mStorageRef = FirebaseStorage.getInstance().getReference();

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
                        ActivityCompat.requestPermissions(EditCustomerProfileActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 110);

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

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if(cm.getActiveNetworkInfo()== null) {
                    Toast.makeText(EditCustomerProfileActivity.this,"Connect to internet please",Toast.LENGTH_LONG).show();

                }


               else if(ed_name.getText().toString().isEmpty()||ed_phone.getText().toString().isEmpty()||ed_password.getText().toString().isEmpty()){
                    Toast.makeText(EditCustomerProfileActivity.this,"Error: All fields must be filled",Toast.LENGTH_LONG).show();
                }
               else if(ed_phone.getText().toString().length()>10||ed_phone.getText().toString().length()<10){
                    ed_phone.setError("Phone must have 10 digits");
                    ed_phone.requestFocus();
                }
               else if((ed_password.getText().toString().length()<9)){
                    ed_password.setError("Minimum length: 9");
                    ed_password.requestFocus();
                }

               else{
                   if(capturedImageUri==null){
                       customer_ref.child(MainActivity.mcustomer.getId()+"/"+"fullName").setValue(ed_name.getText().toString());
                       customer_ref.child(MainActivity.mcustomer.getId()+"/"+"phone").setValue(ed_phone.getText().toString());
                       customer_ref.child(MainActivity.mcustomer.getId()+"/"+"password").setValue(ed_password.getText().toString());

                    customer_ref.child(MainActivity.mcustomer.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            MainActivity.mcustomer = dataSnapshot.getValue(Customer.class);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(EditCustomerProfileActivity.this,"updated successfully",Toast.LENGTH_LONG).show();
                    onBackPressed();
               }// end if
                   else{ // i-e capturedImageUri != null

                       final StorageReference storageReference = mStorageRef.child("Customers" + "/" + MainActivity.mcustomer.getUserName() + ".jpg");
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
                                   customer_ref.child(MainActivity.mcustomer.getId()+"/"+"fullName").setValue(ed_name.getText().toString());
                                   customer_ref.child(MainActivity.mcustomer.getId()+"/"+"phone").setValue(ed_phone.getText().toString());
                                   customer_ref.child(MainActivity.mcustomer.getId()+"/"+"password").setValue(ed_password.getText().toString());
                                   customer_ref.child(MainActivity.mcustomer.getId()+"/"+"imageUrl").setValue(mDurl[0]);
                                   customer_ref.child(MainActivity.mcustomer.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                           MainActivity.mcustomer = dataSnapshot.getValue(Customer.class);

                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError databaseError) {

                                       }
                                   });

                               }// end if

                               else{// i-e if task is not successfull

                                  // mprogressdialog.dismiss();
                                   Toast.makeText(EditCustomerProfileActivity.this,"Some Error occured:\n It may be due to network or internet                                                   speed.",Toast.LENGTH_LONG).show();


                               }// end else



                           }// end onComplete

                       });// end addOnCompleteListener

                   }// end else

                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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


            }
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
