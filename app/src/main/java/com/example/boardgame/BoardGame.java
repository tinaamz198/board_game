package com.example.boardgame;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "games_table")
public class BoardGame {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private int imageResource;
    private boolean isArchived;
    private boolean isFavorite;
    private boolean isUserGame;

    public BoardGame(String title, String description, int imageResource, boolean isArchived, boolean isFavorite, boolean isUserGame) {
        this.title = title;
        this.description = description;
        this.imageResource = imageResource;
        this.isArchived = isArchived;
        this.isFavorite = isFavorite;
        this.isUserGame = isUserGame;
    }

    // Геттеры и сеттеры (ВАЖНО: проверь, чтобы названия совпадали!)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getImageResource() { return imageResource; }
    public boolean isArchived() { return isArchived; }
    public void setArchived(boolean archived) { isArchived = archived; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public boolean isUserGame() { return isUserGame; }
    public void setUserGame(boolean userGame) { isUserGame = userGame; }
    // Геттер для проверки, в избранном ли игра

}