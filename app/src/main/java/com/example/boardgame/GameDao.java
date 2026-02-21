package com.example.boardgame;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface GameDao {
    @Insert
    void insert(BoardGame game);

    @Update
    void update(BoardGame game);

    @Delete
    void delete(BoardGame game);

    // Запрос для каталога: только системные игры, не в архиве
    @Query("SELECT * FROM games_table WHERE isUserGame = 0 AND isArchived = 0")
    LiveData<List<BoardGame>> getCatalogGames();

    // Запрос для избранного: только те, где стоит галочка (1)
    @Query("SELECT * FROM games_table WHERE isFavorite = 1")
    LiveData<List<BoardGame>> getFavoriteGames();

    // Запрос для "Моих игр": только те, что создал пользователь (isUserGame = 1)
    @Query("SELECT * FROM games_table WHERE isUserGame = 1")
    LiveData<List<BoardGame>> getUserGames();

    @Query("SELECT * FROM games_table")
    LiveData<List<BoardGame>> getAllGames();
}