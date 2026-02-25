package com.example.boardgame.mafia;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.R;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MafiaGameFragment extends Fragment {
    private List<String> shuffledRoles = new ArrayList<>();
    private int currentPlayerIndex = 0;
    private boolean isRoleVisible = false;

    private View cardFront, cardBack;
    private TextView tvPlayerNumber, tvRoleTitle, tvRoleDesc;
    private Button btnAction;

    public MafiaGameFragment() { super(R.layout.fragment_mafia_game); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardFront = view.findViewById(R.id.cardFront);
        cardBack = view.findViewById(R.id.cardBack);
        tvPlayerNumber = view.findViewById(R.id.tvPlayerNumber);
        tvRoleTitle = view.findViewById(R.id.tvRoleTitle);
        tvRoleDesc = view.findViewById(R.id.tvRoleDesc);
        btnAction = view.findViewById(R.id.btnAction);

        view.findViewById(R.id.btnBackFromGame).setOnClickListener(v -> {
            // Возвращаемся на экран настройки ролей
            Navigation.findNavController(v).popBackStack();
        });

        // Получаем роли из аргументов
        if (getArguments() != null) {
            String[] roles = getArguments().getStringArray("rolesList");
            if (roles != null) {
                shuffledRoles = new ArrayList<>(Arrays.asList(roles));
                Collections.shuffle(shuffledRoles);
            }
        }

        btnAction.setOnClickListener(v -> {
            if (!isRoleVisible) {
                // ПОКАЗЫВАЕМ РОЛЬ
                showRole(shuffledRoles.get(currentPlayerIndex));
            } else {
                // ПЕРЕХОДИМ К СЛЕДУЮЩЕМУ
                prepareNextPlayer();
            }
        });
    }

    private void showRole(String role) {
        isRoleVisible = true;
        tvRoleTitle.setText(role);

        // Устанавливаем цвет и описание в зависимости от роли
        if (role.equals("Мафия")) tvRoleTitle.setTextColor(Color.RED);
        else tvRoleTitle.setTextColor(Color.GREEN);

        cardBack.setVisibility(View.GONE);
        cardFront.setVisibility(View.VISIBLE);
        btnAction.setText("СКРЫТЬ РОЛЬ");
    }

    private void prepareNextPlayer() {
        currentPlayerIndex++;
        if (currentPlayerIndex < shuffledRoles.size()) {
            isRoleVisible = false;
            cardFront.setVisibility(View.GONE);
            cardBack.setVisibility(View.VISIBLE);
            tvPlayerNumber.setText("Игрок " + (currentPlayerIndex + 1));
            btnAction.setText("УЗНАТЬ РОЛЬ");
        } else {
            tvPlayerNumber.setText("Все роли розданы!");
            btnAction.setText("ВЕРНУТЬСЯ В МЕНЮ");
            btnAction.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        }
    }
}