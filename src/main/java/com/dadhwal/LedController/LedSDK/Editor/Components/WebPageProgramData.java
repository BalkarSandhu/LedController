package com.dadhwal.LedController.LedSDK.Editor.Components;

public class WebPageProgramData {
    private int programID;
    private int pageID;
    private WebpageInfo pageInfo;

    public  WebPageProgramData(int programID, int pageID, WebpageInfo pageInfo){
        this.programID=programID;
        this.pageID=pageID;
        this.pageInfo=pageInfo;
    }

    public void setProgramID(int programID) {
        this.programID = programID;
    }

    public int getProgramID() {
        return programID;
    }

    public void setPageID(int pageID) {
        this.pageID = pageID;
    }

    public int getPageID() {
        return pageID;
    }

    public void setPageInfo(WebpageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public WebpageInfo getPageInfo() {
        return pageInfo;
    }
}

