package com.dadhwal.LedController.LedSDK.Editor.Components;

import java.util.List;

public class Font {
    private List<String> family;
    private boolean isUnderline;
    private int size;
    private String style;

    public Font(List<String> family, boolean isUnderline, int size, String style){
        this.family=family;
        this.isUnderline=isUnderline;
        this.size=size;
        this.style=style;
    }

    public void setFamily(List<String> family) {
        this.family = family;
    }

    public List<String> getFamily() {
        return family;
    }

    public void setUnderline(boolean underline) {
        isUnderline = underline;
    }

    public boolean isUnderline() {
        return isUnderline;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
