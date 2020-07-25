package com.example.kaamasaan;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable {
    String userName, msg,currentDate;

    public Message(){

    }

    public Message(String userName, String msg,String currentDate) {
        this.userName = userName;
        this.msg = msg;
        this.currentDate = currentDate;
    }

    public Message(String userName) {
        this.userName = userName;
    }

    protected Message(Parcel in) {
        userName = in.readString();
        msg = in.readString();
        currentDate = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(msg);
        dest.writeString(currentDate);
    }
}
