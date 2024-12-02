package com.dadhwal.LedController.LedSDK.Program;

public class ProgramTransfer {
    private String sn;
    private String iconPath;
    private String iconName;
    private String programName;
    private ProgramFilePaths sendProgramFilePaths;
    private String deviceIdentifier;
    private boolean startPlayAfterTransferred;
    private boolean insertPlay;

    public ProgramTransfer(String sn, String iconPath, String iconName,
                           String programPath, Object mediasPath, String programName, String deviceIdentifier,
                           boolean startPlayAfterTransferred, boolean insertPlay){
        this.sn=sn;
        this.iconPath=iconPath;
        this.iconName=iconName;
        this.sendProgramFilePaths=new ProgramFilePaths(programPath,mediasPath);
        this.programName=programName;
        this.deviceIdentifier=deviceIdentifier;
        this.startPlayAfterTransferred=startPlayAfterTransferred;
        this.insertPlay=insertPlay;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSn() {
        return sn;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getIconName() {
        return iconName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramName() {
        return programName;
    }

    public void setSendProgramFilePaths(String programPath, Object mediasPath) {
        this.sendProgramFilePaths = new ProgramFilePaths(programPath,mediasPath);;
    }

    public void setDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public void setStartPlayAfterTransferred(boolean startPlayAfterTransferred) {
        this.startPlayAfterTransferred = startPlayAfterTransferred;
    }

    public boolean isStartPlayAfterTransferred() {
        return startPlayAfterTransferred;
    }

    public void setInsertPlay(boolean insertPlay) {
        this.insertPlay = insertPlay;
    }

    public boolean isInsertPlay() {
        return insertPlay;
    }
}

class ProgramFilePaths{
    private String programPath;
    private Object mediasPath;

    public ProgramFilePaths(String programPath, Object mediasPath){
        this.programPath=programPath;
        this.mediasPath=mediasPath;
    }

    public void setProgramPath(String programPath) {
        this.programPath = programPath;
    }

    public String getProgramPath() {
        return programPath;
    }

    public void setMediasPath(Object mediasPath) {
        this.mediasPath = mediasPath;
    }

    public Object getMediasPath() {
        return mediasPath;
    }
}
