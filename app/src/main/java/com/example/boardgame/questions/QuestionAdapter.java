package com.example.boardgame.questions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private List<String> questions;
    public QuestionAdapter(List<String> questions) { this.questions = questions; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
        View v = LayoutInflater.from(p.getContext()).inflate(android.R.layout.simple_list_item_1, p, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int p) {
        TextView tv = h.itemView.findViewById(android.R.id.text1);
        tv.setText(questions.get(p));
        tv.setTextColor(android.graphics.Color.WHITE);
    }

    @Override public int getItemCount() { return questions.size(); }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View v) { super(v); }
    }
}