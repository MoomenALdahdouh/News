package com.moomen.news.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Likes implements Serializable {
    @Keep
    @SerializedName("newsID")
    private String newsID;
    @SerializedName("usersID")
    private ArrayList<String> usersID;

    public Likes() {
    }

    public Likes(String newsID, ArrayList<String> usersID) {
        this.newsID = newsID;
        this.usersID = usersID;
    }

    public String getNewsID() {
        return newsID;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }

    public ArrayList<String> getUsersID() {
        return usersID;
    }

    public void setUsersID(ArrayList<String> usersID) {
        this.usersID = usersID;
    }
}
