package com.aap.medicore.Models;

import android.graphics.drawable.Drawable;

public class EquipmentAccessoriesModel {

    private Drawable imageResource;
    private String text;
    private boolean selected;

    public Drawable getImageResource() {
        return imageResource;
    }

    public void setImageResource(Drawable imageResource) {
        this.imageResource = imageResource;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
