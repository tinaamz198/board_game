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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MyGamesFragment extends Fragment {

    private GameViewModel gameViewModel;
    private GameAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_games, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.rvMyGames);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new GameAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Клик по своей игре — ведет в детали с возможностью редактирования
        adapter.setOnItemClickListener(game -> {
            Bundle bundle = new Bundle();
            bundle.putInt("id", game.getId());
            bundle.putString("title", game.getTitle());
            bundle.putString("desc", game.getDescription());
            bundle.putString("image", game.getImagePath());

            // ПЕРЕДАЕМ ТЕГИ И ДАННЫЕ (чтобы они не пропадали)
            bundle.putString("players", game.getPlayers());
            bundle.putString("difficulty", game.getDifficulty());
            bundle.putString("category", game.getCategory());
            bundle.putBoolean("isFavorite", game.isFavorite());
            bundle.putBoolean("isUserGame", game.isUserGame());

            Navigation.findNavController(view).navigate(R.id.action_myGamesFragment_to_gameDetailFragment, bundle);
        });

        gameViewModel.getUserGames().observe(getViewLifecycleOwner(), games -> {
            if (games != null) {
                adapter.setGames(games);
            }
        });

        adapter.setOnFavoriteClickListener(game -> {
            gameViewModel.update(game);
        });

        // Удаление свайпом
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView r, @NonNull RecyclerView.ViewHolder v, @NonNull RecyclerView.ViewHolder t) { return false; }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {
                int position = vh.getBindingAdapterPosition();
                BoardGame gameToDelete = adapter.getGameAt(position);

                if (gameToDelete == null) return; // Защита от вылета

                new android.app.AlertDialog.Builder(getContext())
                        .setTitle("Удаление")
                        .setMessage("Удалить \"" + gameToDelete.getTitle() + "\"?")
                        .setPositiveButton("Да", (dialog, which) -> {
                            gameViewModel.delete(gameToDelete);
                        })
                        .setNegativeButton("Отмена", (dialog, which) -> {
                            adapter.notifyItemChanged(position); // Возвращаем карточку на место
                        })
                        .show();
            }
        }).attachToRecyclerView(recyclerView);
    }
}