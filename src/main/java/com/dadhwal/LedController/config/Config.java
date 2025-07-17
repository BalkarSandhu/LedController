package com.dadhwal.LedController.config;

public class Config {
    private String controllerIp;
    private String wbFile;
    private String baseurl;

    // Getters and setters
    public String getControllerIp() {
        return controllerIp;
    }

    public void setControllerIp(String newIp) {
        this.controllerIp = newIp;
    }

    public String getWbFile() {
        return wbFile;
    }

    public void setWbFile(String newWbFile) {
        this.wbFile = newWbFile;
    }

    public String getBaseUrl() {
        return baseurl;
    }

    public void setBaseUrl(String newBaseUrl) {
        this.baseurl = newBaseUrl;
    }

     @Override
    public String toString() {
        return "{" +
                "controllerIp='" + controllerIp + '\'' +
                ", wbFile='" + wbFile + '\'' +
                ", baseurl='" + baseurl + '\'' +
                '}';
    }
}