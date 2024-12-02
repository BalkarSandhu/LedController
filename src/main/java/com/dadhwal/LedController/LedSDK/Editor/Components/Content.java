package com.dadhwal.LedController.LedSDK.Editor.Components;

import java.util.List;

public class Content {
    private boolean autoPaging;
    private BackgroundMusic backgroundMusic;
    private DisplayStyle displayStyle;
    private List<Paragraph> paragraphs;
    private List<TextAttribute> textAttributes;

    public Content(boolean autoPaging, BackgroundMusic backgroundMusic, DisplayStyle displayStyle, List<Paragraph> paragraphs, List<TextAttribute> textAttributes){
        this.autoPaging=autoPaging;
        this.backgroundMusic=backgroundMusic;
        this.displayStyle=displayStyle;
        this.paragraphs=paragraphs;
        this.textAttributes=textAttributes;
    }

    public void setAutoPaging(boolean autoPaging) {
        this.autoPaging = autoPaging;
    }

    public boolean isAutoPaging() {
        return autoPaging;
    }

    public void setBackgroundMusic(BackgroundMusic backgroundMusic) {
        this.backgroundMusic = backgroundMusic;
    }

    public BackgroundMusic getBackgroundMusic() {
        return backgroundMusic;
    }

    public void setDisplayStyle(DisplayStyle displayStyle) {
        this.displayStyle = displayStyle;
    }

    public DisplayStyle getDisplayStyle() {
        return displayStyle;
    }

    public void setParagraphs(List<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public List<Paragraph> getParagraphs() {
        return paragraphs;
    }

    public void setTextAttributes(List<TextAttribute> textAttributes) {
        this.textAttributes = textAttributes;
    }

    public List<TextAttribute> getTextAttributes() {
        return textAttributes;
    }
}
