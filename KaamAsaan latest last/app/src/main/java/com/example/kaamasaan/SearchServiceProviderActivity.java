package com.example.kaamasaan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchServiceProviderActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference cat_ref;
    ArrayList<String> al_cat =new ArrayList();
    ListView lv;
    Button btn_search;
    SearchView sv;
    CheckBox chkmobile,chkelectrician,chkwasherman;
    ArrayAdapter<String> arrayAdapter;
    public static String selectedCategory = "";
    public LocationManager lm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_service_provider);
        sv = findViewById(R.id.sv);
        lv = findViewById(R.id.list_categories);
        btn_search = findViewById(R.id.btn_search);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        database = FirebaseDatabase.getInstance();
        cat_ref = database.getReference("Categories");
        chkmobile = findViewById(R.id.chk_mobile);
        chkelectrician = findViewById(R.id.chk_electrician);
        chkwasherman  = findViewById(R.id.chk_washerMan);
        FoundSPListActivity.returnedFromListActivity=false;
        getAllCategories();

chkmobile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
           if(isChecked==true){
               for(int i=0;i<al_cat.size();i++){
                   lv.setItemChecked(i,false);
               }
               chkelectrician.setChecked(false);
               chkwasherman.setChecked(false);
               selectedCategory ="mobile repairing";




           }
           else{
                 selectedCategory = "";
           }

    }
});

chkelectrician.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked==true){
            for(int i=0;i<al_cat.size();i++){
                lv.setItemChecked(i,false);
            }
            chkmobile.setChecked(false);
            chkwasherman.setChecked(false);
            selectedCategory = chkelectrician.getText().toString();




        }
        else{
            selectedCategory = "";
        }
    }
});

chkwasherman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked==true){
            for(int i=0;i<al_cat.size();i++){
                lv.setItemChecked(i,false);
            }
            chkmobile.setChecked(false);
            chkelectrician.setChecked(false);
            selectedCategory =chkwasherman.getText().toString();




        }
        else{
            selectedCategory = "";
        }
    }
});



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView v = (CheckedTextView) view;
                chkmobile.setChecked(false);
                chkelectrician.setChecked(false);
                chkwasherman.setChecked(false);
                selectedCategory = lv.getItemAtPosition(position).toString();





            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                if(cm.getActiveNetworkInfo()== null) {
                    Toast.makeText(SearchServiceProviderActivity.this,"Error: Connect to internet please",Toast.LENGTH_LONG).show();

                }

                else if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    buildAlertMessageNoGPS();
                }
                else if(selectedCategory==""){
                    Toast.makeText(SearchServiceProviderActivity.this,"Error: Category must be selected.",Toast.LENGTH_LONG).show();
                }
                else{
                      startActivity(new Intent(SearchServiceProviderActivity.this,FoundSPMapActivity.class));
                }
            }
        });

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

              //  arrayAdapter.getFilter().filter(newText);


                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
      startActivity(new Intent(SearchServiceProviderActivity.this,CustomerHomeActivity.class));
      finish();

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


                arrayAdapter = new ArrayAdapter<String>(SearchServiceProviderActivity.this,android.R.layout.simple_list_item_checked , al_cat);
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

}
