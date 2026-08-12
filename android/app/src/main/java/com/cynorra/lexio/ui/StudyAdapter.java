package com.cynorra.lexio.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cynorra.lexio.R;
import com.cynorra.lexio.model.StudyTopic;
import java.util.List;

public class StudyAdapter extends RecyclerView.Adapter<StudyAdapter.ViewHolder> {
    public interface OnTopicClickListener {
        void onTopicClick(StudyTopic topic);
    }

    private final List<StudyTopic> topics;
    private final OnTopicClickListener listener;

    public StudyAdapter(List<StudyTopic> topics, OnTopicClickListener listener) {
        this.topics = topics;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudyTopic topic = topics.get(position);
        holder.tvCategory.setText(topic.getCategory());
        holder.tvTitle.setText(topic.getTitle());
        holder.tvDesc.setText(topic.getShortDesc());
        holder.tvTime.setText("⏱ " + topic.getReadTimeMinutes() + " min");

        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_slide_up);
        holder.itemView.startAnimation(animation);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTopicClick(topic);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvTitle, tvDesc, tvTime;

        ViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvStudyCardCategory);
            tvTitle = itemView.findViewById(R.id.tvStudyCardTitle);
            tvDesc = itemView.findViewById(R.id.tvStudyCardDesc);
            tvTime = itemView.findViewById(R.id.tvStudyCardTime);
        }
    }
}
