package com.example.smartfinance.data.remote.network;

import android.content.Context;
import android.util.Log;

import com.example.smartfinance.BuildConfig;
import com.example.smartfinance.R;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String TAG = "RetrofitClient";
    private static Retrofit retrofit = null;

    public static synchronized FynixApiService getApiService(Context context) {
        if (retrofit == null) {
            String baseUrl = context.getString(R.string.gemini_base_url);
            String apiKey = BuildConfig.GEMINI_API_KEY;

            if (isInvalid(baseUrl)) {
                throw new IllegalStateException("Base URL not configured. Check strings.xml");
            }
            if (isInvalid(apiKey)) {
                throw new IllegalStateException("API key missing. Add GEMINI_API_KEY in local.properties");
            }
            if (!baseUrl.endsWith("/")) baseUrl += "/";

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("x-goog-api-key", apiKey)
                                .header("Content-Type", "application/json")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    })
                    .connectTimeout(45, TimeUnit.SECONDS)
                    .readTimeout(45, TimeUnit.SECONDS)
                    .writeTimeout(45, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Log.d(TAG, "Retrofit client initialized with base URL: " + baseUrl);
        }

        return retrofit.create(FynixApiService.class);
    }

    private static boolean isInvalid(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static void reset() {
        retrofit = null;
        Log.d(TAG, "Retrofit client reset");
    }

    public static boolean isInitialized() {
        return retrofit != null;
    }
}
