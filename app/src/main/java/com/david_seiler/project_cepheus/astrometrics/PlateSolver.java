package com.david_seiler.project_cepheus.astrometrics;

import android.content.Context;
import android.util.Log;

import com.david_seiler.project_cepheus.camera.sony.utils.SimpleHttpClient;
import com.david_seiler.project_cepheus.ui.CaptureActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlateSolver {
    // TODO refactor
    private static final String TAG = CaptureActivity.class.getSimpleName();
    private static final String API_KEY = "gnlrsxqrzhphdssl";
    private String imageUrl;
    private Context context;

    public PlateSolver(Context context) {
        this.context = context;
    }

    public String plateSolve(String img) {
        // Get session
        String session = "";
        String submissionId = "";
        String jobId = "";
        String imgUrl = "";
        try {
            String url = "http://nova.astrometry.net/api/login";
            String request = "request-json=%7B%22apikey%22%3A+%22" + API_KEY + "%22%7D";
            Gson g = new Gson();
            Log.d(TAG, "Request:  " + request);
            String responseJson = SimpleHttpClient.httpPost(url, request);
            Log.d(TAG, "Response: " + responseJson);
            JSONObject replyJson = new JSONObject(responseJson);
            session = replyJson.getString("session");
            Log.d("WTF", session);
        } catch (JSONException | IOException e) {
            Log.d("WTF", e + "llkj ");
            return null;
        }
        // test with url upload and get submission_id
        try {
            String url = "http://nova.astrometry.net/api/url_upload";
            String request = "request-json=%7B%22session%22%3A+%22" + session + "%22,%22url%22%3A+%22" + img + "%22%7D%0D%0A";
            Log.d(TAG, "Request:  " + request);
            String responseJson = SimpleHttpClient.httpPost(url, request);
            Log.d(TAG, "Response: " + responseJson);
            JSONObject replyJson = new JSONObject(responseJson);
            submissionId = replyJson.getString("subid");
            Log.d("WTF", submissionId);
        } catch (JSONException | IOException e) {
            Log.d("WTF", e + " lllllll");
            return null;
        }

        // get job_id
        try {
            // TODO just sleep there because im lazy, replace with proper status checking
            Thread.sleep(30000);
            JSONObject requestJson =
                    new JSONObject().put("request-json", new JSONObject()
                            .put("session", session)
                            .put("url", img));
            String url = String.format("http://nova.astrometry.net/api/submissions/%s", submissionId);

            Log.d(TAG, "Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString(), 32000);
            Log.d(TAG, "Response: " + responseJson);
            JSONObject replyJson = new JSONObject(responseJson);
            jobId = replyJson.getJSONArray("jobs").getString(0);
            Log.d("WTF", jobId);

            // For now just return the url
            imgUrl = String.format("https://nova.astrometry.net/annotated_display/%s", jobId);

            // TODO load the image into the cache

        } catch (JSONException | IOException e) {
            Log.d("WTF", e + "kkkkk ");
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("WTF", imgUrl);
        return imgUrl;
    }
}
