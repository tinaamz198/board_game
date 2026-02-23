package com.example.boardgame;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "games_table")
public class BoardGame {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private String imagePath;

    private boolean isFeatured;
    private boolean isFavorite;
    private boolean isUserGame;
    private String players;
    private String difficulty;
    private String category;

    // 1. ДОБАВЛЕНО: Поле для хранения рейтинга
    private float rating;

    // ОБНОВЛЕННЫЙ Конструктор (добавлен параметр float rating)
    public BoardGame(String title, String description, String imagePath, boolean isFeatured,
                     boolean isFavorite, boolean isUserGame, String players, String difficulty,
                     String category, float rating) {
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.isFeatured = isFeatured;
        this.isFavorite = isFavorite;
        this.isUserGame = isUserGame;
        this.players = players;
        this.difficulty = difficulty;
        this.category = category;
        this.rating = rating; // Сохраняем рейтинг
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public boolean isUserGame() { return isUserGame; }
    public void setUserGame(boolean userGame) { isUserGame = userGame; }

    public String getPlayers() { return players; }
    public void setPlayers(String players) { this.players = players; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // 2. ДОБАВЛЕНО: Методы доступа к рейтингу
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
}