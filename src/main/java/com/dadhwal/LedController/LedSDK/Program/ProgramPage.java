package com.dadhwal.LedController.LedSDK.Program;

public class ProgramPage {
    private String name;
    private int width;
    private int height;
    private int tplID;
    private ProgramWindow winInfo;

    public ProgramPage(String name, int width, int height, int tplID, ProgramWindow winInfo){
        this.name=name;
        this.width=width;
        this.height=height;
        this.tplID=tplID;
        this.winInfo=winInfo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setTplID(int tplID) {
        this.tplID = tplID;
    }

    public int getTplID() {
        return tplID;
    }

    public void setWinInfo(ProgramWindow winInfo) {
        this.winInfo = winInfo;
    }

    public ProgramWindow getWinInfo() {
        return winInfo;
    }
}



