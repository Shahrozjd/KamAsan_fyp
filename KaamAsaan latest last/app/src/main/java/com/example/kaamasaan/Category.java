package com.example.kaamasaan;

import android.os.Parcel;
import android.os.Parcelable;

public class Category  implements Parcelable {
    String id,category;
    public Category(){

    }

    public Category(String id, String category) {
        this.id = id;
        this.category = category;
    }

    protected Category(Parcel in) {
        id = in.readString();
        category = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(category);
    }
}
