package com.example.kaamasaan;

import android.os.Parcel;
import android.os.Parcelable;

public class WorkRating implements Parcelable {
    String ImageUrlOfUser,UserNameOfUser;  //customer and service provider's image url  and userName respectively;
    String Review;
    int rating;


    public WorkRating(){

    }
    public WorkRating(String imageUrlOfUser, String userNameOfUser, String review, int rating) {
        ImageUrlOfUser = imageUrlOfUser;
        UserNameOfUser = userNameOfUser;
        Review = review;
        this.rating = rating;
    }

    public String getImageUrlOfUser() {
        return ImageUrlOfUser;
    }

    public void setImageUrlOfUser(String imageUrlOfUser) {
        ImageUrlOfUser = imageUrlOfUser;
    }

    public String getUserNameOfUser() {
        return UserNameOfUser;
    }

    public void setUserNameOfUser(String userNameOfUser) {
        UserNameOfUser = userNameOfUser;
    }

    public String getReview() {
        return Review;
    }

    public void setReview(String review) {
        Review = review;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    protected WorkRating(Parcel in) {
        ImageUrlOfUser = in.readString();
        UserNameOfUser = in.readString();
        Review = in.readString();
        rating = in.readInt();
    }

    public static final Creator<WorkRating> CREATOR = new Creator<WorkRating>() {
        @Override
        public WorkRating createFromParcel(Parcel in) {
            return new WorkRating(in);
        }

        @Override
        public WorkRating[] newArray(int size) {
            return new WorkRating[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ImageUrlOfUser);
        dest.writeString(UserNameOfUser);
        dest.writeString(Review);
        dest.writeInt(rating);
    }
}
