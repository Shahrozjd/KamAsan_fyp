package com.example.kaamasaan;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceProviderNotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceProviderNotificationsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ArrayList<VisitRequest>alsentVisitRequests,alPendingVisits,alCompletedVisits;
    ArrayList<WorkRequest> alSentWorkRequests,alPendingWorks,alCompletedWorks;


    ListView lv_sent_visit_requests,lv_pending_visits,lv_completed_visits,lv_sent_work_requests,
    lv_pending_works,lv_completed_works;

    ScrollView sv;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServiceProviderNotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceProviderNotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceProviderNotificationsFragment newInstance(String param1, String param2) {
        ServiceProviderNotificationsFragment fragment = new ServiceProviderNotificationsFragment();
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
        View view =  inflater.inflate(R.layout.fragment_service_provider_notifications, container, false);
        lv_sent_visit_requests = view.findViewById(R.id.lv_sent_visit_requests);
        lv_pending_visits = view.findViewById(R.id.lv_pending_visits);
        lv_completed_visits = view.findViewById(R.id.lv_completed_visits);
        lv_sent_work_requests = view.findViewById(R.id.lv_sent_work_requests);
        lv_pending_works = view.findViewById(R.id.lv_pending_works);
        lv_completed_works = view.findViewById(R.id.lv_completed_works);

        alsentVisitRequests = new ArrayList<>();
        alPendingVisits = new ArrayList<>();
        alSentWorkRequests = new ArrayList<>();
        alPendingWorks = new ArrayList<>();

        sv  = view.findViewById(R.id.item_scrollvw);
        lv_sent_visit_requests.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent
                    event) {
                /* scrollView.getParent
                ().requestDisallowInterceptTouchEvent(false);*/
                if(lv_sent_visit_requests.getChildCount()==0){
                    // do nothing
                }
                else {
                    lv_sent_visit_requests.getParent().requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
        lv_pending_visits.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent
                    event) {
                /* scrollView.getParent
                ().requestDisallowInterceptTouchEvent(false);*/
                if(lv_pending_visits.getChildCount()==0){
                    // do nothing
                }
                else {
                    lv_pending_visits.getParent().requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        lv_completed_visits.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent
                    event) {
                /* scrollView.getParent
                ().requestDisallowInterceptTouchEvent(false);*/
                if(lv_completed_visits.getChildCount()==0){
                    // do nothing
                }
                else {
                    lv_completed_visits.getParent().requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        lv_sent_work_requests.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent
                    event) {
                /* scrollView.getParent
                ().requestDisallowInterceptTouchEvent(false);*/
                if( lv_sent_work_requests.getChildCount()==0){
                    // do nothing
                }
                else {
                    lv_sent_work_requests.getParent().requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
        lv_pending_works.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent
                    event) {
                /* scrollView.getParent
                ().requestDisallowInterceptTouchEvent(false);*/
                if( lv_pending_works.getChildCount()==0){
                    // do nothing
                }
                else {
                    lv_pending_works.getParent().requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        lv_completed_works.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent
                    event) {
                /* scrollView.getParent
                ().requestDisallowInterceptTouchEvent(false);*/
                if( lv_completed_works.getChildCount()==0){
                    // do nothing
                }
                else {
                    lv_completed_works.getParent().requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });



        sv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent
                    event) {



                return false;
            }
        });


        getLatestData();

        return view;
    }

    class SentVisitRequestsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return alsentVisitRequests .size();
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
            convertView = getActivity().getLayoutInflater().inflate(R.layout.sent_visit_request_list_row,null);
            TextView txt_sent_visit_request_Id = convertView.findViewById(R.id.txt_sent_visit_request_Id);
            TextView txt_to = convertView.findViewById(R.id.txt_to);
            TextView txt_potential_time = convertView.findViewById(R.id.txt_potential_time);
            TextView txt_duration = convertView.findViewById(R.id.txt_duration);
            TextView txt_visit_cost = convertView.findViewById(R.id.txt_visit_cost);

            txt_sent_visit_request_Id.setText(alsentVisitRequests.get(position).getRequestId());
            txt_to.setText(alsentVisitRequests.get(position).getUserName());
            txt_potential_time.setText(alsentVisitRequests.get(position).getPotentialTimeAndDate());
            txt_duration.setText(String.valueOf(alsentVisitRequests.get(position).getDuration()));
            txt_visit_cost.setText(String.valueOf(alsentVisitRequests.get(position).getVisitCost()));

            return convertView;
        }
    }

    class PendingVisitsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return alPendingVisits .size();
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
            convertView = getActivity().getLayoutInflater().inflate(R.layout.sent_visit_request_list_row,null);
            TextView txt_sent_visit_request_Id = convertView.findViewById(R.id.txt_sent_visit_request_Id);
            TextView txt_to = convertView.findViewById(R.id.txt_to);
            TextView txt_potential_time = convertView.findViewById(R.id.txt_potential_time);
            TextView txt_duration = convertView.findViewById(R.id.txt_duration);
            TextView txt_visit_cost = convertView.findViewById(R.id.txt_visit_cost);

            txt_sent_visit_request_Id.setText(alPendingVisits.get(position).getRequestId());
            txt_to.setText(alPendingVisits.get(position).getUserName());
            txt_potential_time.setText(alPendingVisits.get(position).getPotentialTimeAndDate());
            txt_duration.setText(String.valueOf(alPendingVisits.get(position).getDuration()));
            txt_visit_cost.setText(String.valueOf(alPendingVisits.get(position).getVisitCost()));

            return convertView;
        }
    }

    class CompletedVisitsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return alCompletedVisits .size();
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
            convertView = getActivity().getLayoutInflater().inflate(R.layout.completed_visit_list_row_service_provider_side,null);
            final TextView txt_sent_visit_request_Id = convertView.findViewById(R.id.txt_completed_visit_request_Id);
            final TextView txt_customer_name = convertView.findViewById(R.id.txt_from);
            TextView txt_potential_time = convertView.findViewById(R.id.txt_potential_time);
            TextView txt_duration = convertView.findViewById(R.id.txt_duration);
            TextView txt_visit_cost = convertView.findViewById(R.id.txt_visit_cost);
            Button btn_send_work_request =  convertView.findViewById(R.id.btn_send_work_request);
            Button btn_give_rating = convertView.findViewById(R.id.btn_give_rating);
            Button btn_delete_completed_visit = convertView.findViewById(R.id.btn_delete_completed_visit);
            txt_sent_visit_request_Id.setText( alCompletedVisits.get(position).getRequestId());
            txt_customer_name.setText( alCompletedVisits.get(position).getUserName());
            txt_potential_time.setText( alCompletedVisits.get(position).getPotentialTimeAndDate());
            txt_duration.setText(String.valueOf( alCompletedVisits.get(position).getDuration()));
            txt_visit_cost.setText(String.valueOf( alCompletedVisits.get(position).getVisitCost()));


            btn_send_work_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 Intent intent = new Intent(getActivity(),SendWorkRequestActivity.class);
                 intent.putExtra("customerId",alCompletedVisits.get(position).getUserId());
                    intent.putExtra("customerName",txt_customer_name.getText().toString());
                 intent.putExtra("requestId",txt_sent_visit_request_Id.getText().toString());
                 startActivity(intent);
                }
            });

            btn_give_rating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRatingDialog(txt_customer_name.getText().toString());

                }
            });
            btn_delete_completed_visit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alCompletedVisits.remove(position);
                    CompletedVisitsAdapter completedVisitsAdapter = new CompletedVisitsAdapter();
                    lv_completed_visits.setAdapter(completedVisitsAdapter);
                    DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
                    sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"completedVisitsList").setValue(alCompletedVisits);
                }
            });



            return convertView;
        }
    }

    public void getLatestData(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child("ServiceProviders");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn: dataSnapshot.getChildren()){
                    ServiceProvider serviceProvider = ssn.getValue(ServiceProvider.class);
                    if(serviceProvider.getId().equals(MainActivity.mserviceProvider.getId())) {

                        if (serviceProvider.getSentVisitRequestList() == null) {
                            // do nothing
                            if(alsentVisitRequests.size()==1){
                                alsentVisitRequests.remove(0);
                                SentVisitRequestsAdapter sentVisitRequestsAdapter = new SentVisitRequestsAdapter();
                                lv_sent_visit_requests.setAdapter(sentVisitRequestsAdapter);
                            }


                        }
                        else {
                            alsentVisitRequests = serviceProvider.getSentVisitRequestList();
                            SentVisitRequestsAdapter sentVisitRequestsAdapter = new SentVisitRequestsAdapter();
                            lv_sent_visit_requests.setAdapter(sentVisitRequestsAdapter);
                        }


                        if (serviceProvider.getPendingVisitsList() == null) {
                            // do nothing
                            if(alPendingVisits.size()==1){
                                alPendingVisits.remove(0);
                                PendingVisitsAdapter pendingVisitsAdapter = new PendingVisitsAdapter();
                                lv_pending_visits.setAdapter(pendingVisitsAdapter);
                            }
                        } else {
                            //   Toast.makeText(getActivity(),alPendingVisits.size()+"",Toast.LENGTH_LONG).show();
                            alPendingVisits = serviceProvider.getPendingVisitsList();
                            PendingVisitsAdapter pendingVisitsAdapter = new PendingVisitsAdapter();
                            lv_pending_visits.setAdapter(pendingVisitsAdapter);
                        }

                        if (serviceProvider.getCompletedVisitsList() == null) {
                            // do nothing
                        } else {
                            alCompletedVisits = serviceProvider.getCompletedVisitsList();
                            CompletedVisitsAdapter completedVisitsAdapter = new CompletedVisitsAdapter();
                            lv_completed_visits.setAdapter(completedVisitsAdapter);
                        }
                        if (serviceProvider.getSentWorkRequestList() == null) {
                            // do nothing

                            if(alSentWorkRequests.size()==1){
                                alSentWorkRequests.remove(0);
                                SentWorkRequestsAdapter sentWorkRequestsAdapter = new SentWorkRequestsAdapter();
                                lv_sent_work_requests.setAdapter(sentWorkRequestsAdapter);
                            }
                        } else {
                            alSentWorkRequests = serviceProvider.getSentWorkRequestList();
                            SentWorkRequestsAdapter sentWorkRequestsAdapter = new SentWorkRequestsAdapter();
                            lv_sent_work_requests.setAdapter(sentWorkRequestsAdapter);
                        }


                        if (serviceProvider.getPendingWorkList() == null) {
                            // do nothing
                            if(alPendingWorks.size()==1){
                                alPendingWorks.remove(0);
                                PendingWorkRequestsListAdapter pendingWorkRequestsListAdapter = new PendingWorkRequestsListAdapter();
                                lv_pending_works.setAdapter(pendingWorkRequestsListAdapter);
                            }

                        } else {
                            alPendingWorks = serviceProvider.getPendingWorkList();
                            PendingWorkRequestsListAdapter pendingWorkRequestsListAdapter = new PendingWorkRequestsListAdapter();
                            lv_pending_works.setAdapter(pendingWorkRequestsListAdapter);
                        }

                        if (serviceProvider.getCompletedWorkList() == null) {
                            // do nothing

                        } else {
                            alCompletedWorks = serviceProvider.getCompletedWorkList();
                            CompletedWorkRequestsListAdapter completedWorkRequestsListAdapter = new CompletedWorkRequestsListAdapter();
                            lv_completed_works.setAdapter(completedWorkRequestsListAdapter);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void manuallyGiveRatingToCustomer(final String customerUserName, final int rating){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
        final String[] CustomerId = new String[1];
        final String[] CustomerImageUrl = new String[1];
        final Customer[] customer = new Customer[1];
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn:dataSnapshot.getChildren()){
                    customer[0] = ssn.getValue( Customer.class);
                    if( customer[0].getUserName().equals(customerUserName)){
                        CustomerId[0] =  customer[0].getId();
                        CustomerImageUrl[0] =  customer[0].getImageUrl();
                        ResponseTimeRating responseTimeRating = new ResponseTimeRating(MainActivity.mserviceProvider.getProfilePicUrl(),
                                MainActivity.mserviceProvider.getUserName(),rating);
                        ArrayList<ResponseTimeRating> responseTimeRatingArrayListOfCustomer = customer[0].getResponseTimeRatingList();
                        if(responseTimeRatingArrayListOfCustomer==null){
                            responseTimeRatingArrayListOfCustomer = new ArrayList<>();
                            responseTimeRatingArrayListOfCustomer.add(responseTimeRating);
                            customer[0].setResponseTimeRatingList(responseTimeRatingArrayListOfCustomer);
                            int averageResponseTimeRatingOfCustomer = 0;
                            int sum = 0;
                            for (int i = 0; i < responseTimeRatingArrayListOfCustomer.size(); i++) {
                                sum = sum + responseTimeRatingArrayListOfCustomer.get(i).getResponseRating();
                            }
                            averageResponseTimeRatingOfCustomer= sum / responseTimeRatingArrayListOfCustomer.size();
                            customer[0].setResponseRating(averageResponseTimeRatingOfCustomer );

                            break;
                        }
                        else{
                            responseTimeRatingArrayListOfCustomer.add(responseTimeRating);
                            customer[0].setResponseTimeRatingList(responseTimeRatingArrayListOfCustomer);
                            int averageResponseTimeRatingOfCustomer = 0;
                            int sum = 0;
                            for (int i = 0; i < responseTimeRatingArrayListOfCustomer.size(); i++) {
                                sum = sum + responseTimeRatingArrayListOfCustomer.get(i).getResponseRating();
                            }
                            averageResponseTimeRatingOfCustomer  = sum / responseTimeRatingArrayListOfCustomer.size();
                            customer[0].setResponseRating(averageResponseTimeRatingOfCustomer );
                            break;
                        }
                    }
                }
                DatabaseReference newDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(CustomerId[0]);
                newDatabaseReference.setValue(customer[0]);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void showRatingDialog(final String CustomerUserName){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.response_rating_dialog);
        final RatingBar ratingBar = dialog.findViewById(R.id.response_ratingbar);
        Button button_ok = dialog.findViewById(R.id.response_rating_ok_button);
        dialog.setTitle("Response Rating to " + CustomerUserName);
        dialog.setCancelable(false);

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ratingBar.getRating()==0){
                   Toast.makeText(getActivity(),"Error: Rating must be given",Toast.LENGTH_LONG).show();
                }
                else{
                    manuallyGiveRatingToCustomer(CustomerUserName,(int)ratingBar.getRating());
                    dialog.dismiss();
                }





            }
        });

        dialog.show();
    }

    class SentWorkRequestsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return alSentWorkRequests .size();
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
            convertView = getActivity().getLayoutInflater().inflate(R.layout.sent_work_request_list_row,null);
            TextView txt_received_work_request_Id = convertView.findViewById(R.id.txt_received_visit_request_Id);
            TextView txt_from = convertView.findViewById(R.id.txt_from);
            TextView txt_start_date = convertView.findViewById(R.id.txt_startDate);
            TextView txt_end_date = convertView.findViewById(R.id.txt_endDate);
            TextView txt_mode = convertView.findViewById(R.id.txt_mode);
            TextView txt_estimated_cost = convertView.findViewById(R.id.txt_estimated_cost);

            txt_received_work_request_Id.setText(alSentWorkRequests.get(position).getRequestId());
            txt_from.setText(alSentWorkRequests.get(position).getUserName());
            txt_start_date.setText(alSentWorkRequests.get(position).getStartDate());
            txt_end_date.setText(alSentWorkRequests.get(position).getEndDate());
            txt_mode .setText(alSentWorkRequests.get(position).getMode());
            txt_estimated_cost.setText(String.valueOf(alSentWorkRequests.get(position).getEstimatedCost()));

            return convertView;
        }
    }

    class PendingWorkRequestsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return alPendingWorks.size();
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
            convertView = getActivity().getLayoutInflater().inflate(R.layout.serviceprovider_pending_work_list_row,null);
            TextView txt_received_work_request_Id = convertView.findViewById(R.id.txt_received_work_request_Id);
            TextView txt_from = convertView.findViewById(R.id.txt_from);
            TextView txt_start_date = convertView.findViewById(R.id.txt_startDate);
            TextView txt_end_date = convertView.findViewById(R.id.txt_endDate);
            TextView txt_mode = convertView.findViewById(R.id.txt_mode);
            TextView txt_estimated_cost = convertView.findViewById(R.id.txt_estimated_cost);



            txt_received_work_request_Id.setText(alPendingWorks.get(position).getRequestId());
            txt_from.setText(alPendingWorks.get(position).getUserName());
            txt_start_date.setText(alPendingWorks.get(position).getStartDate());
            txt_end_date.setText(alPendingWorks.get(position).getEndDate());
            txt_mode .setText(alPendingWorks.get(position).getMode());
            txt_estimated_cost.setText(String.valueOf(alPendingWorks.get(position).getEstimatedCost()));





            return convertView;
        }
    }

    class CompletedWorkRequestsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return alCompletedWorks.size();
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
            convertView = getActivity().getLayoutInflater().inflate(R.layout.serviceprovider_completed_work_list_row,null);
            TextView txt_received_work_request_Id = convertView.findViewById(R.id.txt_received_visit_request_Id);
            final TextView txt_from = convertView.findViewById(R.id.txt_from);
            TextView txt_start_date = convertView.findViewById(R.id.txt_startDate);
            TextView txt_end_date = convertView.findViewById(R.id.txt_endDate);
            TextView txt_mode = convertView.findViewById(R.id.txt_mode);
            TextView txt_estimated_cost = convertView.findViewById(R.id.txt_estimated_cost);
            Button btn_give_review =  convertView.findViewById(R.id.btn_give_review);
            Button btn_delete_work= convertView.findViewById(R.id. btn_delete_work);


            txt_received_work_request_Id.setText(alCompletedWorks.get(position).getRequestId());
            txt_from.setText(alCompletedWorks.get(position).getUserName());
            txt_start_date.setText(alCompletedWorks.get(position).getStartDate());
            txt_end_date.setText(alCompletedWorks.get(position).getEndDate());
            txt_mode .setText(alCompletedWorks.get(position).getMode());
            txt_estimated_cost.setText(String.valueOf(alCompletedWorks.get(position).getEstimatedCost()));

            final WorkRequest workRequest = new WorkRequest(txt_received_work_request_Id.getText().toString(),txt_from.getText().toString(),
                    "Completed",txt_start_date.getText().toString(),txt_end_date.getText().toString(),
                    txt_mode.getText().toString(),Integer.parseInt(txt_estimated_cost.getText().toString()));
            btn_give_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showWorkRatingDialog(txt_from.getText().toString());
                }
            });

            btn_delete_work.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alCompletedWorks.remove(position);
                    CompletedWorkRequestsListAdapter completedWorkRequestsListAdapter = new CompletedWorkRequestsListAdapter();
                    lv_completed_works.setAdapter(completedWorkRequestsListAdapter);
                    DatabaseReference sp_ref = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders");
                    sp_ref.child(MainActivity.mserviceProvider.getId()+"/"+"completedWorkList").setValue(alCompletedWorks);
                }
            });
            return convertView;
        }
    }

    public void giveWorkRatingToCustomer(final String customerUserName, final WorkRating workRating){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Customers");
       // Toast.makeText(getActivity(),"inside rating function",Toast.LENGTH_LONG).show();
        final String[] CustomerId = new String[1];

        final Customer[]customer = new Customer[1];
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ssn:dataSnapshot.getChildren()) {
                    customer[0] = ssn.getValue(Customer.class);
                    if(customer[0].getUserName().equals(customerUserName)) {
                        CustomerId[0] = customer[0].getId();

                        ArrayList<WorkRating>alWorkRating = customer[0].getWorkRatingList();
                        if(alWorkRating==null){
                            alWorkRating = new ArrayList<>();
                            alWorkRating.add(workRating);
                            customer[0].setWorkRatingList(alWorkRating);
                            int averageWorkRating = 0;
                            int sum = 0;
                            for(int i=0;i<alWorkRating.size();i++){
                                sum =sum+alWorkRating.get(i).getRating();
                            }
                            averageWorkRating = sum/alWorkRating.size();
                            customer[0].setAverageWorkRating(averageWorkRating);
                            break;
                        }
                        else{
                            alWorkRating.add(workRating);
                            customer[0].setWorkRatingList(alWorkRating);
                            int averageWorkRating = 0;
                            int sum = 0;
                            for(int i=0;i<alWorkRating.size();i++){
                                sum =sum+alWorkRating.get(i).getRating();
                            }
                            averageWorkRating = sum/alWorkRating.size();
                            customer[0].setAverageWorkRating(averageWorkRating);
                            break;
                        }
                    }
                }
                DatabaseReference newDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(CustomerId[0]);
                newDatabaseReference.setValue(customer[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showWorkRatingDialog(final String customerUserName){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.work_rating_dialog);
        final RatingBar ratingBar = dialog.findViewById(R.id.work_ratingbar);
        final EditText ed_review = dialog.findViewById(R.id.ed_review);
        Button button_ok = dialog.findViewById(R.id.work_rating_ok_button);
        dialog.setTitle("Give Review to " + customerUserName);
        dialog.setCancelable(false);

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ed_review.getText().toString().isEmpty()||ratingBar.getRating()==0){
                    Toast.makeText(getActivity(),"Error: Rating and Review both must be given",Toast.LENGTH_LONG).show();
                }
                else{
                    System.out.println("myreview: "+ed_review.getText().toString());
                    WorkRating workRating = new WorkRating(MainActivity.mserviceProvider.getProfilePicUrl(),
                            MainActivity.mserviceProvider.getUserName(),ed_review.getText().toString(),
                            (int)ratingBar.getRating());
                    giveWorkRatingToCustomer(customerUserName,
                            workRating);



                    dialog.dismiss();

                }
            }
        });

        dialog.show();

    }

}
