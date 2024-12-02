package com.dadhwal.LedController.LedSDK.Editor.Components;

public class DisplayStyle {
    private ScrollAttributes scrollAttributes;
    private String type;

    public DisplayStyle(ScrollAttributes scrollAttributes, String type){
        this.scrollAttributes=scrollAttributes;
        this.type=type;
    }

    public void setScrollAttributes(ScrollAttributes scrollAttributes) {
        this.scrollAttributes = scrollAttributes;
    }

    public ScrollAttributes getScrollAttributes() {
        return scrollAttributes;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
