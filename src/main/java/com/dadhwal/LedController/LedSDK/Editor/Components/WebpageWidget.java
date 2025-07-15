package com.dadhwal.LedController.LedSDK.Editor.Components;

import java.util.List;

public class WebpageWidget {
    private List<Constraint> constraints;
    private int duration;
    private String dataSource;
    private String originalDataSource;
    private String name;
    private String type;
    private boolean isSupportSpecialEffects;

    public WebpageWidget(List<Constraint> constraints, int duration, String dataSource, String originalDataSource, String name, String type, boolean isSupportSpecialEffects) {
        this.constraints=constraints;
        this.duration=duration;
        this.dataSource=dataSource;
        this.originalDataSource=originalDataSource;
        this.name=name;
        this.type=type;
        this.isSupportSpecialEffects=isSupportSpecialEffects;
    }

    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setOriginalDataSource(String originalDataSource) {
        this.originalDataSource = originalDataSource;
    }

    public String getOriginalDataSource() {
        return originalDataSource;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setIsSupportSpecialEffects(boolean isSupportSpecialEffects) {
        this.isSupportSpecialEffects = isSupportSpecialEffects;
    }

    public boolean getIsSupportSpecialEffects() {
        return isSupportSpecialEffects;
    }
}
