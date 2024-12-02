package com.dadhwal.LedController.LedSDK.Editor.Components;

import java.util.List;

public class Constraint {
    private List<String> cron;
    private String endTime;
    private String startTime;

    public Constraint(List<String> cron, String endTime, String startTime){
        this.cron=cron;
        this.endTime=endTime;
        this.startTime=startTime;
    }

    public void setCron(List<String> cron) {
        this.cron = cron;
    }

    public List<String> getCron() {
        return cron;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime() {
        return startTime;
    }
}
