package com.dadhwal.LedController.LedSDK.Editor.Components;

import java.util.List;

public class WebpageInfo {

    private String name;
    private List<WebpageWidgetContainer> widgetContainers;

    public WebpageInfo(String name, List<WebpageWidgetContainer> widgetContainers){
        this.name=name;
        this.widgetContainers=widgetContainers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setWidgetContainers(List<WebpageWidgetContainer> widgetContainers) {
        this.widgetContainers = widgetContainers;
    }

    public List<WebpageWidgetContainer> getWidgetContainers() {
        return widgetContainers;
    }
}
