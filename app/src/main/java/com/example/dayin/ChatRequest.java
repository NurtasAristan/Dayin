package com.example.dayin;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class ChatRequest {
    @SerializedName("model")
    private String model;

    @SerializedName("messages")
    private Message[] messages;

    public ChatRequest(String model, Message[] messages) {
        this.model = model;
        this.messages = messages;
    }
}

