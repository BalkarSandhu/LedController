package com.dadhwal.LedController.LedSDK.Editor.Components;

import java.util.List;

public class Widget {
    private List<Constraint> constraints;
    private int duration;
    private MetaData metadata;
    private String name;
    private String type;

    public Widget(List<Constraint> constraints, int duration, MetaData metaData, String name, String type){
        this.constraints=constraints;
        this.duration=duration;
        this.metadata=metaData;
        this.name=name;
        this.type=type;
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

    public void setMetadata(MetaData metadata) {
        this.metadata = metadata;
    }

    public MetaData getMetadata() {
        return metadata;
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
}
