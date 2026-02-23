package com.example.boardgame;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private List<BoardGame> gameList = new ArrayList<>();
    private List<BoardGame> gameListFull = new ArrayList<>();
    private OnItemClickListener listener;
    private OnFavoriteClickListener favoriteClickListener;

    public interface OnItemClickListener { void onItemClick(BoardGame game); }
    public interface OnFavoriteClickListener { void onFavoriteClick(BoardGame game); }

    public void setOnItemClickListener(OnItemClickListener listener) { this.listener = listener; }
    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) { this.favoriteClickListener = listener; }

    public GameAdapter(List<BoardGame> gameList) {
        this.gameList = gameList;
        this.gameListFull = new ArrayList<>(gameList);
    }

    public void setGames(List<BoardGame> games) {
        this.gameList = new ArrayList<>(games);
        this.gameListFull = new ArrayList<>(games);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        List<BoardGame> filtered = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            filtered.addAll(gameListFull);
        } else {
            String pattern = text.toLowerCase().trim();
            for (BoardGame item : gameListFull) {
                if (item.getTitle().toLowerCase().contains(pattern)) {
                    filtered.add(item);
                }
            }
        }
        this.gameList = filtered;
        notifyDataSetChanged();
    }

    public void filterAdvanced(String category, String difficulty) {
        List<BoardGame> filtered = new ArrayList<>();
        for (BoardGame game : gameListFull) {
            // Проверка категории (учитываем вариант "Все")
            boolean matchCat = category == null || category.isEmpty() || category.equalsIgnoreCase("Все")
                    || game.getCategory().equalsIgnoreCase(category.trim());

            // Проверка сложности
            boolean matchDiff = difficulty == null || difficulty.isEmpty() || difficulty.equalsIgnoreCase("Все")
                    || game.getDifficulty().equalsIgnoreCase(difficulty.trim());

            if (matchCat && matchDiff) {
                filtered.add(game);
            }
        }
        this.gameList = filtered;
        notifyDataSetChanged();
    }

    public BoardGame getGameAt(int position) {
        if (position >= 0 && position < gameList.size()) return gameList.get(position);
        return null;
    }

    public void sort(boolean ascending) {
        Collections.sort(gameList, (g1, g2) -> {
            if (ascending) {
                return g1.getTitle().compareToIgnoreCase(g2.getTitle());
            } else {
                return g2.getTitle().compareToIgnoreCase(g1.getTitle());
            }
        });
        notifyDataSetChanged();
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

        String path = currentGame.getImagePath();
        if (path == null || path.equals("no_photo") || path.isEmpty()) {
            holder.gameImage.setImageResource(R.drawable.game1);
        } else {
            int resId = holder.itemView.getContext().getResources().getIdentifier(path, "drawable", holder.itemView.getContext().getPackageName());
            if (resId != 0) holder.gameImage.setImageResource(resId);
            else holder.gameImage.setImageURI(Uri.parse(path));
        }

        holder.imgFavorite.setColorFilter(currentGame.isFavorite() ? Color.parseColor("#FFD60A") : Color.parseColor("#8E8E93"));
        holder.imgFavorite.setOnClickListener(v -> {
            if (favoriteClickListener != null) {
                currentGame.setFavorite(!currentGame.isFavorite());
                favoriteClickListener.onFavoriteClick(currentGame);
                notifyItemChanged(position);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(currentGame);
        });
    }

    @Override
    public int getItemCount() { return gameList.size(); }

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