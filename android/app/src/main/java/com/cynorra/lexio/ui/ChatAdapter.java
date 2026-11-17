package com.cynorra.lexio.ui;

import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.cynorra.lexio.R;
import com.cynorra.lexio.SimTexts;
import com.cynorra.lexio.model.Case;
import com.cynorra.lexio.mvi.AppState;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_BUBBLE = 0;
    private static final int TYPE_SYSTEM = 1;
    private static final int TYPE_VERDICT = 2;

    /** A single performance metric shown on the verdict card, derived from the real
     *  scoreEffect values earned during the simulation (not AI-generated). */
    public static class MetricScore {
        private final String label;
        private final int value;

        public MetricScore(String label, int value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() { return label; }
        public int getValue() { return value; }
    }

    public static class ChatItem {
        private final String type; // "user", "ai", "system", "verdict"
        private final String sender;
        private final String content;
        private final List<MetricScore> scores;

        public ChatItem(String type, String sender, String content) {
            this(type, sender, content, new ArrayList<>());
        }

        public ChatItem(String type, String sender, String content, List<MetricScore> scores) {
            this.type = type;
            this.sender = sender;
            this.content = content;
            this.scores = scores;
        }

        public String getType() { return type; }
        public String getSender() { return sender; }
        public String getContent() { return content; }
        public List<MetricScore> getScores() { return scores; }
    }

    private final List<ChatItem> items = new ArrayList<>();
    private int lastAnimatedPosition = -1;

    public void setItems(AppState state) {
        List<AppState.Message> messages = state.getChatMessages() != null ? state.getChatMessages() : new ArrayList<>();
        Case selectedCase = state.getSelectedCase();
        String systemLabel = SimTexts.get(R.string.sim_system_label, selectedCase == null || selectedCase.usesTurkishUi());
        if (messages.size() < items.size()) lastAnimatedPosition = -1;
        items.clear();
        for (AppState.Message m : messages) {
            if ("system".equalsIgnoreCase(m.getSender())) {
                items.add(new ChatItem("system", systemLabel, safe(m.getContent())));
            } else if (m.getSender() != null && m.getSender().startsWith("⚖️")) {
                items.add(new ChatItem("verdict", m.getSender(), safe(m.getContent()), computeMetricScores(state)));
            } else {
                items.add(new ChatItem(
                    "user".equalsIgnoreCase(m.getSender()) ? "user" : "ai",
                    safe(m.getSender()),
                    safe(m.getContent())
                ));
            }
        }
        notifyDataSetChanged();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    /**
     * Derives the 4 performance metrics from the actual scoreEffect values earned per
     * trial step, normalized against the best available choice in that step. This is a
     * deterministic breakdown of real gameplay data, not an AI evaluation.
     */
    private static List<MetricScore> computeMetricScores(AppState state) {
        Case selectedCase = state.getSelectedCase();
        List<Integer> stepScores = state.getStepScores() != null ? state.getStepScores() : new ArrayList<>();
        boolean isTrCase = selectedCase == null || selectedCase.usesTurkishUi();
        String[] labels = {
            SimTexts.get(R.string.sim_metric_argument, isTrCase),
            SimTexts.get(R.string.sim_metric_evidence, isTrCase),
            SimTexts.get(R.string.sim_metric_analysis, isTrCase)};
        List<Integer> percentages = new ArrayList<>();

        if (selectedCase != null) {
            List<Case.TrialStep> steps = selectedCase.getTrialSteps();
            boolean isDefense = "savunma".equalsIgnoreCase(state.getSelectedRole());
            for (int i = 0; i < stepScores.size() && i < steps.size(); i++) {
                List<Case.Choice> choices = isDefense ? steps.get(i).getDefenseChoices() : steps.get(i).getProsecutionChoices();
                int maxPossible = 1;
                for (Case.Choice c : choices) {
                    maxPossible = Math.max(maxPossible, c.getScoreEffect());
                }
                int pct = Math.round(stepScores.get(i) * 100f / maxPossible);
                percentages.add(Math.max(0, Math.min(100, pct)));
            }
        }

        int overall = 0;
        for (int p : percentages) overall += p;
        overall = percentages.isEmpty() ? 0 : overall / percentages.size();

        List<MetricScore> result = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            int value = i < percentages.size() ? percentages.get(i) : overall;
            result.add(new MetricScore(labels[i], value));
        }
        result.add(new MetricScore(SimTexts.get(R.string.sim_metric_statutory, isTrCase), overall));
        return result;
    }

    @Override
    public int getItemViewType(int position) {
        ChatItem item = items.get(position);
        if ("system".equalsIgnoreCase(item.getType())) {
            return TYPE_SYSTEM;
        } else if ("verdict".equalsIgnoreCase(item.getType())) {
            return TYPE_VERDICT;
        } else {
            return TYPE_BUBBLE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_SYSTEM) {
            View view = inflater.inflate(R.layout.item_chat_system, parent, false);
            return new SystemViewHolder(view);
        } else if (viewType == TYPE_VERDICT) {
            View view = inflater.inflate(R.layout.item_chat_verdict, parent, false);
            return new VerdictViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_chat_bubble, parent, false);
            return new BubbleViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatItem item = items.get(position);

        if (holder instanceof SystemViewHolder) {
            SystemViewHolder h = (SystemViewHolder) holder;
            h.tvHeader.setText(item.getSender());
            h.tvText.setText(Html.fromHtml(item.getContent(), Html.FROM_HTML_MODE_LEGACY));
        } else if (holder instanceof VerdictViewHolder) {
            VerdictViewHolder h = (VerdictViewHolder) holder;
            h.tvText.setText(Html.fromHtml(item.getContent().replace("\n", "<br/>"), Html.FROM_HTML_MODE_LEGACY));
            
            List<MetricScore> scores = item.getScores();
            if (scores.size() >= 4) {
                h.tvScoreName1.setText(scores.get(0).getLabel());
                h.tvScoreVal1.setText(scores.get(0).getValue() + "/100");
                h.pbScore1.setProgress(scores.get(0).getValue());

                h.tvScoreName2.setText(scores.get(1).getLabel());
                h.tvScoreVal2.setText(scores.get(1).getValue() + "/100");
                h.pbScore2.setProgress(scores.get(1).getValue());

                h.tvScoreName3.setText(scores.get(2).getLabel());
                h.tvScoreVal3.setText(scores.get(2).getValue() + "/100");
                h.pbScore3.setProgress(scores.get(2).getValue());

                h.tvScoreName4.setText(scores.get(3).getLabel());
                h.tvScoreVal4.setText(scores.get(3).getValue() + "/100");
                h.pbScore4.setProgress(scores.get(3).getValue());
            }
        } else if (holder instanceof BubbleViewHolder) {
            BubbleViewHolder h = (BubbleViewHolder) holder;
            h.tvSender.setText(item.getSender());
            h.tvText.setText(item.getContent());

            boolean isUser = "user".equalsIgnoreCase(item.getType());
            if (isUser) {
                h.container.setGravity(Gravity.END);
                h.tvSender.setVisibility(View.GONE); // Hide sender label for user's own chat bubbles
                h.cardView.setCardBackgroundColor(ContextCompat.getColor(h.itemView.getContext(), R.color.accent_primary));
                h.tvText.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.white));
                h.cardView.setStrokeColor(ContextCompat.getColorStateList(h.itemView.getContext(), R.color.accent_primary));
                bubbleShape(h.cardView, true);
            } else {
                h.container.setGravity(Gravity.START);
                h.tvSender.setVisibility(View.VISIBLE);
                h.tvSender.setText(item.getSender());
                h.cardView.setCardBackgroundColor(ContextCompat.getColor(h.itemView.getContext(), R.color.card_bg));
                h.tvText.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.text_primary));
                h.cardView.setStrokeColor(ContextCompat.getColorStateList(h.itemView.getContext(), R.color.card_border));
                bubbleShape(h.cardView, false);
            }
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_scale_up));
        } else {
            holder.itemView.clearAnimation();
        }
    }

    /** Chat-style bubble: all corners round except the one pointing at the speaker. */
    private static void bubbleShape(MaterialCardView card, boolean user) {
        float r = 20 * card.getResources().getDisplayMetrics().density;
        float tail = 5 * card.getResources().getDisplayMetrics().density;
        com.google.android.material.shape.ShapeAppearanceModel.Builder b = card.getShapeAppearanceModel().toBuilder()
                .setAllCornerSizes(r);
        if (user) b.setBottomRightCornerSize(tail); else b.setBottomLeftCornerSize(tail);
        card.setShapeAppearanceModel(b.build());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SystemViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader, tvText;

        SystemViewHolder(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvSystemHeader);
            tvText = itemView.findViewById(R.id.tvSystemText);
        }
    }

    static class BubbleViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        TextView tvSender, tvText;
        MaterialCardView cardView;

        BubbleViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.llBubbleContainer);
            tvSender = itemView.findViewById(R.id.tvMsgSender);
            tvText = itemView.findViewById(R.id.tvMsgText);
            cardView = itemView.findViewById(R.id.cvMsgBubble);
        }
    }

    static class VerdictViewHolder extends RecyclerView.ViewHolder {
        TextView tvText;
        TextView tvScoreName1, tvScoreVal1, tvScoreName2, tvScoreVal2, tvScoreName3, tvScoreVal3, tvScoreName4, tvScoreVal4;
        ProgressBar pbScore1, pbScore2, pbScore3, pbScore4;

        VerdictViewHolder(View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvVerdictText);
            tvScoreName1 = itemView.findViewById(R.id.tvScoreName1);
            tvScoreVal1 = itemView.findViewById(R.id.tvScoreVal1);
            pbScore1 = itemView.findViewById(R.id.pbScore1);

            tvScoreName2 = itemView.findViewById(R.id.tvScoreName2);
            tvScoreVal2 = itemView.findViewById(R.id.tvScoreVal2);
            pbScore2 = itemView.findViewById(R.id.pbScore2);

            tvScoreName3 = itemView.findViewById(R.id.tvScoreName3);
            tvScoreVal3 = itemView.findViewById(R.id.tvScoreVal3);
            pbScore3 = itemView.findViewById(R.id.pbScore3);

            tvScoreName4 = itemView.findViewById(R.id.tvScoreName4);
            tvScoreVal4 = itemView.findViewById(R.id.tvScoreVal4);
            pbScore4 = itemView.findViewById(R.id.pbScore4);
        }
    }
}
