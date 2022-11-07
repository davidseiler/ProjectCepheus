package com.david_seiler.project_cepheus.camera;

import android.content.Context;

import java.util.ArrayList;

public abstract class CameraStatusObserver {

    protected Context context;

    protected CameraApi mCameraApi;

    protected ArrayList<CameraStatusListener> mListeners = new ArrayList<>();

    protected boolean mWhileEventMonitoring = false;

    protected boolean mIsActive = false;

    // Current camera settings
    protected CameraSettings mCameraSettings;

    // Current Camera Status value.
    protected String mCameraStatus;

    // Current Liveview Status value.
    protected boolean mLiveviewStatus;

    // Current Shoot Mode value.
    protected String mShootMode;

    // Constructor
    protected CameraStatusObserver(Context context, CameraApi cameraApi) {
        this.context = context;
        this.mCameraApi = cameraApi;
        this.mCameraSettings = new CameraSettings();
    }

    public interface CameraStatusListener {
        void onCameraStatusChange(String status);
        void onBulbShootingUrlChange(String url);
        void onCameraSettingsChange(CameraSettings cameraSettings);
    }

    public abstract void activate();

    public abstract void release();

    public abstract boolean start();

    public void addCameraStatusListener(CameraStatusListener listener) {
        this.mListeners.add(listener);
    }

    public String getCameraStatus() {
        return mCameraStatus;
    }
}
