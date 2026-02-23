package com.example.boardgame;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

public class GameDetailFragment extends Fragment {

    private GameViewModel gameViewModel;
    private BoardGame currentGame;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        EditText etTitle = view.findViewById(R.id.detailTitle);
        EditText etDesc = view.findViewById(R.id.detailDescription);
        EditText etPlayers = view.findViewById(R.id.etPlayers);
        EditText etDifficulty = view.findViewById(R.id.etDifficulty);
        EditText etCategory = view.findViewById(R.id.etCategory);
        ImageView img = view.findViewById(R.id.detailImage);

        Button btnStart = view.findViewById(R.id.btnStartPlaying);
        Button btnEdit = view.findViewById(R.id.btnEditGame);
        Button btnSave = view.findViewById(R.id.btnSaveGame); // Убедись, что этот ID есть в XML

        // По умолчанию редактирование выключено
        toggleEdit(false, etTitle, etDesc, etPlayers, etDifficulty, etCategory);

        if (getArguments() != null) {
            int id = getArguments().getInt("id");
            gameViewModel.getAllGames().observe(getViewLifecycleOwner(), games -> {
                for (BoardGame g : games) {
                    if (g.getId() == id) {
                        currentGame = g;
                        etTitle.setText(g.getTitle());
                        etDesc.setText(g.getDescription());
                        etPlayers.setText(g.getPlayers());
                        etDifficulty.setText(g.getDifficulty());
                        etCategory.setText(g.getCategory());

                        // Логика кнопки старта (только для "Правда или Действие")
                        btnStart.setVisibility(g.getTitle().equalsIgnoreCase("Правда или Действие") ? View.VISIBLE : View.GONE);
                        break;
                    }
                }
            });
        }

        btnEdit.setOnClickListener(v -> {
            toggleEdit(true, etTitle, etDesc, etPlayers, etDifficulty, etCategory);
            btnSave.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
        });

        btnSave.setOnClickListener(v -> {
            if (currentGame != null) {
                currentGame.setTitle(etTitle.getText().toString());
                currentGame.setDescription(etDesc.getText().toString());
                currentGame.setPlayers(etPlayers.getText().toString());
                currentGame.setDifficulty(etDifficulty.getText().toString());
                currentGame.setCategory(etCategory.getText().toString());

                gameViewModel.update(currentGame);
                toggleEdit(false, etTitle, etDesc, etPlayers, etDifficulty, etCategory);
                btnSave.setVisibility(View.GONE);
                btnEdit.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Сохранено!", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.btnBack).setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        btnStart.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putInt("id", currentGame.getId());
            Navigation.findNavController(v).navigate(R.id.action_gameDetailFragment_to_gamePlayFragment, b);
        });
    }

    private void toggleEdit(boolean enabled, View... views) {
        for (View v : views) {
            v.setEnabled(enabled);
            v.setFocusableInTouchMode(enabled);
        }
    }
}