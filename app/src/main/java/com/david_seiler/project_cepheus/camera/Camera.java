package com.david_seiler.project_cepheus.camera;

import com.david_seiler.project_cepheus.camera.sony.Model;

// TODO: camera will eventually replace and supercede all the functionality of ServerDevice
public class Camera {
    public enum Type {
        SONY,
        CANON,
        NIKON
    }

    public Camera(Type type) {
        this.type = type;
    }
    private final Type type;
    private Model model;
    private boolean connected;
    private String status;

    public Type getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
