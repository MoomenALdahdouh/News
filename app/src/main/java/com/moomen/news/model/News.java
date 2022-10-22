package com.moomen.news.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class News implements Serializable {
    @Keep
    @SerializedName("page")
    private String page;
    @SerializedName("totalPage")
    private String totalPage;
    @SerializedName("page")
    private ArrayList<PostNews> postNewsArrayList;

    public News() {
    }

    public News(String page, String totalPage, ArrayList<PostNews> postNewsArrayList) {
        this.page = page;
        this.totalPage = totalPage;
        this.postNewsArrayList = postNewsArrayList;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    public ArrayList<PostNews> getPostNewsArrayList() {
        return postNewsArrayList;
    }

    public void setPostNewsArrayList(ArrayList<PostNews> postNewsArrayList) {
        this.postNewsArrayList = postNewsArrayList;
    }
}
