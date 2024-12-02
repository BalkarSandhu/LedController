package com.dadhwal.LedController.LedSDK;

import com.dadhwal.LedController.LedSDK.Ethernet.EthernetTaskInfo;
import com.dadhwal.LedController.LedSDK.Screen.ScreenInfo;

public class StringFormatter {

    private final String sn;
    EthernetTaskInfo taskInfo;

    public StringFormatter(String sn){
        this.sn=sn;
    }

    public StringFormatter(String sn, double volumeRatio){
        this.sn=sn;
        VolumeData volumeData = new VolumeData(volumeRatio);
    }

    public StringFormatter(String sn, EthernetTaskInfo taskInfo){
        this.sn=sn;
        this.taskInfo=taskInfo;
    }
}


