package com.example.dayin;

import com.google.gson.annotations.SerializedName;

public class ChatChoice {
    @SerializedName("message")
    private ChatMessage message;

    public String getText() {
        return message.getText().trim();
    }
}
