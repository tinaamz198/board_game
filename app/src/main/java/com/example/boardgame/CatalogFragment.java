package com.example.boardgame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class CatalogFragment extends Fragment {
    private GameViewModel gameViewModel;
    private FeaturedPagerAdapter featuredAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        // 1. Инициализация ViewPager2
        ViewPager2 viewPager = view.findViewById(R.id.vpFeaturedGames);

        // 2. Настройка адаптера карусели
        featuredAdapter = new FeaturedPagerAdapter(game -> {
            // Клик по карточке для перехода в детали
            Bundle bundle = new Bundle();
            bundle.putInt("id", game.getId());
            bundle.putString("title", game.getTitle());
            bundle.putString("desc", game.getDescription());
            bundle.putString("image", game.getImagePath());
            bundle.putString("players", game.getPlayers());
            bundle.putString("difficulty", game.getDifficulty());
            bundle.putString("category", game.getCategory());

            Navigation.findNavController(requireView()).navigate(R.id.action_catalogFragment_to_gameDetailFragment, bundle);
        });

        // Если в FeaturedPagerAdapter есть метод для лайка, добавь его здесь:
        // featuredAdapter.setOnFavoriteClickListener(game -> gameViewModel.update(game));

        viewPager.setAdapter(featuredAdapter);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        // --- ВИЗУАЛЬНАЯ НАСТРОЙКА КАРУСЕЛИ ---
        viewPager.setOffscreenPageLimit(3);
        viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        viewPager.setPageTransformer(transformer);
        // -------------------------------------

        // 3. Подписка на данные (ОДИН РАЗ)
        gameViewModel.getAllGames().observe(getViewLifecycleOwner(), games -> {
            if (games != null && !games.isEmpty()) {
                List<BoardGame> featured = new ArrayList<>();
                for (BoardGame g : games) {
                    // Фильтр для карусели: избранные или "Свои вопросы"
                    if (g.isFeatured() || "Свои вопросы".equalsIgnoreCase(g.getCategory())) {
                        featured.add(g);
                    }
                }

                // Если список пуст, берем первые 5 игр как "рекомендованные"
                if (featured.isEmpty()) {
                    for (int i = 0; i < Math.min(games.size(), 5); i++) {
                        featured.add(games.get(i));
                    }
                }
                featuredAdapter.setGames(featured);
            }
        });

        // 4. Кнопка "Весь каталог"
        View btnFull = view.findViewById(R.id.btnFullCatalog);
        if (btnFull != null) {
            btnFull.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.action_catalogFragment_to_fullCatalogFragment)
            );
        }

        // 5. Кнопка "Фильтр" (Исправлено)
        View btnFilter = view.findViewById(R.id.btnFilter);
        if (btnFilter != null) {
            btnFilter.setOnClickListener(v ->
                    // ВНИМАНИЕ: Проверь ID действия в nav_graph! Обычно это action_catalogFragment_to_filterBottomSheet
                    Navigation.findNavController(v).navigate(R.id.action_fullCatalogFragment_to_filterBottomSheet)
            );
        }
    }
}