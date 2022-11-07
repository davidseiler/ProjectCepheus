package com.david_seiler.project_cepheus.camera;

import com.david_seiler.project_cepheus.camera.sony.old.ServerDevice;
import com.david_seiler.project_cepheus.intervalometer.Sequence;

import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;


public abstract class CameraApi implements CameraStatusObserver.CameraStatusListener {
    // This class should be responsible for logging and output validation
    // The extensions of this class will implement the specifics of contacting the device
    private static final String TAG = CameraApi.class.getSimpleName();

    protected Camera camera;
    protected String cameraUrl;
    protected ServerDevice targetDevice;

    protected int requestNumber = 1;


    // TODO: must be initialized with a connection url and be full ready to use.
    public CameraApi(Camera camera, String cameraUrl, ServerDevice targetDevice) {
        this.camera = camera;
        this.cameraUrl = cameraUrl;
        this.targetDevice = targetDevice;   // TODO: this will be replaced by camera once fully implemented
    }

    public abstract JSONObject getAvailableApiList() throws IOException;

    public abstract JSONObject getApplicationInfo() throws IOException;

    public abstract JSONObject getCameraStatus(boolean polled) throws IOException;

    public abstract JSONObject getIso() throws IOException;

    public abstract JSONObject setIso(String value) throws IOException;

    public abstract JSONObject getFNumber() throws IOException;

    public abstract JSONObject setFNumber(String value) throws IOException;

    public abstract JSONObject getShutterSpeed() throws IOException;

    public abstract JSONObject setShutterSpeed(String value) throws IOException;

    public abstract String takePhoto();

    public abstract String takeBulbPhoto(long mTime);

    public abstract ArrayList<String> startSequence(Sequence seq);

    public abstract JSONObject downloadImage() throws IOException;

    public abstract JSONObject startLiveView() throws IOException;

    public abstract JSONObject stopLiveView() throws IOException;

    public abstract String testMessage();

}
