package com.example.smartfinance.ui.fynix.network;

import com.example.smartfinance.ui.fynix.model.ApiModels;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FynixApiService {

    @POST("models/gemini-1.5-flash:generateContent")
    Call<ApiModels.GeminiResponse> chatWithGemini(
            @Body ApiModels.GeminiRequest request
    );
}
