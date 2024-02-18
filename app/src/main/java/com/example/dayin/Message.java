package com.example.dayin;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("role")
    private String role;

    @SerializedName("content")
    private String content;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
