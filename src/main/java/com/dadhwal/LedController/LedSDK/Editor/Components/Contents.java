package com.dadhwal.LedController.LedSDK.Editor.Components;

import java.util.List;

public class Contents {
    private List<Widget> widgets;

    public Contents(List<Widget> widgets){
        this.widgets=widgets;
    }

    public void setWidgets(List<Widget> widgets) {
        this.widgets = widgets;
    }

    public List<Widget> getWidgets() {
        return widgets;
    }
}
