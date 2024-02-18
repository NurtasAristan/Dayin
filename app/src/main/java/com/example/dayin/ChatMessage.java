package com.example.dayin;

import com.google.gson.annotations.SerializedName;

public class ChatMessage {
    @SerializedName("content")
    private String content;

    public String getText() {
        return content.trim();
    }
}
