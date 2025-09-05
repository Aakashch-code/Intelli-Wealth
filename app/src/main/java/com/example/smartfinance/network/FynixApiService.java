package com.example.smartfinance.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FynixApiService {

    // Gemini Pro model endpoint
    @POST("v1beta/models/gemini-pro:generateContent")
    Call<ApiModels.GeminiResponse> chatWithGemini(
            @Query("key") String apiKey,
            @Body ApiModels.GeminiRequest request
    );

}