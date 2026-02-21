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

public class FavoriteFragment extends Fragment {

    private GameViewModel gameViewModel;
    private GameAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Используем тот же макет, что и в каталоге
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // В избранном нам не нужна строка поиска и кнопка "Добавить"
        view.findViewById(R.id.searchEditText).setVisibility(View.GONE);
        view.findViewById(R.id.fabAdd).setVisibility(View.GONE);

        RecyclerView recyclerView = view.findViewById(R.id.rvGames);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GameAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

        // Самое главное: просим ТОЛЬКО избранные игры
        gameViewModel.getFavoriteGames().observe(getViewLifecycleOwner(), games -> {
            if (games != null) {
                adapter.setGames(games);
            }
        });

        // Если нажать на звезду в этом списке — убираем из избранного
        adapter.setOnFavoriteClickListener(game -> {
            game.setFavorite(false);
            gameViewModel.update(game);
            Toast.makeText(getContext(), "Удалено из избранного", Toast.LENGTH_SHORT).show();
        });
    }
}