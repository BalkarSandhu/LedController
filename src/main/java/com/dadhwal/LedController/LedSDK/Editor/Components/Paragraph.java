package com.dadhwal.LedController.LedSDK.Editor.Components;

import java.util.List;

public class Paragraph {
    private String backgroundColor;
    private String horizontalAlignment;
    private int letterSpacing;
    private int lineSpacing;
    private List<Line> lines;
    private String verticalAlignment;

    public Paragraph(String backgroundColor, String horizontalAlignment, int letterSpacing, int lineSpacing, List<Line> lines, String verticalAlignment){
        this.backgroundColor=backgroundColor;
        this.horizontalAlignment=horizontalAlignment;
        this.letterSpacing=letterSpacing;
        this.lineSpacing=lineSpacing;
        this.lines=lines;
        this.verticalAlignment=verticalAlignment;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setHorizontalAlignment(String horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public String getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setLetterSpacing(int letterSpacing) {
        this.letterSpacing = letterSpacing;
    }

    public int getLetterSpacing() {
        return letterSpacing;
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setVerticalAlignment(String verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public String getVerticalAlignment() {
        return verticalAlignment;
    }
}
