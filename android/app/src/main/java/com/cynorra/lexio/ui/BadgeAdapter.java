package com.cynorra.lexio.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.cynorra.lexio.R;
import com.cynorra.lexio.model.BadgeData;

import java.util.List;

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.ViewHolder> {
    private final List<BadgeData.Badge> badges;

    public BadgeAdapter(List<BadgeData.Badge> badges) {
        this.badges = badges;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_badge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BadgeData.Badge badge = badges.get(position);
        holder.tvIcon.setText(badge.getIcon());
        holder.tvTitle.setText(badge.getTitle());
        holder.tvDesc.setText(badge.getDescription());

        if (badge.isUnlocked()) {
            holder.cardView.setAlpha(1.0f);
            holder.cardView.setStrokeColor(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.accent_primary));
            holder.tvTitle.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_primary));
        } else {
            holder.cardView.setAlpha(0.45f);
            holder.cardView.setStrokeColor(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.card_border));
            holder.tvTitle.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_muted));
        }
    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvIcon, tvTitle, tvDesc;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cvBadgeCard);
            tvIcon = itemView.findViewById(R.id.tvBadgeIcon);
            tvTitle = itemView.findViewById(R.id.tvBadgeTitle);
            tvDesc = itemView.findViewById(R.id.tvBadgeDesc);
        }
    }
}
