package com.example.faceauthentication.model;

import android.graphics.Bitmap;

public class ItemModel {
    private String name;
    private Bitmap image;

    public ItemModel(String name, Bitmap image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage() {
        return image;
    }
}

