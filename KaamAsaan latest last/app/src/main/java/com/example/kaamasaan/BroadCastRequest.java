package com.example.kaamasaan;

import java.util.ArrayList;

public class BroadCastRequest {
    String id, customerUserName,title,description,category,imageUrl1,imageUrl2,imageUrl3;
    Double latitude,longitude,workRating,minVisitCost, maxVisitCost;
    int noOfPersonsVisiting;
    ArrayList<VisitRequest>VisitRequestsList;

    public BroadCastRequest(){

    }

    public BroadCastRequest(String id, String customerUserName, String title, String description, String category,
                            Double latitude, Double longitude, Double workRating, Double minVisitCost,
                            Double maxVisitCost,int  noOfPersonsVisiting,String imageUrl1,String imageUrl2,
                            String imageUrl3, ArrayList<VisitRequest>VisitRequestsList) {
        this.id = id;
        this.customerUserName = customerUserName;
        this.title = title;
        this.description = description;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.workRating = workRating;
        this.minVisitCost = minVisitCost;
        this.maxVisitCost = maxVisitCost;
        this.noOfPersonsVisiting = noOfPersonsVisiting;
        this.imageUrl1 =imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.imageUrl3 = imageUrl3;
        this.VisitRequestsList = VisitRequestsList;

    }

    public ArrayList<VisitRequest> getVisitRequestsList() {
        return VisitRequestsList;
    }

    public void setVisitRequestsList(ArrayList<VisitRequest> visitRequestsList) {
        VisitRequestsList = visitRequestsList;
    }

    public int getNoOfPersonsVisiting() {
        return noOfPersonsVisiting;
    }

    public void setNoOfPersonsVisiting(int noOfPersonsVisiting) {
        this.noOfPersonsVisiting = noOfPersonsVisiting;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerUserName() {
        return customerUserName;
    }

    public void setCustomerUserName(String customerUserName) {
        this.customerUserName = customerUserName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getWorkRating() {
        return workRating;
    }

    public void setWorkRating(Double workRating) {
        this.workRating = workRating;
    }

    public Double getMinVisitCost() {
        return minVisitCost;
    }

    public void setMinVisitCost(Double minVisitCost) {
        this.minVisitCost = minVisitCost;
    }

    public Double getMaxVisitCost() {
        return maxVisitCost;
    }

    public void setMaxVisitCost(Double maxVisitCost) {
        this.maxVisitCost = maxVisitCost;
    }

    public String getImageUrl1() {
        return imageUrl1;
    }

    public void setImageUrl1(String imageUrl1) {
        this.imageUrl1 = imageUrl1;
    }

    public String getImageUrl2() {
        return imageUrl2;
    }

    public void setImageUrl2(String imageUrl2) {
        this.imageUrl2 = imageUrl2;
    }

    public String getImageUrl3() {
        return imageUrl3;
    }

    public void setImageUrl3(String imageUrl3) {
        this.imageUrl3 = imageUrl3;
    }
}
