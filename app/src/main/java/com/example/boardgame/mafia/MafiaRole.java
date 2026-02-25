package com.example.boardgame.mafia;

import java.io.Serializable;

public class MafiaRole implements Serializable {
    private String name;
    private String description;
    private int imageResId; // ID картинки из drawable

    public MafiaRole(String name, String description, int imageResId) {
        this.name = name;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
}