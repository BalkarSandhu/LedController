package com.dadhwal.LedController.LedSDK.Screen;

import java.util.List;

public class ScreenInfo {
    private String sn;
    private String productName;
    private int width;
    private int height;
    private int ftpPort; // default 16602
    private int syssetFtpPort; // 16604
    private int syssetTcpPort; // 16605
    private int tcpPort; // 16603

    public ScreenInfo(String sn, String productName, int width, int height, int ftpPort, int syssetFtpPort, int tcpPort, int syssetTcpPort) {
        this.sn = sn;
        this.productName = productName;
        this.width = width;
        this.height = height;
        this.ftpPort = ftpPort;
        this.tcpPort = tcpPort;
        this.syssetFtpPort = syssetFtpPort;
        this.syssetTcpPort = syssetTcpPort;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFtpPort() {
        return ftpPort;
    }

    public void setFtpPort(int ftpPort) {
        this.ftpPort = ftpPort;
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

    @Override
    public String toString() {
        return "ScreenInfo{" +
                "sn='" + sn + '\'' +
                ", productName='" + productName + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", ftpPort=" + ftpPort +
                ", syssetFtpPort=" + syssetFtpPort +
                ", syssetTcpPort=" + syssetTcpPort +
                ", tcpPort=" + tcpPort +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ScreenInfo that = (ScreenInfo) obj;

        return width == that.width && height == that.height && ftpPort == that.ftpPort &&
                syssetFtpPort == that.syssetFtpPort && syssetTcpPort == that.syssetTcpPort &&
                tcpPort == that.tcpPort && sn.equals(that.sn) && productName.equals(that.productName);
    }

    @Override
    public int hashCode() {
        int result = sn.hashCode();
        result = 31 * result + productName.hashCode();
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + ftpPort;
        result = 31 * result + syssetFtpPort;
        result = 31 * result + syssetTcpPort;
        result = 31 * result + tcpPort;
        return result;
    }
}
