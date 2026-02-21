package com.example.boardgame;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class EntranceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Связываем код с твоим файлом fragmententer.xml
        return inflater.inflate(R.layout.fragmententer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Таймер на 3 секунды (3000 мс)
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Проверка, что пользователь не закрыл приложение раньше времени
                if (isAdded()) {
                    // Переход к каталогу.
                    // Убедись, что ID стрелки в nav_graph совпадает с этим:
                    Navigation.findNavController(view)
                            .navigate(R.id.action_entranceFragment_to_catalogFragment);
                }
            }
        }, 3000);
    }
}