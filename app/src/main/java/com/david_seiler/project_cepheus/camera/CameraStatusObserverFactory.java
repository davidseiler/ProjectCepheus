package com.david_seiler.project_cepheus.camera;

import android.content.Context;

import com.david_seiler.project_cepheus.camera.sony.SonyCameraStatusObserver;

public class CameraStatusObserverFactory {
    public static CameraStatusObserver getCameraStatusObserver(Context context, Camera camera, CameraApi cameraApi){
        if (camera.getType() == Camera.Type.SONY) {
            return new SonyCameraStatusObserver(context, cameraApi);
        }
        // Add new methods here when Canon api is setup
        return null;
    }
}
