package com.example.smartfinance.data.remote.network;

import com.example.smartfinance.data.model.ApiModels;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FynixApiService {

    @POST("models/gemini-2.5-pro:generateContent")
    Call<ApiModels.GeminiResponse> chatWithGemini(
            @Body ApiModels.GeminiRequest request
    );
}
