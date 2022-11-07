package com.david_seiler.project_cepheus.camera.sony;

import android.accounts.AbstractAccountAuthenticator;
import android.os.SystemClock;
import android.util.Log;

import com.david_seiler.project_cepheus.camera.Camera;
import com.david_seiler.project_cepheus.camera.CameraApi;
import com.david_seiler.project_cepheus.camera.CameraSettings;
import com.david_seiler.project_cepheus.camera.CameraStatusObserver;
import com.david_seiler.project_cepheus.camera.sony.old.ServerDevice;
import com.david_seiler.project_cepheus.camera.sony.utils.SimpleHttpClient;
import com.david_seiler.project_cepheus.intervalometer.Sequence;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class SonyBetaSdkApi extends CameraApi implements CameraStatusObserver.CameraStatusListener {
    private static final String TAG = SonyBetaSdkApi.class.getSimpleName();

    private final Object idleOnlyOperationLock = new Object();

    private LinkedList<Object> idleOperationQueue = new LinkedList<>(); // Keep this as a que for now though it may just need to be a boolean
    private String currCameraStatus = "BUSY";

    private ArrayList<String> bulbShootingPhotos = new ArrayList<>();

    public SonyBetaSdkApi(Camera camera, String cameraUrl, ServerDevice targetDevice) {
        super(camera, cameraUrl, targetDevice);
    }

    @Override
    public JSONObject getAvailableApiList() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getAvailableApiList")
                            .put("params", new JSONArray()).put("id", id())
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            Log.d(TAG, "Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            Log.d(TAG, "Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException | IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public JSONObject getApplicationInfo() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getApplicationInfo") //
                            .put("params", new JSONArray()).put("id", id()) //
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            Log.d(TAG, "Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            Log.d(TAG, "Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public JSONObject getCameraStatus(boolean polled) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getEvent").put("params", new JSONArray().put(polled))
                            .put("id", id()).put("version", "1.8");

            String url = findActionListUrl(service) + "/" + service;
            Log.d(TAG, "Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            largeLog(TAG, "Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException | IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public JSONObject getIso() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getIsoSpeedRate").put("params", new JSONArray())
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            Log.d(TAG, "Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            Log.d(TAG, "Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException | IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public JSONObject setIso(String value) throws IOException {
        String service = "camera";
        try {
            // TODO: validate value
            JSONObject requestJson =
                    new JSONObject().put("method", "setIsoSpeedRate").put("params", new JSONArray().put(value))
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            Log.d(TAG, "Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            Log.d(TAG, "Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException | IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public JSONObject getFNumber() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getFNumber").put("params", new JSONArray())
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            Log.d(TAG, "Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            Log.d(TAG, "Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException | IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public JSONObject setFNumber(String value) throws IOException {
        String service = "camera";
        try {
            // TODO: validate value
            JSONObject requestJson =
                    new JSONObject().put("method", "setFNumber").put("params", new JSONArray().put(value))
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            Log.d(TAG, "Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            Log.d(TAG, "Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException | IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public JSONObject getShutterSpeed() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getShutterSpeed").put("params", new JSONArray())
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            Log.d(TAG, "Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            Log.d(TAG, "Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException | IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public JSONObject setShutterSpeed(String value) throws IOException {
        String service = "camera";
        try {
            // TODO: validate value
            JSONObject requestJson =
                    new JSONObject().put("method", "setShutterSpeed").put("params", new JSONArray().put(value))
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            Log.d(TAG, "Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            Log.d(TAG, "Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException | IOException e) {
            throw new IOException(e);
        }
    }

    // Good for < 30 seconds
    @Override
    public String takePhoto() {
        queueIdleOperation();

        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "actTakePicture").put("params", new JSONArray())
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            Log.d(TAG, "Request:  " + requestJson);
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString(), 32000);
            Log.d(TAG, "Response: " + responseJson);

            JSONObject replyJson = new JSONObject(responseJson);
            if (findErrorCode(replyJson) == 40403) {
                // Camera still taking photo
                // This returns early before the file is ready
                JSONObject awaitRequest =
                        new JSONObject().put("method", "awaitTakePicture").put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                Log.d(TAG, "Request:  " + awaitRequest);
                String awaitResponse = SimpleHttpClient.httpPost(url, awaitRequest.toString(), 32000);
                Log.d(TAG, "Response: " + awaitResponse);
                replyJson = new JSONObject(awaitResponse);
            }
            JSONArray resultsObj = replyJson.getJSONArray("result");

            JSONArray imageUrlsObj = resultsObj.getJSONArray(0);
            String postImageUrl = null;
            if (1 <= imageUrlsObj.length()) {
                postImageUrl = imageUrlsObj.getString(0);
            }
            return postImageUrl;
        } catch (JSONException | MalformedURLException e) {
            // TODO handle
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: handle
            return null;
        }
    }

    private void queueIdleOperation() {
        synchronized (idleOnlyOperationLock) {
            while (!Objects.equals(currCameraStatus, "IDLE")) {
                idleOperationQueue.add(idleOnlyOperationLock);
                try {
                    idleOnlyOperationLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override
    public String takeBulbPhoto(long mTime) {
        queueIdleOperation();

        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startBulbShooting").put("params", new JSONArray())
                            .put("id", id()).put("version", "1.0");

            String url = findActionListUrl(service) + "/" + service;
            Log.d(TAG, "Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString(), 30000);
            largeLog(TAG, "Response: " + responseJson);

            SystemClock.sleep(mTime);   // apparently this is more precise

            largeLog(TAG, "Response: " + responseJson);
            requestJson =
                    new JSONObject().put("method", "stopBulbShooting").put("params", new JSONArray())
                            .put("id", id()).put("version", "1.0");

            url = findActionListUrl(service) + "/" + service;
            Log.d(TAG, "Request:  " + requestJson.toString());
            responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            largeLog(TAG, "Response: " + responseJson);

            JSONObject replyJson = new JSONObject(responseJson);
            JSONArray resultsObj = replyJson.getJSONArray("result");
            JSONArray imageUrlsObj = resultsObj.getJSONArray(0);
            String postImageUrl = null;
            if (1 <= imageUrlsObj.length()) {
                postImageUrl = imageUrlsObj.getString(0);
            }
            return postImageUrl;
        } catch (JSONException | IOException e) {
            return null;
        }
    }



    @Override
    public ArrayList<String> startSequence(Sequence seq) {
        ArrayList<String> allFrames = new ArrayList<>(seq.getNum_frames());
        bulbShootingPhotos.clear();
        // TODO: add continuous shooting for the short intervals.
        // Single Shooting >30 Implementation with intervals that are at least 0.5 seconds
        try {
            Thread.sleep(seq.getDelay());
            for (int i = 0; i < seq.getNum_frames(); ++i) {
                if (seq.isBulb()) { // supports 200 ms and longer exposures
                    takeBulbPhoto(seq.getExposureLength());
                } else {
                    allFrames.add(takePhoto());
                }
                Thread.sleep(seq.getMin_interval());
            }
        } catch (InterruptedException e) {
            // pass
            Log.d(TAG, "sequence failed");
        }


        // todo Continuous Shooting Implementation for sequences with < 0.5 seconds intervals
        Log.d("WTF", "sequence completed" + bulbShootingPhotos);
        if (seq.isBulb()) {
            queueIdleOperation();
            allFrames.clear();
            allFrames.addAll(bulbShootingPhotos);
        }
        Log.d(TAG, "sequence completed" + allFrames);
        return allFrames;
    }

    private ArrayList<String> continuousShootingSequence() {
        return null;
    }

    @Override
    public JSONObject downloadImage() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startBulbShooting").put("params", new JSONArray())
                            .put("id", id()).put("version", "1.0");

            String url = findActionListUrl(service) + "/" + service;
            Log.d(TAG, "Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString(), 30000);
            largeLog(TAG, "Response: " + responseJson);
            Thread.sleep(3000);

            // TODO this works, postviewurl is stored here: "bulbShootingUrl":[{"postviewUrl":
            requestJson =
                    new JSONObject().put("method", "getEvent").put("params", new JSONArray().put(false))
                            .put("id", id()).put("version", "1.8");

            url = findActionListUrl(service) + "/" + service;
            Log.d(TAG, "Request:  " + requestJson.toString());
            responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            largeLog(TAG, "Response: " + responseJson);
            requestJson =
                    new JSONObject().put("method", "stopBulbShooting").put("params", new JSONArray())
                            .put("id", id()).put("version", "1.0");

            url = findActionListUrl(service) + "/" + service;
            Log.d(TAG, "Request:  " + requestJson.toString());
            responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            largeLog(TAG, "Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException | IOException | InterruptedException e) {
            throw new IOException(e);
        }
    }

    @Override
    public JSONObject startLiveView() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startLiveview").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            Log.d(TAG, "Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            Log.d(TAG, "Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException | IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public JSONObject stopLiveView() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "stopLiveview").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            Log.d(TAG, "Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            Log.d(TAG, "Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException | IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String testMessage() {
        return "This is the sony api";
    }

    // helpers
    /**
     * Retrieves Action List URL from Server information.
     *
     * @param service
     * @return
     * @throws IOException
     */
    private String findActionListUrl(String service) throws IOException {
        List<ServerDevice.ApiService> services = targetDevice.getApiServices();
        for (ServerDevice.ApiService apiService : services) {
            if (apiService.getName().equals(service)) {
                return apiService.getActionListUrl();
            }
        }
        throw new IOException("actionUrl not found. service : " + service);
    }

    private int id() {
        return requestNumber++;
    }

    public static void largeLog(String tag, String content) {
        System.out.println("CONTENTLENGTH" + content.length());
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }

    private static int findErrorCode(JSONObject replyJson) throws JSONException {
        int code = 0; // 0 means no error.
        if (replyJson.has("error")) {
            JSONArray errorObj = replyJson.getJSONArray("error");
            code = errorObj.getInt(0);
        }
        return code;
    }

    @Override
    public void onCameraStatusChange(String status) {
        currCameraStatus = status;

        if (Objects.equals(status, "IDLE") && !idleOperationQueue.isEmpty()) {
            synchronized (idleOnlyOperationLock) {
                idleOnlyOperationLock.notifyAll();
                idleOperationQueue.removeFirst();
            }
        }
    }

    @Override
    public void onBulbShootingUrlChange(String url) {
        bulbShootingPhotos.add(url);
    }

    @Override
    public void onCameraSettingsChange(CameraSettings cameraSettings) {

    }
}
