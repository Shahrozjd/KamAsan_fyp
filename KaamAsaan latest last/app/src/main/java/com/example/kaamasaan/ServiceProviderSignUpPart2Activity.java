package com.example.kaamasaan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class ServiceProviderSignUpPart2Activity extends AppCompatActivity {

    Button btnNext,btnBack;
    TextView txt_title,txt_radius_value;
    Typeface mfont;
    SeekBar seekBar;

    EditText editOperationalRadius;
    FirebaseDatabase database;
    DatabaseReference cat_ref;
    ArrayList<String> al_cat =new ArrayList();
    ListView lv;
    ArrayAdapter<String> arrayAdapter;
    String selectedCategory = "";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_sign_up_part2);
        mfont = Typeface.createFromAsset(this.getAssets(), "fonts/KaushanScript-Regular.otf");
        txt_title = findViewById(R.id.tv_title_signup2);
        txt_radius_value = findViewById(R.id.radius_value);
        seekBar = findViewById(R.id.seekBar);
        txt_title.setTypeface(mfont);
        txt_radius_value.setText("2");
        lv = findViewById(R.id.list_categories);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        database = FirebaseDatabase.getInstance();
        cat_ref = database.getReference("Categories");

        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

          getAllCategories();
          lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView v = (CheckedTextView) view;
                selectedCategory = lv.getItemAtPosition(position).toString();



            }
        });




      //  editOperationalRadius = findViewById(R.id.ed_radius);
        btnNext = findViewById(R.id.btn_Next);
        btnBack = findViewById(R.id.btn_back);





        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (selectedCategory.isEmpty()) {
                    Toast.makeText(ServiceProviderSignUpPart2Activity.this,"Category must be selected",Toast.LENGTH_LONG).show();
                }else {


                    //   Toast.makeText(ServiceProviderSignUpPart2Activity.this,"selected text : "+selectedtext, Toast.LENGTH_LONG).show();

                        SharedPreferences.Editor editor =ServiceProviderSignUpPart1Activity. sharedpreferences.edit();
                        editor.putString(ServiceProviderSignUpPart1Activity.categorykey,selectedCategory);
                        editor.putInt(ServiceProviderSignUpPart1Activity.radiuskey,Integer.parseInt(txt_radius_value.getText().toString()));
                        editor.commit();
                        startActivity(new Intent(ServiceProviderSignUpPart2Activity.this, ServiceProviderSignUpPart3Activity.class));
                       // finish();
                }


            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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

                arrayAdapter = new ArrayAdapter<String>(ServiceProviderSignUpPart2Activity.this,android.R.layout.simple_list_item_checked , al_cat);
                lv.setAdapter(arrayAdapter);
                for (int i = 0; i < lv.getChildCount(); i++) {
                    ((TextView)lv.getChildAt(i)).setTextColor(getResources().getColor(R.color.colorBlack));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }



        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            int MIN = 1;
            if (progress < MIN) {

                txt_radius_value.setText("1");
            }
            else {
                txt_radius_value.setText(String.valueOf(progress));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };
}

