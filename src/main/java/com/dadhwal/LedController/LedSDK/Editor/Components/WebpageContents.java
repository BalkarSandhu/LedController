package com.dadhwal.LedController.LedSDK.Editor.Components;

import java.util.List;

public class WebpageContents {
    private List<WebpageWidget> widgets;

    public WebpageContents(List<WebpageWidget> widgets){
        this.widgets=widgets;
    }

    public void setWidgets(List<WebpageWidget> widgets) {
        this.widgets = widgets;
    }

    public List<WebpageWidget> getWidgets() {
        return widgets;
    }
}
