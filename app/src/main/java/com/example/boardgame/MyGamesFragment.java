package com.example.boardgame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyGamesFragment extends Fragment {

    private GameViewModel gameViewModel;
    private GameAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Здесь поиск можно оставить, а кнопку FAB можно оставить для добавления своих игр
        RecyclerView recyclerView = view.findViewById(R.id.rvGames);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GameAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

        // Самое главное: просим ТОЛЬКО игры пользователя
        gameViewModel.getUserGames().observe(getViewLifecycleOwner(), games -> {
            if (games != null) {
                adapter.setGames(games);
            }
        });

        // Клик по звезде тоже будет работать
        adapter.setOnFavoriteClickListener(game -> {
            game.setFavorite(!game.isFavorite());
            gameViewModel.update(game);
        });
    }
}