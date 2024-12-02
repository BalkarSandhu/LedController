package com.dadhwal.LedController.LedSDK.Editor.Components;

public class TextAttribute {
    private String backgroundColor;
    private Attributes attributes;

    public TextAttribute(String backgroundColor, Attributes attributes){
        this.backgroundColor=backgroundColor;
        this.attributes=attributes;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Attributes getAttributes() {
        return attributes;
    }
}