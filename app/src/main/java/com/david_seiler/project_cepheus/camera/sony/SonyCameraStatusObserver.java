package com.david_seiler.project_cepheus.camera.sony;

import android.content.Context;
import android.util.Log;

import com.david_seiler.project_cepheus.camera.CameraApi;
import com.david_seiler.project_cepheus.camera.CameraSettings;
import com.david_seiler.project_cepheus.camera.CameraStatusObserver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SonyCameraStatusObserver extends CameraStatusObserver {
    private static final String TAG = SonyCameraStatusObserver.class.getSimpleName();

    public SonyCameraStatusObserver(Context context, CameraApi cameraApi) {
        super(context, cameraApi);
    }

    /** todo ripped directly from SimpleCameraEventObserver,
     * With the given implementation the camera should be constantly being pinged at the interval of the timeout
     * Starts monitoring by continuously calling getEvent API.
     *
     * @return true if it successfully started, false if a monitoring is already
     *         started.
     */
    public boolean start() {
        if (!mIsActive) {
            Log.w(TAG, "start() observer is not active.");
            return false;
        }

        if (mWhileEventMonitoring) {
            Log.w(TAG, "start() already starting.");
            return false;
        }

        mWhileEventMonitoring = true;
        mCameraStatus = null;
        new Thread() {

            @Override
            public void run() {
                Log.d(TAG, "start() exec.");
                // Call getEvent API continuously.
                boolean firstCall = true;
                MONITORLOOP: while (mWhileEventMonitoring) {

                    // At first, call as non-Long Polling.
                    boolean longPolling = !firstCall;

                    try {
                        // Call getEvent API.
                        JSONObject replyJson = mCameraApi.getCameraStatus(longPolling);

                        // Check error code at first.
                        int errorCode = findErrorCode(replyJson);
                        Log.d(TAG, "getEvent errorCode: " + errorCode);
                        switch (errorCode) {
                            case 0: // no error
                                // Pass through.
                                break;
                            case 1: // "Any" error
                            case 12: // "No such method" error
//                                fireResponseErrorListener();
                                break MONITORLOOP; // end monitoring.
                            case 2: // "Timeout" error
                                // Re-call immediately.
                                continue MONITORLOOP;
                            case 40403: // Still capturing
                            case 40402: // "Already polling" error
                                // Retry after 5 sec.
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    // do nothing.
                                }
                                continue MONITORLOOP;
                            default:
                                Log.w(TAG, "SimpleCameraEventObserver: Unexpected error: "
                                        + errorCode);
//                                fireResponseErrorListener();
                                break MONITORLOOP; // end monitoring.
                        }
                        // CameraSettings (exposure, silent shooting etc)
                        CameraSettings camSettings = findCameraSettings(replyJson);
                        if (camSettings != null) {
                            mCameraSettings.updateSettings(camSettings);
                            mListeners.forEach(l -> l.onCameraSettingsChange(mCameraSettings));
                        }

                        // CameraStatus
                        String cameraStatus = findCameraStatus(replyJson);
                        Log.d(TAG, "getEvent cameraStatus: " + cameraStatus);
                        if (cameraStatus != null && !cameraStatus.equals(mCameraStatus)) {
                            mCameraStatus = cameraStatus;
                            mListeners.forEach(l -> l.onCameraStatusChange(mCameraStatus));
                        }
                        // Bulb shooting
                        String bulbShootingUrl = findBulbShootingUrl(replyJson);
                        if (bulbShootingUrl != null) {
                            mListeners.forEach(l -> l.onBulbShootingUrlChange(bulbShootingUrl));
                        }
                    } catch (IOException e) {
                        // Occurs when the server is not available now.
                        Log.d(TAG, "getEvent timeout by client trigger." + e);
//                        fireResponseErrorListener();
//                        break MONITORLOOP;
                        // do nothing todo this forces an infinite loop of timeouts if the camera is inactive
                    } catch (JSONException e) {
                        Log.w(TAG, "getEvent: JSON format error. " + e.getMessage());
//                        fireResponseErrorListener();
                        break MONITORLOOP;
                    }

                    firstCall = false;
                } // MONITORLOOP end.
                Log.d(TAG, "Monitoring events ended");
                mWhileEventMonitoring = false;
            }
        }.start();

        return true;
    }

    private CameraSettings findCameraSettings(JSONObject replyJson) {
        // Keys: (iso: 29, aperture: 27, shutter: 32)
        try {
            String iso = null;
            String aperture = null;
            String shutter = null;
            JSONArray resultsObj = replyJson.getJSONArray("result");
            if (!resultsObj.isNull(29)) {
                JSONObject bulbShootingObj = resultsObj.getJSONObject(29);
                String type = bulbShootingObj.getString("type");
                if ("isoSpeedRate".equals(type)) {
                    iso = bulbShootingObj.getString("currentIsoSpeedRate");
                } else {
                    Log.w(TAG, "Event reply: Illegal Index (29) " + type);
                }
            }
            if (!resultsObj.isNull(27)) {
                JSONObject bulbShootingObj = resultsObj.getJSONObject(27);
                String type = bulbShootingObj.getString("type");
                if ("fNumber".equals(type)) {
                    aperture = bulbShootingObj.getString("currentFNumber");
                } else {
                    Log.w(TAG, "Event reply: Illegal Index (27) " + type);
                }
            }
            if (!resultsObj.isNull(32)) {
                JSONObject bulbShootingObj = resultsObj.getJSONObject(32);
                String type = bulbShootingObj.getString("type");
                if ("shutterSpeed".equals(type)) {
                    shutter = bulbShootingObj.getString("currentShutterSpeed");
                } else {
                    Log.w(TAG, "Event reply: Illegal Index (32) " + type);
                }
            }
            return new CameraSettings(iso, aperture, shutter);
        } catch(JSONException e) {
            return null;
        }
    }

    private String findBulbShootingUrl(JSONObject replyJson) {
        try {
            String bulbShootingUrl = null;
            int indexOfBulbShootingUrl = 65;
            JSONArray resultsObj = replyJson.getJSONArray("result");
            if (!resultsObj.isNull(indexOfBulbShootingUrl)) {
                JSONObject bulbShootingObj = resultsObj.getJSONObject(indexOfBulbShootingUrl);
                String type = bulbShootingObj.getString("type");
                if ("bulbShooting".equals(type)) {
                    bulbShootingUrl = bulbShootingObj.getJSONArray("bulbShootingUrl").getJSONObject(0).getString("postviewUrl");
                } else {
                    Log.w(TAG, "Event reply: Illegal Index (1: bulbShootingUrl) " + type);
                }
            }
            return bulbShootingUrl;
        } catch(JSONException e) {
            return null;
        }
    }

    /**
     * Finds and extracts an error code from reply JSON data.
     *
     * @param replyJson
     * @return
     * @throws JSONException
     */
    private static int findErrorCode(JSONObject replyJson) throws JSONException {
        int code = 0; // 0 means no error.
        if (replyJson.has("error")) {
            JSONArray errorObj = replyJson.getJSONArray("error");
            code = errorObj.getInt(0);
        }
        return code;
    }

    /**
     * Finds and extracts a value of Camera Status from reply JSON data. As for
     * getEvent v1.0, results[1] => "cameraStatus"
     *
     * @param replyJson
     * @return
     * @throws JSONException
     */
    private static String findCameraStatus(JSONObject replyJson) throws JSONException {
        String cameraStatus = null;
        int indexOfCameraStatus = 1;
        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexOfCameraStatus)) {
            JSONObject cameraStatusObj = resultsObj.getJSONObject(indexOfCameraStatus);
            String type = cameraStatusObj.getString("type");
            if ("cameraStatus".equals(type)) {
                cameraStatus = cameraStatusObj.getString("cameraStatus");
            } else {
                Log.w(TAG, "Event reply: Illegal Index (1: CameraStatus) " + type);
            }
        }
        return cameraStatus;
    }

    /**
     * Requests to release resource.
     */
    public void release() {
        mWhileEventMonitoring = false;
        mIsActive = false;
    }

    public void activate() {
        mIsActive = true;
    }

}
