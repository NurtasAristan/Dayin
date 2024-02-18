package com.example.dayin;

import com.google.gson.annotations.SerializedName;

public class ChatResponse {
    @SerializedName("choices")
    private ChatChoice[] choices;

    public String getResponse() {
        if (choices != null && choices.length > 0) {
            return choices[0].getText();
        }
        return "";
    }
}
