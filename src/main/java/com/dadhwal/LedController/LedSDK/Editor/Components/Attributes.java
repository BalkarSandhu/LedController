package com.dadhwal.LedController.LedSDK.Editor.Components;

public class Attributes {
        private Font font;
        private int letterSpacing;
        private String textColor;

        public Attributes(Font font, int letterSpacing, String textColor){
            this.font=font;
            this.letterSpacing=letterSpacing;
            this.textColor=textColor;
        }

        public void setFont(Font font) {
                this.font = font;
        }

        public Font getFont() {
                return font;
        }

        public void setLetterSpacing(int letterSpacing) {
                this.letterSpacing = letterSpacing;
        }

        public int getLetterSpacing() {
                return letterSpacing;
        }

        public void setTextColor(String textColor) {
                this.textColor = textColor;
        }

        public String getTextColor() {
                return textColor;
        }
}

