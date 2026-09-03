package com.cynorra.lexio.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cynorra.lexio.R;
import com.cynorra.lexio.model.FeedPost;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private final List<FeedPost> posts;
    private int lastPosition = -1;

    public FeedAdapter(List<FeedPost> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeedPost post = posts.get(position);
        Context context = holder.itemView.getContext();
        holder.tvAuthorName.setText(post.getAuthorName());
        holder.tvAuthorRole.setText(post.getAuthorRole());
        holder.tvTimestamp.setText(formatRelativeTime(context, post));
        holder.tvContent.setText(post.getContent());
        holder.tvLikes.setText(context.getString(R.string.feed_likes_count, post.getLikesCount()));
        holder.tvComments.setText(context.getString(R.string.feed_comments_count, post.getCommentsCount()));

        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_slide_up);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    /** Computes a live relative-time label from createdAt; falls back to the stored
     *  timestamp string for legacy posts that predate the createdAt field. */
    private static String formatRelativeTime(Context context, FeedPost post) {
        long createdAt = post.getCreatedAt();
        if (createdAt <= 0) {
            String fallback = post.getTimestamp();
            return fallback != null ? fallback : "";
        }

        long diffMs = System.currentTimeMillis() - createdAt;
        long minutes = diffMs / (60 * 1000);
        if (minutes < 1) return context.getString(R.string.feed_time_now);
        if (minutes < 60) return context.getString(R.string.feed_time_minutes, minutes);
        long hours = minutes / 60;
        if (hours < 24) return context.getString(R.string.feed_time_hours, hours);
        long days = hours / 24;
        if (days < 7) return context.getString(R.string.feed_time_days, days);
        long weeks = days / 7;
        return context.getString(R.string.feed_time_weeks, weeks);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthorName, tvAuthorRole, tvTimestamp, tvContent, tvLikes, tvComments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorName);
            tvAuthorRole = itemView.findViewById(R.id.tvAuthorRole);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvComments = itemView.findViewById(R.id.tvComments);
        }
    }
}
