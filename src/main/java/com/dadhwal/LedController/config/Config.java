package com.dadhwal.LedController.config;

import org.springframework.stereotype.Component;

@Component
public class Config {
    private String controllerIp;
    private String wbFile;
    private String baseUrl;

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
        return baseUrl;
    }

    public void setBaseUrl(String newBaseUrl) {
        this.baseUrl = newBaseUrl;
    }

     @Override
    public String toString() {
        return "{" +
                "controllerIp='" + controllerIp + '\'' +
                ", wbFile='" + wbFile + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                '}';
    }
}