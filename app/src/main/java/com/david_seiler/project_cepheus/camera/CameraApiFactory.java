package com.david_seiler.project_cepheus.camera;

import com.david_seiler.project_cepheus.camera.sony.old.ServerDevice;
import com.david_seiler.project_cepheus.camera.sony.SonyBetaSdkApi;

public class CameraApiFactory {
    public static CameraApi getCameraApi(Camera camera, String cameraUrl, ServerDevice targetDevice){
        if (camera.getType() == Camera.Type.SONY) {
            return new SonyBetaSdkApi(camera, cameraUrl, targetDevice);
        }
        // Add new methods here when Canon api is setup
        return null;
    }
}
