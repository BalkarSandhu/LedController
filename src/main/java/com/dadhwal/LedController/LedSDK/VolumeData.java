package com.dadhwal.LedController.LedSDK;

public class VolumeData {
    private double ratio;

    public VolumeData(double ratio){
        this.ratio=ratio;
    }

    public double getVolumeData(){
        return this.ratio;
    }

    public void setVolumeData(double ratio){
        this.ratio=ratio;
    }
}
