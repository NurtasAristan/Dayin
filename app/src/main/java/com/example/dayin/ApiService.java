package com.example.dayin;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer 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"
    })
    @POST("chat/completions")
    Call<ChatResponse> getChatCompletion(@Body ChatRequest chatRequest);
}
