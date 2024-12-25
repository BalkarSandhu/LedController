package com.dadhwal.LedController.controller.responses;

import java.util.List;

public class SearchTerminal {
    private String aliasName;
    private int ftpPort;
    private boolean hasPassWord;
    private int height;
    private int ignoreTime;
    private String ip;
    private String key;
    private boolean logined;
    private List<String> loginedUsernames;
    private String password;
    private String platform;
    private boolean privacy;
    private String productName;
    private String sn; // Serial number
    private int syssetFtpPort;
    private int syssetTcpPort;
    private int tcpPort;
    private int terminalState;
    private int width;

    // Getters and Setters
    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public int getFtpPort() {
        return ftpPort;
    }

    public void setFtpPort(int ftpPort) {
        this.ftpPort = ftpPort;
    }

    public boolean isHasPassWord() {
        return hasPassWord;
    }

    public void setHasPassWord(boolean hasPassWord) {
        this.hasPassWord = hasPassWord;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getIgnoreTime() {
        return ignoreTime;
    }

    public void setIgnoreTime(int ignoreTime) {
        this.ignoreTime = ignoreTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isLogined() {
        return logined;
    }

    public void setLogined(boolean logined) {
        this.logined = logined;
    }

    public List<String> getLoginedUsernames() {
        return loginedUsernames;
    }

    public void setLoginedUsernames(List<String> loginedUsernames) {
        this.loginedUsernames = loginedUsernames;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getSyssetFtpPort() {
        return syssetFtpPort;
    }

    public void setSyssetFtpPort(int syssetFtpPort) {
        this.syssetFtpPort = syssetFtpPort;
    }

    public int getSyssetTcpPort() {
        return syssetTcpPort;
    }

    public void setSyssetTcpPort(int syssetTcpPort) {
        this.syssetTcpPort = syssetTcpPort;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public int getTerminalState() {
        return terminalState;
    }

    public void setTerminalState(int terminalState) {
        this.terminalState = terminalState;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "TerminalResponse{" +
                "aliasName='" + aliasName + '\'' +
                ", ftpPort=" + ftpPort +
                ", hasPassWord=" + hasPassWord +
                ", height=" + height +
                ", ignoreTime=" + ignoreTime +
                ", ip='" + ip + '\'' +
                ", key='" + key + '\'' +
                ", logined=" + logined +
                ", loginedUsernames=" + loginedUsernames +
                ", password='" + password + '\'' +
                ", platform='" + platform + '\'' +
                ", privacy=" + privacy +
                ", productName='" + productName + '\'' +
                ", sn='" + sn + '\'' +
                ", syssetFtpPort=" + syssetFtpPort +
                ", syssetTcpPort=" + syssetTcpPort +
                ", tcpPort=" + tcpPort +
                ", terminalState=" + terminalState +
                ", width=" + width +
                '}';
    }
}
