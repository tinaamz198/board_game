package com.example.boardgame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.boardgame.database.BoardGame;
import com.example.boardgame.base.GameViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GamePlayFragment extends Fragment {

    private List<String> questionsList = new ArrayList<>();
    private int currentIndex = 0;
    private GameViewModel gameViewModel;
    private int gameId;
    private String lastRawText = ""; // Чтобы не перемешивать каждую секунду при обновлении базы

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_play, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        TextView tvTitle = view.findViewById(R.id.tvGameTitle);
        TextView tvQuestion = view.findViewById(R.id.tvQuestionText);

        if (getArguments() != null) {
            this.gameId = getArguments().getInt("id", -1);
            String title = getArguments().getString("title", "Игра");
            tvTitle.setText(title);

            // Наблюдаем за игрой
            gameViewModel.getAllGames().observe(getViewLifecycleOwner(), games -> {
                if (games != null && gameId != -1) {
                    for (BoardGame g : games) {
                        if (g.getId() == gameId) {
                            String currentRawText = g.getDescription();
                            // Перемешиваем только если текст действительно изменился (например, после редактора)
                            if (!currentRawText.equals(lastRawText)) {
                                loadAndShuffleQuestions(currentRawText);
                                lastRawText = currentRawText;
                                currentIndex = 0; // Сбрасываем на первый (уже перемешанный) вопрос
                            }
                            updateUI(tvQuestion);
                            break;
                        }
                    }
                }
            });
        }

        // Переход в редактор
        view.findViewById(R.id.btnEditQuestions).setOnClickListener(v -> {
            if (gameId != -1 && gameId != 0) {
                Bundle b = new Bundle();
                b.putInt("id", gameId);
                Navigation.findNavController(view).navigate(R.id.action_gamePlayFragment_to_editQuestionsFragment, b);
            }
        });

        // Кнопка Дальше
        view.findViewById(R.id.btnNext).setOnClickListener(v -> {
            if (!questionsList.isEmpty() && currentIndex < questionsList.size() - 1) {
                currentIndex++;
                updateUI(tvQuestion);
            } else {
                Toast.makeText(getContext(), "Это был последний случайный вопрос!", Toast.LENGTH_SHORT).show();
            }
        });

        // Кнопка Назад
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                updateUI(tvQuestion);
            }
        });

        view.findViewById(R.id.btnClose).setOnClickListener(v ->
                Navigation.findNavController(view).popBackStack()
        );
    }

    private void loadAndShuffleQuestions(String text) {
        if (text != null && !text.isEmpty()) {
            if (text.contains("|")) {
                questionsList = new ArrayList<>(Arrays.asList(text.split("\\|")));
            } else {
                questionsList = new ArrayList<>();
                questionsList.add(text);
            }
            // ГЛАВНАЯ ФИШКА: Рандом здесь!
            Collections.shuffle(questionsList);
        } else {
            questionsList = new ArrayList<>();
        }
    }

    private void updateUI(TextView tv) {
        if (!questionsList.isEmpty()) {
            if (currentIndex >= questionsList.size()) currentIndex = 0;
            tv.setText(questionsList.get(currentIndex));
        } else {
            tv.setText("Вопросов нет. Добавьте их через редактор.");
        }
    }
}