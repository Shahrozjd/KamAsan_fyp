package com.example.kaamasaan;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class ServiceProvider implements Parcelable {
    String id,fullName,userName,phone,password,category,availaibilityTime,responseTime,bio,userType,profilePicUrl;
    double latititude, longitude;
    int radius, responseRating, averageWorkRating,visitCost;
    HashMap<String, ArrayList<Message>> chatHashmap;
    ArrayList<VisitRequest>sentVisitRequestList;//  requests with sent and rejected status will reside in this list
    ArrayList<VisitRequest>pendingVisitsList;  //   requests with accepted i-e pending and expired status will reside in this list
    ArrayList<VisitRequest>completedVisitsList;         //    completed visits will reside in this list
    ArrayList<ResponseTimeRating>responseTimeRatingList;
    ArrayList<WorkRequest>sentWorkRequestList;
    ArrayList<WorkRequest>pendingWorkList;
    ArrayList<WorkRequest>completedWorkList;
    ArrayList<WorkRating>workRatingList;



    public ServiceProvider(){

    }

    public ServiceProvider(String id, String fullName, String userName, String phone, String password, String category, String availaibilityTime,
                           String responseTime, String bio, String userType, String profilePicUrl, double latititude, double longitude,
                           int radius, HashMap<String, ArrayList<Message>>chatHashmap, int ResponseTimeRating, int workRating, int visitCost
                          ,ArrayList<VisitRequest>sentVisitRequestList,ArrayList<VisitRequest>pendingVisitsList,ArrayList<VisitRequest>completedVisitsList
                           , ArrayList<ResponseTimeRating>responseTimeRatingList, ArrayList<WorkRequest>sentWorkRequestList,
                           ArrayList<WorkRequest>pendingWorkList, ArrayList<WorkRequest>completedWorkList,ArrayList<WorkRating>workRatingList)
    {
        this.id = id;
        this.fullName = fullName;
        this.userName = userName;
        this.phone = phone;
        this.password = password;
        this.category = category;
        this.availaibilityTime = availaibilityTime;
        this.responseTime = responseTime;
        this.bio = bio;
        this.userType = userType;
        this.profilePicUrl = profilePicUrl;
        this.latititude = latititude;
        this.longitude = longitude;
        this.radius = radius;
        this.chatHashmap = chatHashmap;
        this.responseRating = ResponseTimeRating;
        this.averageWorkRating = workRating;
        this.visitCost = visitCost;
        this.sentVisitRequestList = sentVisitRequestList;
        this.pendingVisitsList = pendingVisitsList;
        this.completedVisitsList = completedVisitsList;
        this.responseTimeRatingList = responseTimeRatingList;
        this.sentWorkRequestList = sentWorkRequestList;
        this.pendingWorkList = pendingWorkList;
        this.completedWorkList = completedWorkList;
        this.workRatingList = workRatingList;
    }

    protected ServiceProvider(Parcel in) {
        id = in.readString();
        fullName = in.readString();
        userName = in.readString();
        phone = in.readString();
        password = in.readString();
        category = in.readString();
        availaibilityTime = in.readString();
        responseTime = in.readString();
        bio = in.readString();
        userType = in.readString();
        profilePicUrl = in.readString();
        latititude = in.readDouble();
        longitude = in.readDouble();
        radius = in.readInt();
        chatHashmap = chatHashmap;
        responseRating = in.readInt();
        averageWorkRating = in.readInt();
        visitCost = in.readInt();
        sentVisitRequestList = in.createTypedArrayList(VisitRequest.CREATOR);
        pendingVisitsList = in.createTypedArrayList(VisitRequest.CREATOR);
        completedVisitsList = in.createTypedArrayList(VisitRequest.CREATOR);
        sentWorkRequestList = in.createTypedArrayList(WorkRequest.CREATOR);
        pendingWorkList = in.createTypedArrayList(WorkRequest.CREATOR);
        completedWorkList = in.createTypedArrayList(WorkRequest.CREATOR);
        responseTimeRatingList =  in.createTypedArrayList(ResponseTimeRating.CREATOR);
        workRatingList = in.createTypedArrayList(WorkRating.CREATOR);

    }

    public static final Creator<ServiceProvider> CREATOR = new Creator<ServiceProvider>() {
        @Override
        public ServiceProvider createFromParcel(Parcel in) {
            return new ServiceProvider(in);
        }

        @Override
        public ServiceProvider[] newArray(int size) {
            return new ServiceProvider[size];
        }
    };

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAvailaibilityTime() {
        return availaibilityTime;
    }

    public void setAvailaibilityTime(String availaibilityTime) {
        this.availaibilityTime = availaibilityTime;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public double getLatititude() {
        return latititude;
    }

    public void setLatititude(double latititude) {
        this.latititude = latititude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public HashMap<String, ArrayList<Message>> getChatHashmap() {
        return chatHashmap;
    }

    public void setChatHashmap(HashMap<String, ArrayList<Message>> chatHashmap) {
        this.chatHashmap = chatHashmap;
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

    public int getVisitCost() {
        return visitCost;
    }

    public void setVisitCost(int visitCost) {
        this.visitCost = visitCost;
    }

    public ArrayList<VisitRequest> getSentVisitRequestList() {
        return sentVisitRequestList;
    }

    public void setSentVisitRequestList(ArrayList<VisitRequest> sentVisitRequestList) {
        this.sentVisitRequestList = sentVisitRequestList;
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

    public ArrayList<com.example.kaamasaan.ResponseTimeRating> getResponseTimeRatingList() {
        return responseTimeRatingList;
    }

    public void setResponseTimeRatingList(ArrayList<com.example.kaamasaan.ResponseTimeRating> responseTimeRatingList) {
        this.responseTimeRatingList = responseTimeRatingList;
    }

    public ArrayList<WorkRequest> getSentWorkRequestList() {
        return sentWorkRequestList;
    }

    public void setSentWorkRequestList(ArrayList<WorkRequest> sentWorkRequestList) {
        this.sentWorkRequestList = sentWorkRequestList;
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
        dest.writeString(category);
        dest.writeString(availaibilityTime);
        dest.writeString(responseTime);
        dest.writeString(bio);
        dest.writeString(userType);
        dest.writeString(profilePicUrl);
        dest.writeDouble(latititude);
        dest.writeDouble(longitude);
        dest.writeInt(radius);
       dest.writeMap(chatHashmap);
       dest.writeInt(responseRating);
       dest.writeInt(averageWorkRating);
       dest.writeInt(visitCost);
        dest.writeList(sentVisitRequestList);
        dest.writeList(pendingVisitsList);
        dest.writeList(completedVisitsList);
        dest.writeList(sentWorkRequestList);
        dest.writeList(pendingWorkList);
        dest.writeList(completedWorkList);
        dest.writeList(responseTimeRatingList);
        dest.writeList(workRatingList);

    }
}
