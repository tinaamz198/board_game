package com.example.boardgame;

import com.example.boardgame.database.BoardGame;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("games") // Путь к твоему JSON списку на Tomcat
    Call<List<BoardGame>> getGames();
}