package com.dadhwal.LedController.controller;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private String baseurl;
    private String controllerIp;
    private String wbFile;

    // Getters and Setters
    public String getBaseurl() {
        return baseurl;
    }

    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

    public String getControllerIp() {
        return controllerIp;
    }

    public void setControllerIp(String controllerIp) {
        this.controllerIp = controllerIp;
    }

    public String getWbFile() {
        return wbFile;
    }

    public void setWbFile(String wbFile) {
        this.wbFile = wbFile;
    }
}
