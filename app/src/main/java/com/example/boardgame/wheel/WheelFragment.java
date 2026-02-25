package com.example.boardgame.wheel;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.boardgame.R;
import com.example.boardgame.database.BoardGame;
import com.example.boardgame.base.GameViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WheelFragment extends Fragment {
    private WheelView wheelView;
    private final List<String> options = new ArrayList<>();
    private boolean isSpinning = false;
    private GameViewModel gameViewModel;
    private BoardGame currentGame;

    // Используем твой макет fragment_wheel
    public WheelFragment() { super(R.layout.fragment_wheel); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация по твоим ID
        wheelView = view.findViewById(R.id.wheelView);
        Button btnAddOption = view.findViewById(R.id.btnAddOption);
        Button btnSpin = view.findViewById(R.id.btnSpin);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            // Возвращаемся на предыдущий фрагмент (в карточку игры или каталог)
            androidx.navigation.Navigation.findNavController(v).popBackStack();
        });

        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        // Получаем ID игры для загрузки вариантов
        int gameId = (getArguments() != null) ? getArguments().getInt("id", -1) : -1;

        if (gameId != -1) {
            gameViewModel.getAllGames().observe(getViewLifecycleOwner(), games -> {
                for (BoardGame g : games) {
                    if (g.getId() == gameId) {
                        currentGame = g;
                        String rawData = g.getDescription();
                        // Загружаем только если список еще пуст
                        if (rawData != null && !rawData.isEmpty() && options.isEmpty()) {
                            options.addAll(Arrays.asList(rawData.split("\\|")));
                            wheelView.setOptions(options);
                        }
                        break;
                    }
                }
            });
        }

        // Открытие диалога управления списком
        btnAddOption.setOnClickListener(v -> showManageOptionsDialog());

        // Логика вращения
        btnSpin.setOnClickListener(v -> {
            if (!isSpinning && options.size() > 1) {
                spinWheel();
            } else if (options.size() <= 1) {
                Toast.makeText(getContext(), "Добавьте хотя бы 2 варианта!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showManageOptionsDialog() {
        // Создаем диалог на основе макета dialog_manage_options
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_manage_options, null);
        EditText etInput = dialogView.findViewById(R.id.etNewOption);
        ListView lvOptions = dialogView.findViewById(R.id.lvOptions);
        Button btnAdd = dialogView.findViewById(R.id.btnAddOption);

        // Исправляем цвет текста (чтобы не был белым на светлом фоне диалога)
        etInput.setTextColor(Color.BLACK);
        etInput.setHintTextColor(Color.GRAY);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, options) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.BLACK);
                return textView;
            }
        };
        lvOptions.setAdapter(adapter);

        // Редактирование: нажали на элемент — он ушел в поле ввода
        lvOptions.setOnItemClickListener((parent, v, position, id) -> {
            etInput.setText(options.get(position));
            options.remove(position);
            adapter.notifyDataSetChanged();
            wheelView.setOptions(options);
        });

        // Удаление: зажали элемент — он удалился
        lvOptions.setOnItemLongClickListener((parent, v, position, id) -> {
            options.remove(position);
            adapter.notifyDataSetChanged();
            wheelView.setOptions(options);
            Toast.makeText(getContext(), "Удалено", Toast.LENGTH_SHORT).show();
            return true;
        });

        // Кнопка добавить внутри диалога
        btnAdd.setOnClickListener(v -> {
            String text = etInput.getText().toString().trim();
            if (!text.isEmpty()) {
                options.add(text);
                adapter.notifyDataSetChanged();
                etInput.setText("");
                wheelView.setOptions(options);
            }
        });

        new AlertDialog.Builder(requireContext())
                .setTitle("Настройка вариантов")
                .setView(dialogView)
                .setPositiveButton("Сохранить", (d, w) -> saveOptionsToDb())
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void saveOptionsToDb() {
        if (currentGame != null) {
            String combined = TextUtils.join("|", options);
            currentGame.setDescription(combined);
            gameViewModel.update(currentGame);
            Toast.makeText(getContext(), "Список сохранен!", Toast.LENGTH_SHORT).show();
        }
    }

    private void spinWheel() {
        isSpinning = true;
        Random random = new Random();
        int randomDegrees = random.nextInt(3600) + 720;

        RotateAnimation rotate = new RotateAnimation(0, randomDegrees,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotate.setDuration(3000);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new DecelerateInterpolator());

        rotate.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {}

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                isSpinning = false;
                if (options.isEmpty()) return;

                float finalAngle = (360 - (randomDegrees % 360)) % 360;
                int index = (int) (finalAngle / (360f / options.size()));

                if (index < options.size()) {
                    showResultDialog(options.get(index));
                }
            }
            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {}
        });

        wheelView.startAnimation(rotate);
    }

    private void showResultDialog(String result) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Результат")
                .setMessage("Выпало: " + result)
                .setPositiveButton("OK", null)
                .show();
    }
}