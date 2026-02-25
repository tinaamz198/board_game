package com.example.boardgame.catalog;

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

import com.example.boardgame.GameAdapter;
import com.example.boardgame.R;
import com.example.boardgame.database.BoardGame;
import com.example.boardgame.base.GameViewModel;

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

        gameViewModel.getAllGames().observe(getViewLifecycleOwner(), games -> {
            if (games != null && !games.isEmpty()) {

                // --- ВЕРХНЯЯ КАРУСЕЛЬ: СЛУЧАЙНЫЕ ИГРЫ ---
                List<BoardGame> randomGames = new ArrayList<>(games);
                java.util.Collections.shuffle(randomGames); // Перемешиваем весь список

                // Берем, например, первые 5 случайных игр для показа
                List<BoardGame> featured = randomGames.subList(0, Math.min(5, randomGames.size()));
                featuredAdapter.setGames(featured);

                // --- НИЖНИЙ СПИСОК: ТОЛЬКО "СЫГРАТЬ" ---
                List<BoardGame> quickPlayGames = new ArrayList<>();
                for (BoardGame game : games) {
                    String title = game.getTitle().trim();
                    // Отбираем только нужные три игры для нижнего списка
                    if (title.equalsIgnoreCase("Мафия") ||
                            title.equalsIgnoreCase("Рандомайзер") ||
                            title.equalsIgnoreCase("Мои вопросы")) {
                        quickPlayGames.add(game);
                    }
                }
                // Устанавливаем эти 3 игры в нижний адаптер
                popularAdapter.setGames(quickPlayGames);
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