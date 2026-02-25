package com.example.boardgame.questions;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

import com.example.boardgame.R;
import com.example.boardgame.database.BoardGame;
import com.example.boardgame.base.GameViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditQuestionsFragment extends Fragment {
    // Оставляем только ОДНУ переменную списка
    private final List<String> questions = new ArrayList<>();
    private QuestionAdapter adapter;
    private GameViewModel gameViewModel;
    private BoardGame currentGame;

    public EditQuestionsFragment() { super(R.layout.fragment_edit_questions); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        // Получаем ID игры. ВАЖНО: проверь, что в GamePlayFragment ты передаешь именно "id"
        int gameId = (getArguments() != null) ? getArguments().getInt("id", -1) : -1;

        RecyclerView rv = view.findViewById(R.id.rvQuestionsList);
        EditText etInput = view.findViewById(R.id.etNewQuestion);

        adapter = new QuestionAdapter(questions);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        // Если ID валидный, ищем игру
        if (gameId != -1) {
            gameViewModel.getAllGames().observe(getViewLifecycleOwner(), games -> {
                if (games != null && currentGame == null) { // Ищем только один раз
                    for (BoardGame g : games) {
                        if (g.getId() == gameId) {
                            currentGame = g;
                            String raw = g.getDescription();
                            if (raw != null && !raw.isEmpty()) {
                                questions.clear();
                                // Разбиваем строку на вопросы
                                questions.addAll(Arrays.asList(raw.split("\\|")));
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        }
                    }
                }
            });
        }

        // Добавление вопроса (+)
        view.findViewById(R.id.btnAddQuestion).setOnClickListener(v -> {
            String q = etInput.getText().toString().trim();
            if (!q.isEmpty()) {
                questions.add(q);
                adapter.notifyItemInserted(questions.size() - 1);
                rv.scrollToPosition(questions.size() - 1);
                etInput.setText("");
            }
        });

        // Кнопка Готово
        view.findViewById(R.id.btnDone).setOnClickListener(v -> {
            if (currentGame != null) {
                // Сохраняем вопросы через разделитель |
                currentGame.setDescription(TextUtils.join("|", questions));
                gameViewModel.update(currentGame);
                Toast.makeText(getContext(), "Сохранено", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).popBackStack();
            } else {
                // Если мы здесь, значит currentGame всё еще null
                Toast.makeText(getContext(), "Ошибка: Игра с ID " + gameId + " не найдена", Toast.LENGTH_LONG).show();
            }
        });
        // В блоке сохранения добавь проверку:
        view.findViewById(R.id.btnDone).setOnClickListener(v -> {
            if (currentGame != null) {
                String joinedQuestions = TextUtils.join("|", questions);
                currentGame.setDescription(joinedQuestions);
                gameViewModel.update(currentGame);
                Toast.makeText(getContext(), "Вопросы обновлены", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).popBackStack();
            }
        });

        // Свайп для удаления
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override public boolean onMove(RecyclerView r, RecyclerView.ViewHolder vh, RecyclerView.ViewHolder t) { return false; }
            @Override public void onSwiped(RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getAdapterPosition();
                questions.remove(pos);
                adapter.notifyItemRemoved(pos);
            }
        }).attachToRecyclerView(rv);
    }
}