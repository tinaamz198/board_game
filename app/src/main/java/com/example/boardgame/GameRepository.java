package com.example.boardgame;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameRepository {
    private final GameDao gameDao;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public GameRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        gameDao = database.gameDao();
    }

    public LiveData<List<BoardGame>> getCatalogGames() { return gameDao.getCatalogGames(); }
    public LiveData<List<BoardGame>> getFavoriteGames() { return gameDao.getFavoriteGames(); }
    public LiveData<List<BoardGame>> getUserGames() { return gameDao.getUserGames(); }

    public void insert(BoardGame game) {
        executorService.execute(() -> gameDao.insert(game));
    }
    public void delete(BoardGame game) {
        executorService.execute(() -> gameDao.delete(game));
    }
    public LiveData<List<BoardGame>> getAllGames() {
        return gameDao.getAllGames(); // Просто возвращаем переменную, которую создали в конструкторе
    }

    // Убедись, что метод update выглядит так:
    public void update(BoardGame game) {
        executorService.execute(() -> gameDao.update(game));
    }
}