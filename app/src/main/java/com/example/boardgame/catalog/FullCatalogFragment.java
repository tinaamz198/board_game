package com.example.boardgame.catalog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.GameAdapter;
import com.example.boardgame.R;
import com.example.boardgame.database.BoardGame;
import com.example.boardgame.base.GameViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class FullCatalogFragment extends Fragment {
    private GameViewModel gameViewModel;
    private GameAdapter gameAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_full_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        // 1. Инициализация View
        RecyclerView recyclerView = view.findViewById(R.id.rvGames);
        EditText etSearch = view.findViewById(R.id.searchEditText);
        Button btnSort = view.findViewById(R.id.btnSort);
        View btnFilter = view.findViewById(R.id.btnFilter);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);
        View btnBack = view.findViewById(R.id.btnBackCatalog);

        // 2. Настройка адаптера
        gameAdapter = new GameAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(gameAdapter);

        // 3. Подписка на данные
        gameViewModel.getAllGames().observe(getViewLifecycleOwner(), games -> {
            if (games != null) {
                gameAdapter.setGames(games);
                String currentQuery = etSearch.getText().toString();
                if (!currentQuery.isEmpty()) {
                    gameAdapter.filter(currentQuery);
                }
            }
        });

        // 4. Поиск
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (gameAdapter != null) gameAdapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // --- ОБРАБОТКА РЕЗУЛЬТАТОВ ИЗ ШТОРКИ СОРТИРОВКИ ---
        getParentFragmentManager().setFragmentResultListener("sort_request", getViewLifecycleOwner(), (requestKey, bundle) -> {
            boolean isAscending = bundle.getBoolean("isAscending", true);
            if (gameAdapter != null) {
                gameAdapter.sort(isAscending);
            }
        });

        // --- ОБРАБОТКА РЕЗУЛЬТАТОВ ИЗ ШТОРКИ ФИЛЬТРА ---
        getParentFragmentManager().setFragmentResultListener("filter_request", getViewLifecycleOwner(), (requestKey, bundle) -> {
            String category = bundle.getString("category", "");
            String difficulty = bundle.getString("difficulty", "");
            if (gameAdapter != null) {
                gameAdapter.filterAdvanced(category, difficulty);
            }
        });

        // 5. Кнопка СОРТИРОВКИ (теперь открывает шторку)
        if (btnSort != null) {
            btnSort.setOnClickListener(v -> {
                // ПРОВЕРЬ: В navigation.xml должен быть action к sortBottomSheet
                Navigation.findNavController(v).navigate(R.id.action_fullCatalogFragment_to_sortBottomSheet);
            });
        }

        // 6. Кнопка ФИЛЬТРА
        if (btnFilter != null) {
            btnFilter.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_fullCatalogFragment_to_filterBottomSheet);
            });
        }

        // 7. Остальные клики
        gameAdapter.setOnItemClickListener(game -> {
            Bundle b = new Bundle();
            b.putInt("id", game.getId());
            b.putString("title", game.getTitle());
            b.putString("desc", game.getDescription());
            b.putString("image", game.getImagePath());
            b.putString("players", game.getPlayers());
            b.putString("difficulty", game.getDifficulty());
            b.putString("category", game.getCategory());
            Navigation.findNavController(view).navigate(R.id.action_fullCatalogFragment_to_gameDetailFragment, b);
        });

        gameAdapter.setOnFavoriteClickListener(game -> gameViewModel.update(game));

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        }
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                BoardGame newGame = new BoardGame(
                        "Новая игра",           // Название
                        "Введите описание...",   // Описание
                        "Введите правила...",    // Правила (обязательно добавь этот аргумент!)
                        "fornewgame",                // <--- ИЗМЕНИ ЗДЕСЬ (название твоей картинки из drawable)
                        false,                  // isFeatured
                        false,                  // isFavorite
                        true,                   // isUserGame
                        "2-4",                  // Игроки
                        "Легко",                // Сложность
                        "Вечеринка",            // Категория
                        0.0f                    // Рейтинг
                );

                gameViewModel.insert(newGame);
                Toast.makeText(getContext(), "Игра создана! Нажмите на неё, чтобы изменить детали.", Toast.LENGTH_SHORT).show();
            });
        }
    }
}