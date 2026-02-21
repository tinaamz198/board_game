package com.example.boardgame; // Проверь, что это имя твоего пакета

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GameDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Привязываем наш XML файл с дизайном
        return inflater.inflate(R.layout.fragment_game_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        View btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Эта команда просто возвращает на предыдущий экран
            androidx.navigation.Navigation.findNavController(view).navigateUp();
        });

        // 1. Находим все элементы дизайна по их ID из XML
        ImageView imageView = view.findViewById(R.id.detailImage);
        TextView titleView = view.findViewById(R.id.detailTitle);
        TextView descView = view.findViewById(R.id.detailDescription);
        Button btnPlay = view.findViewById(R.id.btnPlay);

        // 2. Получаем данные ("пакет"), которые нам передал каталог
        if (getArguments() != null) {
            String title = getArguments().getString("title");
            String desc = getArguments().getString("desc");
            int imageRes = getArguments().getInt("image");

            // 3. Устанавливаем полученные данные в наши элементы
            titleView.setText(title);
            descView.setText(desc);
            imageView.setImageResource(imageRes);
        }

        // Кнопка "Играть" (просто для красоты пока)
        btnPlay.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Скоро здесь будет игра!", Toast.LENGTH_SHORT).show();
        });
    }
}