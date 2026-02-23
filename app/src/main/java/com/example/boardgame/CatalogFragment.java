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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class CatalogFragment extends Fragment {
    private GameViewModel gameViewModel;
    private FeaturedPagerAdapter featuredAdapter;
    private GameAdapter popularAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        setupFeaturedCarousel(view);
        setupPopularList(view);

        // ГЛАВНОЕ ИЗМЕНЕНИЕ: Используем только getAllGames()
        gameViewModel.getAllGames().observe(getViewLifecycleOwner(), games -> {
            if (games != null && !games.isEmpty()) {

                // 1. ВЕРХНЯЯ КАРТОЧКА (Самая новая игра)
                List<BoardGame> latest = new ArrayList<>();
                latest.add(games.get(0));
                featuredAdapter.setGames(latest);

                // 2. НИЖНИЙ СПИСОК (Следующие 5 игр после самой новой)
                if (games.size() > 1) {
                    List<BoardGame> recentOnes = new ArrayList<>();
                    // Начинаем с i=1, чтобы самая новая игра не дублировалась внизу
                    for (int i = 1; i < Math.min(games.size(), 6); i++) {
                        recentOnes.add(games.get(i));
                    }
                    popularAdapter.setGames(recentOnes);
                }
            }
        });

        View btnFull = view.findViewById(R.id.btnFullCatalog);
        if (btnFull != null) {
            btnFull.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.action_catalogFragment_to_fullCatalogFragment)
            );
        }
    }

    private void setupFeaturedCarousel(View view) {
        ViewPager2 viewPager = view.findViewById(R.id.vpFeaturedGames);
        featuredAdapter = new FeaturedPagerAdapter(game -> openGameDetail(game, view));
        viewPager.setAdapter(featuredAdapter);
        viewPager.setOffscreenPageLimit(3);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        viewPager.setPageTransformer(transformer);
    }

    private void setupPopularList(View view) {
        RecyclerView rvPopular = view.findViewById(R.id.rvPopularGames);
        popularAdapter = new GameAdapter(new ArrayList<>());
        rvPopular.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPopular.setAdapter(popularAdapter);
        popularAdapter.setOnItemClickListener(game -> openGameDetail(game, view));
        popularAdapter.setOnFavoriteClickListener(game -> gameViewModel.update(game));
    }

    private void openGameDetail(BoardGame game, View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", game.getId());
        bundle.putString("title", game.getTitle());
        bundle.putString("desc", game.getDescription());
        bundle.putString("image", game.getImagePath());
        bundle.putString("players", game.getPlayers());
        bundle.putString("difficulty", game.getDifficulty());
        bundle.putString("category", game.getCategory());
        Navigation.findNavController(view).navigate(R.id.action_catalogFragment_to_gameDetailFragment, bundle);
    }
}