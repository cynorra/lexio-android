package com.cynorra.lexio.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cynorra.lexio.R;
import com.cynorra.lexio.model.Workspace;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.List;

public class WorkspaceAdapter extends RecyclerView.Adapter<WorkspaceAdapter.ViewHolder> {
    private final List<Workspace> workspaces;
    private int lastPosition = -1;

    public WorkspaceAdapter(List<Workspace> workspaces) {
        this.workspaces = workspaces;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workspace, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Workspace ws = workspaces.get(position);
        holder.tvWorkspaceIcon.setText(ws.getIconEmoji());
        holder.tvWorkspaceName.setText(ws.getName());
        holder.tvWorkspaceDesc.setText(ws.getDescription());
        holder.tvMemberCount.setText(ws.getMemberCount() + " üye");
        
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_slide_up);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return workspaces.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWorkspaceIcon, tvWorkspaceName, tvWorkspaceDesc, tvMemberCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWorkspaceIcon = itemView.findViewById(R.id.tvWorkspaceIcon);
            tvWorkspaceName = itemView.findViewById(R.id.tvWorkspaceName);
            tvWorkspaceDesc = itemView.findViewById(R.id.tvWorkspaceDesc);
            tvMemberCount = itemView.findViewById(R.id.tvMemberCount);
        }
    }
}
