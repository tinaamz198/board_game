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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class FavoriteFragment extends Fragment {

    private GameViewModel gameViewModel;
    private GameAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_full_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Скрываем лишнее
        if (view.findViewById(R.id.searchEditText) != null) view.findViewById(R.id.searchEditText).setVisibility(View.GONE);
        if (view.findViewById(R.id.btnSort) != null) view.findViewById(R.id.btnSort).setVisibility(View.GONE);
        if (view.findViewById(R.id.btnFilter) != null) view.findViewById(R.id.btnFilter).setVisibility(View.GONE);
        if (view.findViewById(R.id.fabAdd) != null) view.findViewById(R.id.fabAdd).setVisibility(View.GONE);

        RecyclerView recyclerView = view.findViewById(R.id.rvGames);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Теперь конструктор принимает ArrayList, ошибки не будет
        adapter = new GameAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        gameViewModel.getFavoriteGames().observe(getViewLifecycleOwner(), games -> {
            if (games != null) {
                adapter.setGames(games);
            }
        });

        adapter.setOnItemClickListener(game -> {
            Bundle bundle = new Bundle();
            bundle.putInt("id", game.getId());
            bundle.putString("title", game.getTitle());
            bundle.putString("desc", game.getDescription());
            bundle.putString("image", game.getImagePath());
            bundle.putBoolean("isFavorite", game.isFavorite());

            try {
                Navigation.findNavController(view).navigate(R.id.action_favoriteFragment_to_gameDetailFragment, bundle);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Ошибка навигации", Toast.LENGTH_SHORT).show();
            }
        });

        // Теперь метод setOnFavoriteClickListener существует
        adapter.setOnFavoriteClickListener(game -> {
            game.setFavorite(false);
            gameViewModel.update(game);
            Toast.makeText(getContext(), "Удалено из избранного", Toast.LENGTH_SHORT).show();
        });
    }
}