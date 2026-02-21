package com.example.boardgame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.ArrayList;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private List<BoardGame> gameList;
    private List<BoardGame> gameListFull;
    private OnItemClickListener listener;
    private OnFavoriteClickListener favoriteClickListener;

    public interface OnItemClickListener {
        void onItemClick(BoardGame game);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(BoardGame game);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteClickListener = listener;
    }

    public GameAdapter(List<BoardGame> gameList) {
        this.gameList = gameList;
        this.gameListFull = new ArrayList<>(gameList != null ? gameList : new ArrayList<>());
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        BoardGame currentGame = gameList.get(position);

        holder.titleText.setText(currentGame.getTitle());
        holder.descText.setText(currentGame.getDescription());
        holder.gameImage.setImageResource(currentGame.getImageResource());

        // Правильная установка иконки
        if (currentGame.isFavorite()) {
            holder.imgFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.imgFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(currentGame);
        });

        // Клик по звездочке с мгновенным обновлением иконки
        holder.imgFavorite.setOnClickListener(v -> {
            if (favoriteClickListener != null) {
                // 1. Меняем состояние в самой модели
                currentGame.setFavorite(!currentGame.isFavorite());

                // 2. Сообщаем фрагменту, чтобы он сохранил это в базу
                favoriteClickListener.onFavoriteClick(currentGame);

                // 3. ВАЖНО: говорим адаптеру перерисовать эту карточку
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameList == null ? 0 : gameList.size();
    }

    public void setGames(List<BoardGame> games) {
        this.gameList = games;
        // Обновляем список для поиска, чтобы новые игры тоже искались
        this.gameListFull = new ArrayList<>(games != null ? games : new ArrayList<>());
        notifyDataSetChanged();
    }

    public void filter(String text) {
        if (gameListFull == null) return;
        List<BoardGame> filteredList = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            filteredList.addAll(gameListFull);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (BoardGame item : gameListFull) {
                if (item.getTitle().toLowerCase().contains(filterPattern)) {
                    filteredList.add(item);
                }
            }
        }
        this.gameList = filteredList;
        notifyDataSetChanged();
    }

    public BoardGame getGameAt(int position) {
        return gameList.get(position);
    }

    public static class GameViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, descText;
        ImageView gameImage, imgFavorite;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.gameTitle);
            descText = itemView.findViewById(R.id.gameDescription);
            gameImage = itemView.findViewById(R.id.gameImage);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);
        }
    }
}