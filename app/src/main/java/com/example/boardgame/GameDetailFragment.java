package com.example.boardgame;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

public class GameDetailFragment extends Fragment {

    private GameViewModel gameViewModel;
    private BoardGame currentGame;
    private ImageView detailImage;
    private String selectedImageUri = "";

    // Лаунчер для выбора фото из галереи
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        // Сохраняем права на чтение URI (важно для Android 10+)
                        requireContext().getContentResolver().takePersistableUriPermission(
                                imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        selectedImageUri = imageUri.toString();
                        detailImage.setImageURI(imageUri);
                    }
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

        EditText etTitle = view.findViewById(R.id.detailTitle);
        EditText etDesc = view.findViewById(R.id.detailDescription);
        EditText etPlayers = view.findViewById(R.id.etPlayers);
        EditText etDifficulty = view.findViewById(R.id.etDifficulty);
        EditText etCategory = view.findViewById(R.id.etCategory);
        detailImage = view.findViewById(R.id.detailImage);

        Button btnStart = view.findViewById(R.id.btnStartPlaying);
        Button btnEdit = view.findViewById(R.id.btnEditGame);
        Button btnSave = view.findViewById(R.id.btnSaveGame);
        Button btnPickPhoto = view.findViewById(R.id.btnPickPhoto); // Новая кнопка

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

                        // Загрузка картинки
                        displayImage(g.getImagePath());

                        btnStart.setVisibility(g.getTitle().equalsIgnoreCase("Правда или Действие") ? View.VISIBLE : View.GONE);
                        break;
                    }
                }
            });
        }

        btnPickPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        btnEdit.setOnClickListener(v -> {
            toggleEdit(true, etTitle, etDesc, etPlayers, etDifficulty, etCategory);
            btnSave.setVisibility(View.VISIBLE);
            btnPickPhoto.setVisibility(View.VISIBLE); // Показываем выбор фото при редактировании
            btnEdit.setVisibility(View.GONE);
        });

        btnSave.setOnClickListener(v -> {
            if (currentGame != null) {
                currentGame.setTitle(etTitle.getText().toString());
                currentGame.setDescription(etDesc.getText().toString());
                currentGame.setPlayers(etPlayers.getText().toString());
                currentGame.setDifficulty(etDifficulty.getText().toString());
                currentGame.setCategory(etCategory.getText().toString());
                if (!selectedImageUri.isEmpty()) {
                    currentGame.setImagePath(selectedImageUri);
                }

                gameViewModel.update(currentGame);
                toggleEdit(false, etTitle, etDesc, etPlayers, etDifficulty, etCategory);
                btnSave.setVisibility(View.GONE);
                btnPickPhoto.setVisibility(View.GONE);
                btnEdit.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Обновлено!", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.btnBack).setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        btnStart.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putInt("id", currentGame.getId());
            Navigation.findNavController(v).navigate(R.id.action_gameDetailFragment_to_gamePlayFragment, b);
        });
    }

    private void displayImage(String path) {
        if (path == null || path.isEmpty() || path.equals("no_photo")) {
            detailImage.setImageResource(R.drawable.game1);
        } else if (path.startsWith("content://") || path.startsWith("file://")) {
            detailImage.setImageURI(Uri.parse(path));
        } else {
            int resId = getResources().getIdentifier(path, "drawable", requireContext().getPackageName());
            if (resId != 0) detailImage.setImageResource(resId);
        }
    }

    private void toggleEdit(boolean enabled, View... views) {
        for (View v : views) {
            v.setEnabled(enabled);
            v.setFocusableInTouchMode(enabled);
            if (v instanceof EditText) {
                v.setAlpha(enabled ? 1.0f : 0.8f);
            }
        }
    }
}