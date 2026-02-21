package com.example.boardgame;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Находим фрагмент-контейнер, в котором будут меняться экраны
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // 2. Находим наше нижнее меню
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

            // 3. Магия: связываем меню с контроллером навигации
            NavigationUI.setupWithNavController(bottomNav, navController);
        }
    }
}