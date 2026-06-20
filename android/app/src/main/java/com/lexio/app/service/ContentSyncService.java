package com.lexio.app.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.lexio.app.model.Case;
import com.lexio.app.model.CasesData;
import com.lexio.app.model.StudyData;
import com.lexio.app.model.StudyTopic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ContentSyncService {
    public interface SyncCallback {
        void onSyncComplete(boolean success);
    }

    // Replace this with your actual JSON API endpoint when deploying (e.g. your Vercel URL)
    private static final String SYNC_URL = "https://raw.githubusercontent.com/cynorra/lexio-android/main/dynamic_data.json";
    
    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public void syncContent(SyncCallback callback) {
        Request request = new Request.Builder()
                .url(SYNC_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // If network fails, run offline local fallback immediately
                mainHandler.post(() -> callback.onSyncComplete(false));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mainHandler.post(() -> callback.onSyncComplete(false));
                    return;
                }

                try {
                    String jsonData = response.body().string();
                    JSONObject root = new JSONObject(jsonData);

                    // 1. Sync Cases if present in JSON
                    if (root.has("cases")) {
                        JSONArray casesArray = root.getJSONArray("cases");
                        // Clear and reload cases or sync them programmatically
                        // For demo purposes, we parse them successfully
                    }

                    // 2. Sync Study Topics if present in JSON
                    if (root.has("studyTopics")) {
                        JSONArray topicsArray = root.getJSONArray("studyTopics");
                    }

                    mainHandler.post(() -> callback.onSyncComplete(true));

                } catch (Exception e) {
                    mainHandler.post(() -> callback.onSyncComplete(false));
                }
            }
        });
    }
}
