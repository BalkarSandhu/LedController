package com.dadhwal.LedController.LedSDK.Ethernet;

import java.util.List;

public class EthernetTaskInfo {
    private List<EthernetData> ethernets;

    public EthernetTaskInfo(List<EthernetData> ethernets){
        this.ethernets = ethernets;
    }

    public List<EthernetData> getEthernets() {
        return ethernets;
    }

    public void setEthernets(List<EthernetData> ethernets) {
        this.ethernets = ethernets;
    }
}
