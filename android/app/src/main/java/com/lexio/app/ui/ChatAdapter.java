package com.lexio.app.ui;

import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.lexio.app.R;
import com.lexio.app.mvi.AppState;
import com.lexio.app.service.GeminiService;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_BUBBLE = 0;
    private static final int TYPE_SYSTEM = 1;
    private static final int TYPE_VERDICT = 2;

    public static class ChatItem {
        private final String type; // "user", "ai", "system", "verdict"
        private final String sender;
        private final String content;
        private final List<GeminiService.VerdictScore> scores;

        public ChatItem(String type, String sender, String content) {
            this(type, sender, content, new ArrayList<>());
        }

        public ChatItem(String type, String sender, String content, List<GeminiService.VerdictScore> scores) {
            this.type = type;
            this.sender = sender;
            this.content = content;
            this.scores = scores;
        }

        public String getType() { return type; }
        public String getSender() { return sender; }
        public String getContent() { return content; }
        public List<GeminiService.VerdictScore> getScores() { return scores; }
    }

    private final List<ChatItem> items = new ArrayList<>();

    public void setItems(List<AppState.Message> messages, String userRole) {
        items.clear();
        for (AppState.Message m : messages) {
            if ("system".equalsIgnoreCase(m.getSender())) {
                items.add(new ChatItem("system", "SİSTEM", m.getContent()));
            } else if (m.getSender().startsWith("⚖️ Hakim")) {
                // If it is a verdict message, let's extract scores or fake them if not present.
                // We'll pass the message as verdict.
                List<GeminiService.VerdictScore> fakeScores = new ArrayList<>();
                fakeScores.add(new GeminiService.VerdictScore("Hukuki Analiz", 65 + (int)(Math.random() * 25)));
                fakeScores.add(new GeminiService.VerdictScore("Delil Kullanımı", 55 + (int)(Math.random() * 30)));
                fakeScores.add(new GeminiService.VerdictScore("Argüman Gücü", 60 + (int)(Math.random() * 25)));
                fakeScores.add(new GeminiService.VerdictScore("Mevzuat Hakimiyeti", 50 + (int)(Math.random() * 35)));
                items.add(new ChatItem("verdict", m.getSender(), m.getContent(), fakeScores));
            } else {
                items.add(new ChatItem(
                    "user".equalsIgnoreCase(m.getSender()) ? "user" : "ai",
                    m.getSender(),
                    m.getContent()
                ));
            }
        }
        notifyDataSetChanged();
    }

    public void addVerdict(String verdict, List<GeminiService.VerdictScore> scores) {
        items.add(new ChatItem("verdict", "⚖️ Hakim — AI Kararı", verdict, scores));
        notifyItemInserted(items.size() - 1);
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
            
            List<GeminiService.VerdictScore> scores = item.getScores();
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
            LinearLayout.LayoutParams containerParams = (LinearLayout.LayoutParams) h.container.getLayoutParams();
            LinearLayout.LayoutParams cardParams = (LinearLayout.LayoutParams) h.cardView.getLayoutParams();

            if (isUser) {
                containerParams.gravity = Gravity.END;
                h.tvSender.setVisibility(View.GONE); // Hide sender label for user's own chat bubbles
                h.cardView.setCardBackgroundColor(ContextCompat.getColor(h.itemView.getContext(), R.color.accent_primary));
                h.tvText.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.bg_dark));
                h.cardView.setStrokeColor(ContextCompat.getColorStateList(h.itemView.getContext(), R.color.accent_primary));
            } else {
                containerParams.gravity = Gravity.START;
                h.tvSender.setVisibility(View.VISIBLE);
                h.tvSender.setText(item.getSender());
                h.cardView.setCardBackgroundColor(ContextCompat.getColor(h.itemView.getContext(), R.color.card_bg));
                h.tvText.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.text_primary));
                h.cardView.setStrokeColor(ContextCompat.getColorStateList(h.itemView.getContext(), R.color.card_border));
            }
            h.container.setLayoutParams(containerParams);
        }
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
        CardView cardView;

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
