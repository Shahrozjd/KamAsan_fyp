package com.example.kaamasaan;

import android.os.Parcel;
import android.os.Parcelable;

public class ResponseTimeRating  implements Parcelable {
    String imageUrl,customerName;
    int responseRating;

    public ResponseTimeRating(){

    }

    public ResponseTimeRating(String imageUrl, String customerName, int responseRating) {
        this.imageUrl = imageUrl;
        this.customerName = customerName;
        this.responseRating = responseRating;
    }

    protected ResponseTimeRating(Parcel in) {
        imageUrl = in.readString();
        customerName = in.readString();
        responseRating = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageUrl);
        dest.writeString(customerName);
        dest.writeInt(responseRating);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResponseTimeRating> CREATOR = new Creator<ResponseTimeRating>() {
        @Override
        public ResponseTimeRating createFromParcel(Parcel in) {
            return new ResponseTimeRating(in);
        }

        @Override
        public ResponseTimeRating[] newArray(int size) {
            return new ResponseTimeRating[size];
        }
    };

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getResponseRating() {
        return responseRating;
    }

    public void setResponseRating(int responseRating) {
        this.responseRating = responseRating;
    }
}
