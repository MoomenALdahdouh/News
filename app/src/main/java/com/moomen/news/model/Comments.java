package com.moomen.news.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Comments implements Serializable {
    @Keep
    @SerializedName("userName")
    private String userName;
    @SerializedName("userID")
    private String userID;
    @SerializedName("date")
    private String date;
    @SerializedName("idNews")
    private String idNews;
    @SerializedName("comment")
    private String comment;
    @SerializedName("userImage")
    private String userImage;

    public Comments() {
    }

    public Comments(String userName, String userID, String date, String idNews, String comment, String userImage) {
        this.userName = userName;
        this.userID = userID;
        this.date = date;
        this.idNews = idNews;
        this.comment = comment;
        this.userImage = userImage;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdNews() {
        return idNews;
    }

    public void setIdNews(String idNews) {
        this.idNews = idNews;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
