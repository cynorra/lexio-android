package com.lexio.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.lexio.app.R;
import com.lexio.app.model.LeaderboardEntry;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    private final List<LeaderboardEntry> entries;

    public LeaderboardAdapter(List<LeaderboardEntry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardEntry entry = entries.get(position);
        
        // Custom Rank text / Medal icon
        if (entry.getRank() == 1) {
            holder.tvRank.setText("🥇");
        } else if (entry.getRank() == 2) {
            holder.tvRank.setText("🥈");
        } else if (entry.getRank() == 3) {
            holder.tvRank.setText("🥉");
        } else {
            holder.tvRank.setText(String.valueOf(entry.getRank()));
        }

        holder.tvUser.setText(entry.getUsername());
        holder.tvScore.setText(entry.getScore() + " Puan");

        // Highlight current user
        if (entry.isCurrentUser()) {
            holder.tvYou.setVisibility(View.VISIBLE);
            holder.cardView.setStrokeColor(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.accent_primary));
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.card_bg));
        } else {
            holder.tvYou.setVisibility(View.GONE);
            holder.cardView.setStrokeColor(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.card_border));
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.card_bg));
        }
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvRank, tvUser, tvYou, tvScore;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cvLeaderboardRow);
            tvRank = itemView.findViewById(R.id.tvLeaderboardRank);
            tvUser = itemView.findViewById(R.id.tvLeaderboardUser);
            tvYou = itemView.findViewById(R.id.tvLeaderboardYou);
            tvScore = itemView.findViewById(R.id.tvLeaderboardScore);
        }
    }
}
