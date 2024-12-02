package com.dadhwal.LedController.LedSDK.Editor;

import com.dadhwal.LedController.LedSDK.Editor.Components.WidgetContainer;

import java.util.List;

public class PageInfo {

    private String name;
    private List<WidgetContainer> widgetContainers;

    public PageInfo(String name, List<WidgetContainer> widgetContainers){
        this.name=name;
        this.widgetContainers=widgetContainers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setWidgetContainers(List<WidgetContainer> widgetContainers) {
        this.widgetContainers = widgetContainers;
    }

    public List<WidgetContainer> getWidgetContainers() {
        return widgetContainers;
    }
}
