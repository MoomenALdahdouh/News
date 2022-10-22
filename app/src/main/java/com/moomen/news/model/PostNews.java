package com.moomen.news.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PostNews implements Serializable {
    @Keep
    @SerializedName("companyID")
    private String companyID;
    @SerializedName("companyName")
    private String companyName;
    @SerializedName("companyImage")
    private String companyImage;
    @SerializedName("image")
    private String image;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("date")
    private String date;
    @SerializedName("like")
    private int like;
    @SerializedName("showing")
    private int showing;
    @SerializedName("commentsArrayList")
    private ArrayList<Comments> commentsArrayList;
    @SerializedName("paid")
    private boolean paid;
    @SerializedName("tagArrayList")
    private ArrayList<String> tagArrayList;
    @SerializedName("visibility")
    private boolean visibility;
    @SerializedName("companyStatus")
    private boolean companyStatus;
    @SerializedName("newsStatus")
    private boolean newsStatus;
    @SerializedName("country")
    private String country;


    public PostNews() {
    }

    public PostNews(String companyID, String companyName, String companyImage, String image, String title
            , String description, String date, int like, int showing, ArrayList<Comments> commentsArrayList, boolean paid
            , ArrayList<String> tagArrayList, boolean visibility, boolean companyStatus, boolean newsStatus, String country) {
        this.companyID = companyID;
        this.companyName = companyName;
        this.companyImage = companyImage;
        this.image = image;
        this.title = title;
        this.description = description;
        this.date = date;
        this.like = like;
        this.showing = showing;
        this.commentsArrayList = commentsArrayList;
        this.paid = paid;
        this.tagArrayList = tagArrayList;
        this.visibility = visibility;
        this.companyStatus = companyStatus;
        this.newsStatus = newsStatus;
        this.country = country;
    }

    public String getImage() {
        return image;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyImage() {
        return companyImage;
    }

    public void setCompanyImage(String companyImage) {
        this.companyImage = companyImage;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getShowing() {
        return showing;
    }

    public void setShowing(int showing) {
        this.showing = showing;
    }

    public ArrayList<Comments> getCommentsArrayList() {
        return commentsArrayList;
    }

    public void setCommentsArrayList(ArrayList<Comments> commentsArrayList) {
        this.commentsArrayList = commentsArrayList;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public ArrayList<String> getTagArrayList() {
        return tagArrayList;
    }

    public void setTagArrayList(ArrayList<String> tagArrayList) {
        this.tagArrayList = tagArrayList;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public boolean isCompanyStatus() {
        return companyStatus;
    }

    public void setCompanyStatus(boolean companyStatus) {
        this.companyStatus = companyStatus;
    }

    public boolean isNewsStatus() {
        return newsStatus;
    }

    public void setNewsStatus(boolean newsStatus) {
        this.newsStatus = newsStatus;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
