package com.david_seiler.project_cepheus.intervalometer;

import com.david_seiler.project_cepheus.camera.CameraSettings;

public class Sequence {
    private long delay;
    private long min_interval;
    private long exposure_length;
    private int num_frames;

    private CameraSettings cameraSettings;
    private String[] frame_urls;
    private boolean bulb = false;

    public Sequence(long delay, long min_interval, int num_frames) {
        this.delay = delay;
        this.min_interval = min_interval;
        this.num_frames = num_frames;
    }

    public Sequence(long delay, long min_interval, int num_frames, long exposure_length, boolean bulb) {
        this(delay, min_interval, num_frames);
        this.exposure_length = exposure_length;
        this.bulb = true;
    }

    public interface SequenceListener {
        void onFrameComplete(String url);
        void onSequenceComplete(String[] urls);
    }

    public long getDelay() {
        return delay;
    }

    public long getMin_interval() {
        return min_interval;
    }

    public int getNum_frames() {
        return num_frames;
    }

    public long getExposureLength() {
        return exposure_length;
    }

    public boolean isBulb() {
        return bulb;
    }

}
