package com.dadhwal.LedController.LedSDK.Editor.Components;

import java.util.List;

public class Line {
    private List<Seg> segs;

    public Line(List<Seg> segs){
        this.segs=segs;
    }

    public void setSegs(List<Seg> segs) {
        this.segs = segs;
    }

    public List<Seg> getSegs() {
        return segs;
    }
}
