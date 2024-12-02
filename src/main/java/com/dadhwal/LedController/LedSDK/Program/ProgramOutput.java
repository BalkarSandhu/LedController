package com.dadhwal.LedController.LedSDK.Program;

public class ProgramOutput {
    private int programID;
    private String outPutPath;

    public ProgramOutput(int programID, String outPutPath){
        this.programID=programID;
        this.outPutPath=outPutPath;
    }

    public void setProgramID(int programID) {
        this.programID = programID;
    }

    public int getProgramID() {
        return programID;
    }

    public void setOutPutPath(String outPutPath) {
        this.outPutPath = outPutPath;
    }

    public String getOutPutPath() {
        return outPutPath;
    }
}
