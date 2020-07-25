package com.example.kaamasaan;

import android.os.Parcel;
import android.os.Parcelable;

public class WorkRequest implements Parcelable {
    String requestId, UserName,   Status;  // UserName will be Customer's User Name on Customer side and ServiceProvider's User Name on Service Prover side.Status will
    String startDate, endDate, Mode; // offline/online
    int estimatedCost;

public WorkRequest(){

    }

    protected WorkRequest(Parcel in) {
        requestId = in.readString();

        UserName = in.readString();
        Status = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        Mode = in.readString();
        estimatedCost = in.readInt();
    }

    public static final Creator<WorkRequest> CREATOR = new Creator<WorkRequest>() {
        @Override
        public WorkRequest createFromParcel(Parcel in) {
            return new WorkRequest(in);
        }

        @Override
        public WorkRequest[] newArray(int size) {
            return new WorkRequest[size];
        }
    };

    public WorkRequest(String requestId,String userName, String status, String startDate, String endDate, String mode, int estimatedCost) {
        this.requestId = requestId;

        UserName = userName;
        Status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        Mode = mode;
        this.estimatedCost = estimatedCost;
    }



    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }

    public int getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(int estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(requestId);
        dest.writeString(UserName);
        dest.writeString(Status);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(Mode);
        dest.writeInt(estimatedCost);
    }
}
