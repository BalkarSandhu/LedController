package com.dadhwal.LedController.LedSDK.Ethernet;

import java.util.List;

public class EthernetData {
    private int scopeId;
    private String name;
    private boolean dhcp;
    private String ip;
    private String mask;
    private String gateWay;
    private List<String> dns;

    public EthernetData(int scopeId, String name, boolean dhcp, String ip, String mask, String gateWay, List<String> dns) {
        this.scopeId = scopeId;
        this.name = name;
        this.dhcp = dhcp;
        this.ip = ip;
        this.mask = mask;
        this.gateWay = gateWay;
        this.dns = dns;
    }

    public int getScopeId() {
        return scopeId;
    }

    public void setScopeId(int scopeId) {
        this.scopeId = scopeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDhcp() {
        return dhcp;
    }

    public void setDhcp(boolean dhcp) {
        this.dhcp = dhcp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getGateWay() {
        return gateWay;
    }

    public void setGateWay(String gateWay) {
        this.gateWay = gateWay;
    }

    public List<String> getDns() {
        return dns;
    }

    public void setDns(List<String> dns) {
        this.dns = dns;
    }
}
