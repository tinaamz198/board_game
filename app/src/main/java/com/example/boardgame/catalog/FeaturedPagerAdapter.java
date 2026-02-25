package com.example.boardgame.catalog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.R;
import com.example.boardgame.database.BoardGame;

import java.util.ArrayList;
import java.util.List;

public class FeaturedPagerAdapter extends RecyclerView.Adapter<FeaturedPagerAdapter.GameViewHolder> {

    private List<BoardGame> games = new ArrayList<>();
    private final OnGameClickListener listener;

    public interface OnGameClickListener {
        void onGameClick(BoardGame game);
    }

    public FeaturedPagerAdapter(OnGameClickListener listener) {
        this.listener = listener;
    }

    public void setGames(List<BoardGame> games) {
        // Добавлена проверка на null для безопасности
        this.games = (games != null) ? games : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apple_card, parent, false);
        return new GameViewHolder(view);
    }
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        if (!games.isEmpty()) {
            BoardGame game = games.get(position);
            holder.title.setText(game.getTitle());
            holder.description.setText(game.getDescription());

            // Загрузка фото по имени из drawable
            String path = game.getImagePath();
            int resId = holder.itemView.getContext().getResources().getIdentifier(
                    path, "drawable", holder.itemView.getContext().getPackageName());

            if (resId != 0) {
                holder.image.setImageResource(resId);
            } else {
                holder.image.setImageResource(R.drawable.game1); // Твоя стандартная заглушка
            }

            holder.itemView.setOnClickListener(v -> listener.onGameClick(game));
        }
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public static class GameViewHolder extends RecyclerView.ViewHolder {
        private final TextView title, description;
        private final ImageView image;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.gameTitle);
            description = itemView.findViewById(R.id.gameDescription);
            image = itemView.findViewById(R.id.gameImage);
        }

        public void bind(BoardGame game, OnGameClickListener listener) {
            title.setText(game.getTitle());
            description.setText(game.getDescription());

            // ИСПРАВЛЕНО: Базовая обработка изображений
            // Если ты хранишь путь к файлу или ресурс
            if (game.getImagePath() != null && !game.getImagePath().isEmpty()) {
                // Если это имя ресурса из drawable (например, "game1")
                int resId = itemView.getContext().getResources().getIdentifier(
                        game.getImagePath(), "drawable", itemView.getContext().getPackageName());

                if (resId != 0) {
                    image.setImageResource(resId);
                } else {
                    // Ставим заглушку, если картинка не найдена
                    image.setImageResource(R.drawable.ic_launcher_background);
                }
            } else {
                image.setImageResource(R.drawable.ic_launcher_background);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onGameClick(game);
                }
            });
        }
    }
}