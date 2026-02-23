package com.example.boardgame;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.Executors;

@Database(entities = {BoardGame.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    public abstract GameDao gameDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "game_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadExecutor().execute(() -> {
                GameDao dao = instance.gameDao();
                // Используем категории: Вечеринка, Стратегия, Детектив
                dao.insert(new BoardGame("Правда или Действие", "Классическая игра...", "game1", true, false, false, "2-10", "Легко", "Вечеринка"));
                dao.insert(new BoardGame("Мафия", "Детективная игра.", "game2", true, false, false, "6-15", "Средне", "Детектив"));
                dao.insert(new BoardGame("Монополия", "Экономическая стратегия.", "game3", false, false, false, "2-6", "Сложно", "Стратегия"));
                dao.insert(new BoardGame("Крокодил", "Жесты и мимика.", "game1", false, false, false, "3-12", "Легко", "Вечеринка"));
            });
        }
    };
}