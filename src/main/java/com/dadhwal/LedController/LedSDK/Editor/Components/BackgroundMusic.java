package com.dadhwal.LedController.LedSDK.Editor.Components;

public class BackgroundMusic {
    private int duration;
    private boolean isTextSync;

    public BackgroundMusic(int duration, boolean isTextSync){
        this.duration=duration;
        this.isTextSync=isTextSync;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setTextSync(boolean textSync) {
        isTextSync = textSync;
    }

    public boolean isTextSync() {
        return isTextSync;
    }
}
