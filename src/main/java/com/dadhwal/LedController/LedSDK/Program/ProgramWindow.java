package com.dadhwal.LedController.LedSDK.Program;

public class ProgramWindow {
    private int height;
    private int width;
    private int left;
    private int top;
    private int zindex;
    private int index;

    public ProgramWindow(int height, int width, int left, int top, int zindex, int index){
        this.height=height;
        this.width=width;
        this.left=left;
        this.top=top;
        this.zindex=zindex;
        this.index=index;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getLeft() {
        return left;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getTop() {
        return top;
    }

    public void setZindex(int zindex) {
        this.zindex = zindex;
    }

    public int getZindex() {
        return zindex;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
