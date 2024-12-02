package com.dadhwal.LedController.LedSDK.Editor.Components;

public class Effects {
    private String animation;
    private int speed;

    public Effects(String animation, int speed){
        this.animation=animation;
        this.speed=speed;
    }

    public void setAnimation(String animation) {
        this.animation = animation;
    }

    public String getAnimation() {
        return animation;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }
}
