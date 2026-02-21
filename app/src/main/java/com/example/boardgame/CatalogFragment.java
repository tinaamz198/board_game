package com.example.boardgame;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class CatalogFragment extends Fragment {

    private GameViewModel gameViewModel;
    private GameAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Инициализация ViewModel сразу
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

        // 2. Настройка списка
        RecyclerView recyclerView = view.findViewById(R.id.rvGames);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GameAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 3. Поиск
        EditText searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 4. Кнопка добавить (+) - добавляет в "Мои игры"
        view.findViewById(R.id.fabAdd).setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            builder.setTitle("Новая игра");
            final EditText inputName = new EditText(getContext());
            inputName.setHint("Введите название");
            builder.setView(inputName);

            builder.setPositiveButton("Добавить", (dialog, which) -> {
                String name = inputName.getText().toString();
                if (!name.isEmpty()) {
                    // Параметры: имя, описание, фото, архив(0), избранное(0), МОЯ ИГРА(1)
                    gameViewModel.insert(new BoardGame(name, "Моя личная игра", R.drawable.game1, false, false, true));
                    Toast.makeText(getContext(), "Добавлено в 'Мои игры'", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Отмена", null);
            builder.show();
        });

        // 5. Переход в детали
        adapter.setOnItemClickListener(game -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", game.getTitle());
            bundle.putString("desc", game.getDescription());
            bundle.putInt("image", game.getImageResource());

            Navigation.findNavController(view)
                    .navigate(R.id.action_catalogFragment_to_gameDetailFragment, bundle);
        });

        // 6. Наблюдение за КАТАЛОГОМ (только системные игры)
        gameViewModel.getCatalogGames().observe(getViewLifecycleOwner(), games -> {
            if (games != null) {
                adapter.setGames(games);
                if (games.isEmpty()) {
                    // Системные игры (последний флаг - false)
                    gameViewModel.insert(new BoardGame("Дженга", "Падающая башня.", R.drawable.game2, false, false, false));
                    gameViewModel.insert(new BoardGame("Мафия", "Детективная игра.", R.drawable.game3, false, false, false));
                }
            }
        });

        // 7. Свайп для удаления
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) { return false; }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                BoardGame game = adapter.getGameAt(position);
                gameViewModel.delete(game);
                Toast.makeText(getContext(), "Удалено", Toast.LENGTH_SHORT).show();
            }
        })
                .attachToRecyclerView(recyclerView);// 5.1. Клик по звездочке (Избранное)
        adapter.setOnFavoriteClickListener(game -> {
            // В адаптере статус уже переключился, теперь сохраняем в базу
            gameViewModel.update(game);

            String message = game.isFavorite() ? "Добавлено в избранное" : "Удалено из избранного";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }
}