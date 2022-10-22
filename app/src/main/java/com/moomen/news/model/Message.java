package com.moomen.news.model;

import androidx.annotation.Keep;

import com.google.firebase.firestore.ServerTimestamp;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Message implements Serializable {
    @Keep
    @SerializedName("message")
    private String message;
    //@ServerTimestamp
    @SerializedName("date")
    private String date;
    @SerializedName("senderID")
    private String senderID;

    public Message() {
    }

    public Message(String message, String senderID,String date) {
        this.message = message;
        this.senderID = senderID;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }
}
