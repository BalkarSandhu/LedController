package com.dadhwal.LedController.LedSDK.Editor.Components;

public class WidgetContainer {
    private Contents contents;
    private int id;
    private String name;

    public WidgetContainer(Contents contents, int id, String name){
        this.contents=contents;
        this.id=id;
        this.name=name;
    }

    public void setContents(Contents contents){
        this.contents=contents;
    }

    public Contents getContents() {
        return contents;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
