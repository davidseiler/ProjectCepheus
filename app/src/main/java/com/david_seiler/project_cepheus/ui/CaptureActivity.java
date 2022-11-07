package com.david_seiler.project_cepheus.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.david_seiler.project_cepheus.R;
import com.david_seiler.project_cepheus.astrometrics.PlateSolver;
import com.david_seiler.project_cepheus.camera.Camera;
import com.david_seiler.project_cepheus.camera.CameraApi;
import com.david_seiler.project_cepheus.camera.CameraApiFactory;
import com.david_seiler.project_cepheus.camera.CameraSettings;
import com.david_seiler.project_cepheus.camera.CameraStatusObserver;
import com.david_seiler.project_cepheus.camera.CameraStatusObserverFactory;
import com.david_seiler.project_cepheus.intervalometer.Sequence;
import com.david_seiler.project_cepheus.camera.sony.old.SampleApplication;
import com.david_seiler.project_cepheus.camera.sony.old.ServerDevice;
import com.david_seiler.project_cepheus.camera.sony.old.SimpleRemoteApi;
import com.david_seiler.project_cepheus.camera.sony.old.SimpleStreamSurfaceView;
import com.david_seiler.project_cepheus.camera.sony.utils.DisplayHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CaptureActivity extends AppCompatActivity implements CameraStatusObserver.CameraStatusListener {
    private static final String TAG = CaptureActivity.class.getSimpleName();
    // TODO: Do all the setup that is required to work with the API
    // TODO: API calls like take photo/start sequence need to handle camera state properly through the use of event observer (all sony camera's support getEvent1.0)
    // TODO: handle orientation changes
    // TODO: thread safe operations may need to be updated
    // TODO: Handle app exit/lifecycle for liveview
    // TODO: setup all the current stuff that works into the extracted classes that can be simply replaced to work with different camera models

    private Button toggleLiveViewButton;
    private Button takeSinglePhotoButton;
    private Button startSequenceButton;
    private Button setISOButton;
    private Button setApertureButton;
    private Button setShutterSpeedButton;
    private Button plateSolveButton;

    private CameraSettings currentCameraSettings;

    private TextView currentCameraSettingsText;

    private SimpleStreamSurfaceView liveView;

    private ImageView lastImagePreview;

    private String pendingImageUrl = null;
    private boolean mEnableImagePreview = true;

    // TODO: below has been directly ripped from SampleCameraActivity and needs refactoring
    private ServerDevice mTargetServer;

    private SimpleRemoteApi mRemoteApi;
    private CameraApi mCameraApi;
    private Camera mCamera;

    private SimpleStreamSurfaceView mLiveviewSurface;

    private CameraStatusObserver cameraStatusObserver;

    private final Set<String> mAvailableCameraApiSet = new HashSet<String>();

    private final Set<String> mSupportedApiSet = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        toggleLiveViewButton = findViewById(R.id.toggle_live_view);
        takeSinglePhotoButton = findViewById(R.id.take_single_photo);
        startSequenceButton = findViewById(R.id.start_sequence);
        setISOButton = findViewById(R.id.set_iso);
        setApertureButton = findViewById(R.id.set_aperture);
        setShutterSpeedButton = findViewById(R.id.set_shutter_speed);
        plateSolveButton = findViewById(R.id.plate_solve_button);

        currentCameraSettingsText = findViewById(R.id.camera_settings);

        liveView = findViewById(R.id.surfaceview_liveview);

        lastImagePreview = findViewById(R.id.image_preview);

        // TODO: This is how the camera api should be used once implemented

        SampleApplication app = (SampleApplication) getApplication();
        mTargetServer = app.getTargetServerDevice();
        mRemoteApi = new SimpleRemoteApi(mTargetServer);
        mCamera = new Camera(Camera.Type.SONY);
        mCameraApi = CameraApiFactory.getCameraApi(mCamera, mTargetServer.getDDUrl(), mTargetServer);
//        app.setRemoteApi(mRemoteApi);
        cameraStatusObserver = CameraStatusObserverFactory.getCameraStatusObserver(getApplicationContext(), mCamera, mCameraApi);
//        app.setCameraEventObserver(mEventObserver);
        // TODO: eventObserver needs to be implemented
        currentCameraSettings = new CameraSettings("-1", "-1", "-1");
    }

    @Override
    protected void onResume() {
        super.onResume();
        openConnection();

        cameraStatusObserver.activate();
        mLiveviewSurface = (SimpleStreamSurfaceView) findViewById(R.id.surfaceview_liveview);

        toggleLiveViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Live View toggled from UI");
                if (mLiveviewSurface.getVisibility() != View.VISIBLE) {
                    openConnection();
                    mLiveviewSurface.setVisibility(View.VISIBLE);
                } else {
                    mLiveviewSurface.setVisibility(View.GONE);
                    mLiveviewSurface.stop();
                    stopLiveview();
                }

            }
        });

        takeSinglePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        startSequenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraBusyUIUpdate();
                mEnableImagePreview = false;
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Sequence seq = new Sequence(500, 0, 15);
                            Sequence bulbSeq = new Sequence(500, 0, 8, 200, true);
                            ArrayList<String> results = mCameraApi.startSequence(seq);
                            Log.d("WTF", "what happens here?" + results);
                            pendingImageUrl = results.get(seq.getNum_frames() - 1); // only display the last image of the sequence for now
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEnableImagePreview = true;
                                    Glide.with(CaptureActivity.this).load(pendingImageUrl).into(lastImagePreview);
                                    lastImagePreview.setVisibility(View.VISIBLE);
                                    cameraFreeUIUpdate();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        setISOButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Implement
                // TODO: extract into classes
                System.out.println("LOG RESULTS FROM FUNCTION CALL");
            }
        });

        setApertureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Implement
                // TODO: extract into classes
                System.out.println("LOG RESULTS FROM FUNCTION CALL");
            }
        });

        setShutterSpeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Implement
                // TODO: extract into classes
                System.out.println("LOG RESULTS FROM FUNCTION CALL");
            }
        });

        plateSolveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("LOG RESULTS FROM FUNCTION CALL");

                // TODO: make this automatically connect to data or regular wifi then reconnect back to the phone after.
                new Thread() {
                    @Override
                    public void run() {
                        PlateSolver x = new PlateSolver(CaptureActivity.this);
                        String url = x.plateSolve("https://storage.googleapis.com/portfolio-image-repo/photos/astro/Just%20background%20extraction.jpeg");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(CaptureActivity.this).load(url).into(lastImagePreview);
                                lastImagePreview.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }.start();
            }
        });

        lastImagePreview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                lastImagePreview.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
//        closeConnection();
        // TODO this needs to be implemented to stop all the resource loss
        Log.d(TAG, "onPause() completed.");
    }

    public void takePhoto() {
        takeSinglePhotoButton.setEnabled(false);
        new Thread() {
            @Override
            public void run() {
                try {
                    if (Objects.equals(currentCameraSettings.getShutterSpeed(), "BULB")) {
                        pendingImageUrl = mCameraApi.takeBulbPhoto(3000);
                    } else {
                        pendingImageUrl = mCameraApi.takePhoto();
                    }
                    Log.d(TAG, " " + cameraStatusObserver.getCameraStatus() + pendingImageUrl);
                } finally {
                    DisplayHelper.setProgressIndicator(CaptureActivity.this, false);
                }
            }
        }.start();
    }

    /**
     * TODO: legacy, Open connection to the camera device to start monitoring Camera events
     * and showing liveview.
     */
    private void openConnection() {

        cameraStatusObserver.addCameraStatusListener(this);
        cameraStatusObserver.addCameraStatusListener(mCameraApi);
        new Thread() {

            @Override
            public void run() {

                try {
                    JSONObject replyJson = null;

                    // getAvailableApiList
                    replyJson = mCameraApi.getAvailableApiList();
                    loadAvailableCameraApiList(replyJson);

                    // check version of the server device
                    if (isCameraApiAvailable("getApplicationInfo")) {
                        replyJson = mCameraApi.getApplicationInfo();
                        if (!isSupportedServerVersion(replyJson)) {
                            DisplayHelper.toast(getApplicationContext(), //
                                    R.string.msg_error_non_supported_device);
                            CaptureActivity.this.finish();
                            return;
                        }
                    } else {
                        // never happens;
                        return;
                    }

                    // getEvent start
                    if (isCameraApiAvailable("getEvent")) {
                        Log.d(TAG, "&&&&&&&&&&&&&&&&&&Observer started");
                        cameraStatusObserver.start();
                    }

                    // Liveview start
                    if (isCameraApiAvailable("startLiveview")) {
                        startLiveview();
                    }

                } catch (IOException e) {
                    DisplayHelper.setProgressIndicator(CaptureActivity.this, false);
                    DisplayHelper.toast(getApplicationContext(), R.string.msg_error_connection);
                }
            }
        }.start();

    }

    // TODO: below is the API start sequences and will require refactoring
    /**
     * Retrieve a list of APIs that are available at present.
     *
     * @param replyJson
     */
    private void loadAvailableCameraApiList(JSONObject replyJson) {
        synchronized (mAvailableCameraApiSet) {
            mAvailableCameraApiSet.clear();
            try {
                JSONArray resultArrayJson = replyJson.getJSONArray("result");
                JSONArray apiListJson = resultArrayJson.getJSONArray(0);
                for (int i = 0; i < apiListJson.length(); i++) {
                    mAvailableCameraApiSet.add(apiListJson.getString(i));
                }
            } catch (JSONException e) {
//                Log.w(TAG, "loadAvailableCameraApiList: JSON format error.");
            }
        }
    }

    /**
     * Retrieve a list of APIs that are supported by the target device.
     *
     * @param replyJson
     */
    private void loadSupportedApiList(JSONObject replyJson) {
        synchronized (mSupportedApiSet) {
            try {
                JSONArray resultArrayJson = replyJson.getJSONArray("results");
                for (int i = 0; i < resultArrayJson.length(); i++) {
                    mSupportedApiSet.add(resultArrayJson.getJSONArray(i).getString(0));
                }
            } catch (JSONException e) {
//                Log.w(TAG, "loadSupportedApiList: JSON format error.");
            }
        }
    }

    /**
     * Check if the specified API is available at present. This works correctly
     * only for Camera API.
     *
     * @param apiName
     * @return
     */
    private boolean isCameraApiAvailable(String apiName) {
        boolean isAvailable = false;
        synchronized (mAvailableCameraApiSet) {
            isAvailable = mAvailableCameraApiSet.contains(apiName);
        }
        return isAvailable;
    }

    /**
     * Check if the specified API is supported. This is for camera and avContent
     * service API. The result of this method does not change dynamically.
     *
     * @param apiName
     * @return
     */
    private boolean isApiSupported(String apiName) {
        boolean isAvailable = false;
        synchronized (mSupportedApiSet) {
            isAvailable = mSupportedApiSet.contains(apiName);
        }
        return isAvailable;
    }

    /**
     * Check if the version of the server is supported in this application.
     *
     * @param replyJson
     * @return
     */
    private boolean isSupportedServerVersion(JSONObject replyJson) {
        try {
            JSONArray resultArrayJson = replyJson.getJSONArray("result");
            String version = resultArrayJson.getString(1);
            String[] separated = version.split("\\.");
            int major = Integer.valueOf(separated[0]);
            if (2 <= major) {
                return true;
            }
        } catch (JSONException e) {
        } catch (NumberFormatException e) {
        }
        return false;
    }
    private void startLiveview() {
        if (mLiveviewSurface == null) {
            return;
        }
        new Thread() {
            @Override
            public void run() {

                try {
                    JSONObject replyJson = null;
                    replyJson = mCameraApi.startLiveView();

                    if (!SimpleRemoteApi.isErrorReply(replyJson)) {
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                        if (1 <= resultsObj.length()) {
                            // Obtain liveview URL from the result.
                            final String liveviewUrl = resultsObj.getString(0);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    mLiveviewSurface.start(liveviewUrl, //
                                            new SimpleStreamSurfaceView.StreamErrorListener() {

                                                @Override
                                                public void onError(StreamErrorReason reason) {
                                                    stopLiveview();
                                                }
                                            });
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                } catch (JSONException e) {
                }
            }
        }.start();
    }
    private void stopLiveview() {
        new Thread() {
            @Override
            public void run() {
                try {
                    mCameraApi.stopLiveView();
                } catch (IOException e) {
//                    Log.w(TAG, "stopLiveview IOException: " + e.getMessage());
                }
            }
        }.start();
    }

    private void cameraBusyUIUpdate() {
        takeSinglePhotoButton.setEnabled(false);
    }

    private void cameraFreeUIUpdate() {
        takeSinglePhotoButton.setEnabled(true);
    }

    @Override
    public void onCameraStatusChange(String status) {
        Log.d(TAG, "CameraStatus change updated to: " + status + " " + pendingImageUrl);
        switch (status) {
            case "IDLE":
                if (pendingImageUrl != null && mEnableImagePreview) {
                    // TODO: view hierarchy requires this to be run in the UI thread, probably fix this when I fix threading
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            takeSinglePhotoButton.setEnabled(true);
                            Glide.with(CaptureActivity.this).load(pendingImageUrl).into(lastImagePreview);
                            lastImagePreview.setVisibility(View.VISIBLE);
                        }
                    });
                }
            default:
                // nothing
        }
    }

    @Override
    public void onBulbShootingUrlChange(String url) {
        Log.d(TAG, "bulbShootingUrl change updated to: " + url + " " + pendingImageUrl);
        pendingImageUrl = url;
    }

    @Override
    public void onCameraSettingsChange(CameraSettings cameraSettings) {
        Log.d(TAG, "CameraSettings updated to: " + cameraSettings.toString());
        currentCameraSettings = cameraSettings;
        String cameraSettingsText = String.format("ISO:%s, APERTURE:%s, SHUTTER SPEED:%s\n", currentCameraSettings.getIso(), currentCameraSettings.getAperture(), currentCameraSettings.getShutterSpeed());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentCameraSettingsText.setText(cameraSettingsText);
            }
        });
    }
}
