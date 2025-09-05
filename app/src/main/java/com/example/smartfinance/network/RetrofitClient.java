package com.example.smartfinance.network;

import android.content.Context;
import android.util.Log;

import com.example.smartfinance.R;

import com.example.smartfinance.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    String apiKey = BuildConfig.GEMINI_API_KEY;

    private static final String TAG = "RetrofitClient";
    private static Retrofit retrofit = null;

    public static synchronized FynixApiService getApiService(Context context) {
        if (retrofit == null) {
            String baseUrl = context.getString(R.string.gemini_base_url);
            String apiKey = BuildConfig.GEMINI_API_KEY;

            // Validate configuration
            if (baseUrl == null || baseUrl.isEmpty()) {
                Log.e(TAG, "❌ Base URL is not configured in strings.xml");
                throw new IllegalStateException("Base URL not configured. Please check strings.xml");
            }


            if (apiKey == null || apiKey.isEmpty()) {
                Log.e(TAG, "❌ API key is missing from BuildConfig (check local.properties)");
                throw new IllegalStateException("API key not configured. Add GEMINI_API_KEY in local.properties");
            }
            // Ensure base URL ends with slash
            if (!baseUrl.endsWith("/")) {
                baseUrl += "/";
                Log.w(TAG, "⚠️ Base URL was missing trailing slash. Fixed to: " + baseUrl);
            }

            Log.d(TAG, "✅ Using base URL: " + baseUrl);
            Log.d(TAG, "✅ API key configured: " + apiKey.substring(0, Math.min(apiKey.length(), 8)) + "...");

            // Logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> {
                if (message.startsWith("{") || message.startsWith("[")) {
                    Log.d("API_JSON", message);
                } else if (message.startsWith("-->") || message.startsWith("<--")) {
                    Log.d("API_FLOW", message);
                } else {
                    Log.d("API_DETAIL", message);
                }
            });
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttpClient with header interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(chain -> {
                        Request original = chain.request();

                        // ✅ Gemini expects x-goog-api-key, not Authorization
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

            Log.d(TAG, "✅ Retrofit client initialized successfully");
        }
        return retrofit.create(FynixApiService.class);
    }

    public static void reset() {
        retrofit = null;
        Log.d(TAG, "🔄 Retrofit client reset");
    }

    public static boolean isInitialized() {
        return retrofit != null;
    }
}
