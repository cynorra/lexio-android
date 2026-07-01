package com.cynorra.lexio.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cynorra.lexio.R;
import com.cynorra.lexio.model.Case;
import java.util.List;

public class CasesAdapter extends RecyclerView.Adapter<CasesAdapter.ViewHolder> {
    public interface OnCaseClickListener {
        void onCaseClick(Case selectedCase);
    }

    private final List<Case> cases;
    private final OnCaseClickListener listener;

    public CasesAdapter(List<Case> cases, OnCaseClickListener listener) {
        this.cases = cases;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_case_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Case c = cases.get(position);
        holder.tvTag.setText(c.getTagLabel());
        holder.tvTitle.setText(c.getTitle());
        holder.tvPreview.setText(c.getPreview());
        holder.tvRef.setText("📋 " + c.getRef());
        holder.tvDuration.setText("⏱ " + c.getDuration());
        holder.tvDifficulty.setText("📊 " + c.getDifficulty());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCaseClick(c);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cases.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag, tvTitle, tvPreview, tvRef, tvDuration, tvDifficulty;

        ViewHolder(View itemView) {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tvCardTag);
            tvTitle = itemView.findViewById(R.id.tvCardTitle);
            tvPreview = itemView.findViewById(R.id.tvCardPreview);
            tvRef = itemView.findViewById(R.id.tvCardRef);
            tvDuration = itemView.findViewById(R.id.tvCardDuration);
            tvDifficulty = itemView.findViewById(R.id.tvCardDifficulty);
        }
    }
}
