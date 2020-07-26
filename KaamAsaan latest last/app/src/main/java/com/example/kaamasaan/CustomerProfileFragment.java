package com.example.kaamasaan;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView txtName,txtJobs,txtReviews;;
    ImageButton imageButton;
    Typeface mfont;
    CircleImageView imv;
    ArrayList<WorkRating> alWorkRating;
    ListView lv_reviews;
    RatingBar ratingBar1,ratingBar2,ratingBar3,ratingBar4,ratingBar5;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerProfileFragment newInstance(String param1, String param2) {
        CustomerProfileFragment fragment = new CustomerProfileFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_profile, container, false);
        txtName = view.findViewById(R.id.txt_name);
        imageButton = view.findViewById(R.id.btn_edit_name);
        imv = view.findViewById(R.id.iv_prof_pic);
        txtName.setText(MainActivity.mcustomer.getFullName());
        txtName.setTypeface(mfont);
        ratingBar1 = view.findViewById(R.id.ratingbar1);
        ratingBar2 = view.findViewById(R.id.ratingbar2);
        ratingBar3 = view.findViewById(R.id.ratingbar3);
        ratingBar4 = view.findViewById(R.id.ratingbar4);
        ratingBar5 = view.findViewById(R.id.ratingbar5); txtReviews = view.findViewById(R.id.txt_reviews);
        txtJobs = view.findViewById(R.id.txt_jobs);
        lv_reviews = view.findViewById(R.id.lv_reviews);

        getLatestData();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),EditCustomerProfileActivity.class));
            }
        });


        return view;
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return alWorkRating.size();
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
            convertView = getActivity().getLayoutInflater().inflate(R.layout.review_list_row,null);
            CircleImageView iv = convertView.findViewById(R.id.iv);
            TextView txt_name = convertView.findViewById(R.id.txt_username);
            final TextView txt_review = convertView.findViewById(R.id.txt_review);
            RatingBar ratingBar =convertView.findViewById(R.id.ratingbar);




            txt_name.setText(alWorkRating.get(position).getUserNameOfUser());
            txt_review.setText(alWorkRating.get(position).getReview());
            ratingBar.setRating(alWorkRating.get(position).getRating());
            Glide.with(getActivity()).load(alWorkRating.get(position).getImageUrlOfUser()).into(iv);






            return convertView;
        }
    }
    public void getLatestData(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users").
                child("Customers");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                   Customer customer = ssn.getValue(Customer.class);
                    if( customer.getId().equals(MainActivity.mcustomer.getId())){
                        int combinedRating = (customer.getResponseRating()+customer.getAverageWorkRating())/2;

                        if( combinedRating ==1){
                            ratingBar1.setRating( combinedRating );
                        }
                        else if (combinedRating ==2){
                            ratingBar2.setRating( combinedRating );
                        }
                        else if(combinedRating ==3){
                            ratingBar3.setRating( combinedRating );
                        }
                        else if( combinedRating ==4){
                            ratingBar4.setRating( combinedRating );
                        }
                        else if( combinedRating ==5){
                            ratingBar5.setRating( combinedRating );
                        }

                        alWorkRating =  customer.getWorkRatingList();
                        if(alWorkRating==null){
                            // do nothing
                        }
                        else{
                           CustomAdapter customAdapter = new  CustomAdapter();
                            lv_reviews.setAdapter(customAdapter);
                        }
                        if( customer.getCompletedWorkList()==null){
                            // do nothing
                        }
                        else{
                            txtJobs.setText("Jobs: "+ customer.getCompletedWorkList().size());
                        }
                        if( customer.getWorkRatingList()==null){
                            // do nothing
                        }
                        else{
                            txtReviews.setText("Reviews: "+ customer.getWorkRatingList().size());
                        }

                        Glide.with(getActivity()).load(Uri.parse( customer.getImageUrl())).into(imv);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void showReviewDetailsDialog(int position){
        View view =  getActivity().getLayoutInflater().inflate(R.layout.review_detail_dialog,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Holo_Dialog_NoActionBar);
        final AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setView(view,0,0,0,0);

        AppCompatImageView iv =  view.findViewById(R.id.iv);
        TextView txt_name=  view.findViewById(R.id.txt_username);
        final RatingBar ratingBar = view.findViewById(R.id.ratingbar);
        TextView txt_review=  view.findViewById(R.id.txt_review);
        ImageButton button_close =  view.findViewById(R.id.btn_close);

        txt_name.setText(alWorkRating.get(position).getUserNameOfUser());
        txt_review.setText(alWorkRating.get(position).getReview());
        ratingBar.setRating(alWorkRating.get(position).getRating());
        Glide.with(getActivity()).load(alWorkRating.get(position).getImageUrlOfUser()).into(iv);

        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });



        alertDialog.show();


    }

}
