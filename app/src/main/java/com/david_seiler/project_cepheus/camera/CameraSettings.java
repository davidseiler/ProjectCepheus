package com.david_seiler.project_cepheus.camera;

public class CameraSettings {
    private String iso;
    private String aperture;
    private String shutterSpeed;
    private boolean silentShooting;

    public CameraSettings(String iso, String aperture, String shutterSpeed) {
        this.iso = iso;
        this.aperture = aperture;
        this.shutterSpeed = shutterSpeed;
    }

    public CameraSettings() {
        this.iso = null;
        this.aperture = null;
        this.shutterSpeed = null;
    }

    public String getIso() {
        return iso;
    }

    public String getAperture() {
        return aperture;
    }

    public String getShutterSpeed() {
        return shutterSpeed;
    }

    public void updateSettings(String iso, String aperture, String shutterSpeed) {
        this.iso = iso;
        this.aperture = aperture;
        this.shutterSpeed = shutterSpeed;
    }

    public void updateSettings(CameraSettings cmp) {
        if (cmp.getIso() != null) {
            this.iso = cmp.getIso();
        }
        if (cmp.getAperture() != null) {
            this.aperture = cmp.getAperture();
        }
        if (cmp.getShutterSpeed() != null) {
            this.shutterSpeed = cmp.getShutterSpeed();
        }
    }
}
