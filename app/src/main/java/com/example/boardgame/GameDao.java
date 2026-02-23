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
    // Этот запрос теперь берет ВСЕ игры для общего каталога
    @Query("SELECT * FROM games_table ORDER BY id DESC")
    LiveData<List<BoardGame>> getAllGames();

    @Query("SELECT * FROM games_table WHERE isFavorite = 1")
    LiveData<List<BoardGame>> getFavoriteGames();

    @Query("SELECT * FROM games_table WHERE isUserGame = 1 ORDER BY id DESC")
    LiveData<List<BoardGame>> getUserGames();

    @Insert
    void insert(BoardGame game);

    @Update
    void update(BoardGame game);

    @Delete
    void delete(BoardGame game);
}