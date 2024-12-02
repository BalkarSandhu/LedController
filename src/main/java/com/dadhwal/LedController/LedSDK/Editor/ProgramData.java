package com.dadhwal.LedController.LedSDK.Editor;

public class ProgramData {
    private int programID;
    private int pageID;
    private PageInfo pageInfo;

    public  ProgramData(int programID, int pageID, PageInfo pageInfo){
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

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }
}
