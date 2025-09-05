package com.example.smartfinance.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FynixApiService {

    @POST("models/gemini-1.5-pro:generateContent")
    Call<ApiModels.GeminiResponse> chatWithGemini(
            @Body ApiModels.GeminiRequest request
    );
}
