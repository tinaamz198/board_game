package com.example.boardgame.base;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.boardgame.database.GameRepository;
import com.example.boardgame.database.BoardGame;

import java.util.List;

public class GameViewModel extends AndroidViewModel {
    private final GameRepository repository;

    public GameViewModel(@NonNull Application application) {
        super(application);
        repository = new GameRepository(application);
    }

    // УДАЛЕНО: getPopularGames() больше не вызывает ошибку

    public LiveData<List<BoardGame>> getAllGames() {
        return repository.getAllGames();
    }

    public LiveData<List<BoardGame>> getFavoriteGames() {
        return repository.getFavoriteGames();
    }

    public LiveData<List<BoardGame>> getUserGames() {
        return repository.getUserGames();
    }

    public void insert(BoardGame game) { repository.insert(game); }
    public void delete(BoardGame game) { repository.delete(game); }
    public void update(BoardGame game) { repository.update(game); }
}