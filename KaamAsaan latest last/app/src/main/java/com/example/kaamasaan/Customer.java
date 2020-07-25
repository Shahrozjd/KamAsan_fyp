package com.example.kaamasaan;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Customer implements Parcelable {
    String id,fullName, userName,phone, password,imageUrl;
    ArrayList<String> BroadCastRequestsIdsList;
    int responseRating,  averageWorkRating;
    HashMap<String, ArrayList<Message>>chatHashmap;
    ArrayList<VisitRequest>receivedVisitRequestList; //  requests with received status will reside in this list
    ArrayList<VisitRequest>pendingVisitsList;       //   requests with accepted i-e pending and expired status will reside in this list
    ArrayList<VisitRequest>completedVisitsList;     //   completed visits will reside in this list
    ArrayList<WorkRequest>receivedWorkRequestList;
    ArrayList<WorkRequest>pendingWorkList;
    ArrayList<WorkRequest>completedWorkList;
    ArrayList<ResponseTimeRating>responseTimeRatingList;
    ArrayList<WorkRating>workRatingList;


    Customer(){

    }

    public Customer(String id, String fullName, String userName, String phone, String password,
                    String imageUrl,ArrayList<String>BroadCastRequestsIdsList,HashMap<String,
                    ArrayList<Message>>chatHashmap, ArrayList<VisitRequest>receivedVisitRequestList,
                    ArrayList<VisitRequest>pendingVisitsList, ArrayList<VisitRequest>completedVisitsList,
                   ArrayList<ResponseTimeRating>responseTimeRatingList, ArrayList<WorkRequest>receivedWorkRequestList,
                    ArrayList<WorkRequest>pendingWorkList, ArrayList<WorkRequest>completedWorkList,
                    ArrayList<WorkRating>workRatingList,int responseRating,int averageWorkRating)
                     {
        this.id = id;
        this.fullName = fullName;
        this.userName = userName;
        this.phone = phone;
        this.password = password;
        this.imageUrl = imageUrl;
        this.BroadCastRequestsIdsList = BroadCastRequestsIdsList;
        this.chatHashmap = chatHashmap;
        this.receivedVisitRequestList = receivedVisitRequestList;
        this.pendingVisitsList = pendingVisitsList;
        this.completedVisitsList = completedVisitsList;
        this.receivedWorkRequestList = receivedWorkRequestList;
        this.pendingWorkList = pendingWorkList;
        this.completedWorkList = completedWorkList;
        this.workRatingList = workRatingList;
        this.responseRating = responseRating;
        this.averageWorkRating = averageWorkRating;

    }

    protected Customer(Parcel in) {
        id = in.readString();
        fullName = in.readString();
        userName = in.readString();
        phone = in.readString();
        password = in.readString();
        imageUrl = in.readString();
        BroadCastRequestsIdsList = in.createStringArrayList();
        responseRating = in.readInt();
        averageWorkRating = in.readInt();
        receivedVisitRequestList = in.createTypedArrayList(VisitRequest.CREATOR);
        pendingVisitsList = in.createTypedArrayList(VisitRequest.CREATOR);
        completedVisitsList = in.createTypedArrayList(VisitRequest.CREATOR);
        receivedWorkRequestList = in.createTypedArrayList(WorkRequest.CREATOR);
        pendingWorkList = in.createTypedArrayList(WorkRequest.CREATOR);
        completedWorkList = in.createTypedArrayList(WorkRequest.CREATOR);
        responseTimeRatingList =  in.createTypedArrayList(ResponseTimeRating.CREATOR);
        workRatingList = in.createTypedArrayList(WorkRating.CREATOR);



    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

    public ArrayList<String> getBroadCastRequestsIdsList() {
        return BroadCastRequestsIdsList;
    }

    public void setBroadCastRequestsIdsList(ArrayList<String> broadCastRequestsIdsList) {
        BroadCastRequestsIdsList = broadCastRequestsIdsList;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public HashMap<String, ArrayList<Message>> getChatHashmap() {
        return chatHashmap;
    }

    public void setChatHashmap(HashMap<String, ArrayList<Message>> chatHashmap) {
        this.chatHashmap = chatHashmap;
    }

    public ArrayList<VisitRequest> getReceivedVisitRequestList() {
        return receivedVisitRequestList;
    }

    public void setReceivedVisitRequestList(ArrayList<VisitRequest> receivedVisitRequestList) {
        this.receivedVisitRequestList = receivedVisitRequestList;
    }

    public ArrayList<VisitRequest> getPendingVisitsList() {
        return pendingVisitsList;
    }

    public void setPendingVisitsList(ArrayList<VisitRequest> pendingVisitsList) {
        this.pendingVisitsList = pendingVisitsList;
    }

    public ArrayList<VisitRequest> getCompletedVisitsList() {
        return completedVisitsList;
    }

    public void setCompletedVisitsList(ArrayList<VisitRequest> completedVisitsList) {
        this.completedVisitsList = completedVisitsList;
    }

    public int getResponseRating() {
        return responseRating;
    }

    public void setResponseRating(int responseRating) {
        this.responseRating = responseRating;
    }

    public int getAverageWorkRating() {
        return averageWorkRating;
    }

    public void setAverageWorkRating(int averageWorkRating) {
        this.averageWorkRating = averageWorkRating;
    }

    public ArrayList<WorkRequest> getReceivedWorkRequestList() {
        return receivedWorkRequestList;
    }

    public void setReceivedWorkRequestList(ArrayList<WorkRequest> receivedWorkRequestList) {
        this.receivedWorkRequestList = receivedWorkRequestList;
    }

    public ArrayList<WorkRequest> getPendingWorkList() {
        return pendingWorkList;
    }

    public void setPendingWorkList(ArrayList<WorkRequest> pendingWorkList) {
        this.pendingWorkList = pendingWorkList;
    }

    public ArrayList<WorkRequest> getCompletedWorkList() {
        return completedWorkList;
    }

    public void setCompletedWorkList(ArrayList<WorkRequest> completedWorkList) {
        this.completedWorkList = completedWorkList;
    }

    public ArrayList<WorkRating> getWorkRatingList() {
        return workRatingList;
    }

    public void setWorkRatingList(ArrayList<WorkRating> workRatingList) {
        this.workRatingList = workRatingList;
    }

    public ArrayList<ResponseTimeRating> getResponseTimeRatingList() {
        return responseTimeRatingList;
    }

    public void setResponseTimeRatingList(ArrayList<ResponseTimeRating> responseTimeRatingList) {
        this.responseTimeRatingList = responseTimeRatingList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(fullName);
        dest.writeString(userName);
        dest.writeString(phone);
        dest.writeString(password);
        dest.writeString(imageUrl);
        dest.writeStringList(BroadCastRequestsIdsList);
        dest.writeInt(responseRating);
        dest.writeInt(averageWorkRating);
        dest.writeList(receivedVisitRequestList);
        dest.writeList(pendingVisitsList);
        dest.writeList(completedVisitsList);
        dest.writeList(receivedWorkRequestList);
        dest.writeList(pendingWorkList);
        dest.writeList(completedWorkList);
        dest.writeList(responseTimeRatingList);
        dest.writeList(workRatingList);
    }
}
