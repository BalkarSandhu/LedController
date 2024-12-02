package com.dadhwal.LedController.LedSDK;

public class LoginReq {
    private String sn;
    private String ip;
    private String username;
    private String password;
    private int loginType;
    private int rememberPwd;

    public LoginReq(String sn, String username, String password,int loginType, int rememberPwd){
        this.sn=sn;
        this.username=username;
        this.password=password;
        this.loginType=loginType;
        this.rememberPwd=rememberPwd;
    }

    public LoginReq(String sn, String ip, String username, String password,int loginType, int rememberPwd){
        this.sn=sn;
        this.ip=ip;
        this.username=username;
        this.password=password;
        this.loginType=loginType;
        this.rememberPwd=rememberPwd;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSn() {
        return sn;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRememberPwd(int rememberPwd) {
        this.rememberPwd = rememberPwd;
    }

    public int getRememberPwd() {
        return rememberPwd;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public int getLoginType() {
        return loginType;
    }
}
