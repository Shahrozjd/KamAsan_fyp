package com.example.kaamasaan;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    String name, password;


    public User(){

    }

    public User(String name, String phone) {
        this.name = name;
        this.password = phone;
    }

    protected User(Parcel in) {
        name = in.readString();
        password = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return password;
    }

    public void setPhone(String phone) {
        this.password = phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(password);
    }
}
