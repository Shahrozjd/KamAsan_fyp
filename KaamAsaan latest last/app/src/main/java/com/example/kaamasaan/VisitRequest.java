package com.example.kaamasaan;

import android.os.Parcel;
import android.os.Parcelable;

public class VisitRequest implements Parcelable {
    String RequestId,UserId,UserName, Status;  // UserName will be Customer's User Name on Customer side and ServiceProvider's User Name on Service Prover side
    String potentialTimeAndDate;              //Status can be Sent, Accepted , Rejected, Completed
    int duration, VisitCost;
    String imageUrl;// this imageUrl is profile pic Url of Service Provider.  We will use Url giving response rating to customer

   public VisitRequest(){

   }

    public VisitRequest(String requestId, String UserId,String userName, String status, String potentialTimeAndDate,int duration, int visitCost,String imageUrl) {
       this. RequestId = requestId;
       this.UserId = UserId;
        this.UserName = userName;
       this. Status = status;
       this. potentialTimeAndDate = potentialTimeAndDate;
        this.duration = duration;
       this. VisitCost = visitCost;
       this.imageUrl = imageUrl;
    }

    protected VisitRequest(Parcel in) {
        RequestId = in.readString();
        UserId  = in.readString();
        UserName = in.readString();
        Status = in.readString();
        potentialTimeAndDate = in.readString();
        duration = in.readInt();
        VisitCost = in.readInt();
        imageUrl = in.readString();
    }

    public static final Creator<VisitRequest> CREATOR = new Creator<VisitRequest>() {
        @Override
        public VisitRequest createFromParcel(Parcel in) {
            return new VisitRequest(in);
        }

        @Override
        public VisitRequest[] newArray(int size) {
            return new VisitRequest[size];
        }
    };

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getPotentialTimeAndDate() {
        return potentialTimeAndDate;
    }

    public void setPotentialTimeAndDate(String potentialTimeAndDate) {
        this.potentialTimeAndDate = potentialTimeAndDate;

    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getVisitCost() {
        return VisitCost;
    }

    public void setVisitCost(int visitCost) {
        VisitCost = visitCost;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(RequestId);
        dest.writeString(UserId);
        dest.writeString(UserName);
        dest.writeString(Status);
        dest.writeString(potentialTimeAndDate);
        dest.writeInt(duration);
        dest.writeInt(VisitCost);
    }
}
