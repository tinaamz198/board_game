package com.example.boardgame.details;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.boardgame.R;
import com.example.boardgame.database.BoardGame;
import com.example.boardgame.base.GameViewModel;

public class GameDetailFragment extends Fragment {

    private GameViewModel gameViewModel;
    private BoardGame currentGame;
    private EditText etTitle, etShortDesc, etFullRules, etPlayers, etDifficulty, etCategory;
    private ImageView detailImage;
    private RatingBar ratingBar;
    private Button btnStart, btnEdit, btnSave, btnPickPhoto;

    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && currentGame != null) {
                    detailImage.setImageURI(uri);
                    currentGame.setImagePath(uri.toString());
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        etTitle = view.findViewById(R.id.detailTitle);
        etShortDesc = view.findViewById(R.id.Description);
        etFullRules = view.findViewById(R.id.detailDescription);
        etPlayers = view.findViewById(R.id.etPlayers);
        etDifficulty = view.findViewById(R.id.etDifficulty);
        etCategory = view.findViewById(R.id.etCategory);
        detailImage = view.findViewById(R.id.detailImage);
        ratingBar = view.findViewById(R.id.gameRatingBar);

        btnStart = view.findViewById(R.id.btnStartPlaying);
        btnEdit = view.findViewById(R.id.btnEditGame);
        btnSave = view.findViewById(R.id.btnSaveGame);
        btnPickPhoto = view.findViewById(R.id.btnPickPhoto);

        // ЛОГИКА КНОПКИ "НАЧАТЬ ИГРУ" - ТЕПЕРЬ ВЕДЕТ НА ВОПРОСЫ
        // Внутри onViewCreated в GameDetailFragment.java
        btnStart.setOnClickListener(v -> {
            if (currentGame == null) return;
            Bundle bundle = new Bundle();
            bundle.putInt("id", currentGame.getId());
            String title = currentGame.getTitle().trim();

            if (title.equalsIgnoreCase("Мафия")) {
                Navigation.findNavController(v).navigate(R.id.action_gameDetailFragment_to_mafiaSetupFragment, bundle);
            } else if (title.equalsIgnoreCase("Рандомайзер")) {
                Navigation.findNavController(v).navigate(R.id.action_gameDetailFragment_to_wheelFragment, bundle);
            } else if (title.equalsIgnoreCase("Мои вопросы") || title.equalsIgnoreCase("Правда или Действие")) {
                // ИСПРАВЛЕНО: Теперь ведет на экран САМОЙ ИГРЫ (карточки с вопросами)
                Navigation.findNavController(v).navigate(R.id.action_gameDetailFragment_to_gamePlayFragment, bundle);
            } else {
                Navigation.findNavController(v).navigate(R.id.action_gameDetailFragment_to_gamePlayFragment, bundle);
            }
        });

        btnPickPhoto.setOnClickListener(v -> getContent.launch("image/*"));

        if (getArguments() != null) {
            int id = getArguments().getInt("id");
            gameViewModel.getAllGames().observe(getViewLifecycleOwner(), games -> {
                for (BoardGame g : games) {
                    if (g.getId() == id) {
                        currentGame = g;
                        displayGameData(g);
                        break;
                    }
                }
            });
        }

        btnEdit.setOnClickListener(v -> {
            toggleEdit(true);
            btnSave.setVisibility(View.VISIBLE);
            btnPickPhoto.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
        });

        btnSave.setOnClickListener(v -> {
            if (currentGame != null) {
                saveData();
                toggleEdit(false);
                btnSave.setVisibility(View.GONE);
                btnPickPhoto.setVisibility(View.GONE);
                btnEdit.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Сохранено!", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.btnBack).setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        toggleEdit(false);
    }

    private void displayGameData(BoardGame g) {
        etTitle.setText(g.getTitle());
        etShortDesc.setText(g.getDescription());
        etFullRules.setText(g.getRules());
        etPlayers.setText(g.getPlayers());
        etDifficulty.setText(g.getDifficulty());
        etCategory.setText(g.getCategory());
        ratingBar.setRating(g.getRating());

        String path = g.getImagePath();
        if (path != null && !path.isEmpty()) {
            int resId = getResources().getIdentifier(path, "drawable", requireContext().getPackageName());
            if (resId != 0) {
                detailImage.setImageResource(resId);
            } else {
                detailImage.setImageURI(Uri.parse(path));
            }
        }

        String title = g.getTitle().trim();
        // Кнопка "Начать игру" видна для ключевых игр и всех пользовательских
        if (title.equalsIgnoreCase("Рандомайзер") ||
                title.equalsIgnoreCase("Правда или Действие") ||
                title.equalsIgnoreCase("Мафия") ||
                title.equalsIgnoreCase("Мои вопросы") ||
                g.isUserGame()) {
            btnStart.setVisibility(View.VISIBLE);
        } else {
            btnStart.setVisibility(View.GONE);
        }

        btnEdit.setVisibility(g.isUserGame() ? View.VISIBLE : View.GONE);
    }

    private void saveData() {
        if (currentGame == null) return;
        currentGame.setTitle(etTitle.getText().toString());
        currentGame.setDescription(etShortDesc.getText().toString());
        currentGame.setRules(etFullRules.getText().toString());
        currentGame.setPlayers(etPlayers.getText().toString());
        currentGame.setDifficulty(etDifficulty.getText().toString());
        currentGame.setCategory(etCategory.getText().toString());
        currentGame.setRating(ratingBar.getRating());
        gameViewModel.update(currentGame);
    }

    private void toggleEdit(boolean enabled) {
        EditText[] fields = {etTitle, etShortDesc, etFullRules, etPlayers, etDifficulty, etCategory};
        for (EditText f : fields) {
            f.setEnabled(enabled);
            f.setFocusableInTouchMode(enabled);
            f.setFocusable(enabled);
        }
        ratingBar.setIsIndicator(!enabled);
    }
}