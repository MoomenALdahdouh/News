package com.moomen.news.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RemoveAccount implements Serializable {
    @Keep
    @SerializedName("userEmail")
    private String userEmail;

    public RemoveAccount() {
    }

    public RemoveAccount(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
