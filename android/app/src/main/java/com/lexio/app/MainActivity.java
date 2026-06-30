package com.lexio.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import com.lexio.app.model.Case;
import com.lexio.app.model.CasesData;
import com.lexio.app.model.LeaderboardEntry;
import com.lexio.app.model.StudyData;
import com.lexio.app.model.StudyTopic;
import com.lexio.app.model.FeedData;
import com.lexio.app.model.WorkspaceData;
import com.lexio.app.mvi.AppEvent;
import com.lexio.app.mvi.AppState;
import com.lexio.app.mvi.AppStore;
import com.lexio.app.service.ContentSyncService;
import com.lexio.app.ui.CasesAdapter;
import com.lexio.app.ui.ChatAdapter;
import com.lexio.app.ui.LeaderboardAdapter;
import com.lexio.app.ui.StudyAdapter;
import com.lexio.app.ui.FeedAdapter;
import com.lexio.app.ui.WorkspaceAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AppStore.Observer {
    private static final int RC_SIGN_IN = 9001;

    private AppStore store;
    private GoogleSignInClient googleSignInClient;
    private ContentSyncService syncService;
    private AppState.Screen activeScreen = null;
    private ChatAdapter chatAdapter;

    private int userScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        store = new AppStore();
        syncService = new ContentSyncService();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("644546865365-0nac1q45eumek8drpfq9fi3vlccee6d3.apps.googleusercontent.com")
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Start dynamic content syncing
        syncService.syncContent(success -> {
            if (success) {
                Toast.makeText(MainActivity.this, "İçerikler güncellendi!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Çevrimdışı mod aktif.", Toast.LENGTH_SHORT).show();
            }
        });

        // Subscribe to state updates
        store.subscribe(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        store.unsubscribe(this);
    }

    @Override
    public void onStateChanged(AppState state) {
        if (activeScreen != state.getActiveScreen()) {
            activeScreen = state.getActiveScreen();
            setupScreenLayout(activeScreen, state);
        }
        updateScreenState(activeScreen, state);
    }

    private void setupScreenLayout(AppState.Screen screen, AppState state) {
        switch (screen) {
            case LOGIN:
                setContentView(R.layout.layout_login);
                setupLoginViews();
                break;
            case LANDING:
                setContentView(R.layout.layout_landing);
                setupLandingViews();
                break;
            case CASE_LIST:
                setContentView(R.layout.layout_case_list);
                setupCaseListViews();
                setupBottomNavigation();
                loadBannerAd();
                break;
            case CASE_DETAIL:
                setContentView(R.layout.layout_case_detail);
                setupCaseDetailViews(state.getSelectedCase());
                break;
            case SIMULATION:
                setContentView(R.layout.layout_simulation);
                setupSimulationViews(state);
                break;
            case STUDY_PORTAL:
                setContentView(R.layout.layout_study_portal);
                setupStudyPortalViews();
                setupBottomNavigation();
                loadBannerAd();
                break;
            case STUDY_DETAIL:
                setContentView(R.layout.layout_study_detail);
                setupStudyDetailViews(state.getSelectedStudyTopic());
                break;
            case FEED:
                setContentView(R.layout.layout_feed);
                setupFeedViews();
                setupBottomNavigation();
                loadBannerAd();
                break;
            case WORKSPACE:
                setContentView(R.layout.layout_workspace);
                setupWorkspaceViews();
                setupBottomNavigation();
                loadBannerAd();
                break;
            case LEADERBOARD:
                setContentView(R.layout.layout_leaderboard);
                setupLeaderboardViews(state.getLeaderboardInterval());
                setupBottomNavigation();
                loadBannerAd();
                break;
        }
    }

    // Google Sign-In callback
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String name = account.getDisplayName();
                store.dispatch(new AppEvent(AppEvent.Type.LOGIN_SUCCESS, name));
            }
        } catch (ApiException e) {
            Toast.makeText(this, "Giriş başarısız: " + e.getStatusCode(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupLoginViews() {
        View btnSignIn = findViewById(R.id.btnGoogleSignIn);
        if (btnSignIn != null) {
            btnSignIn.setOnClickListener(v -> {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            });
        }
    }

    private void setupBottomNavigation() {
        View navCases = findViewById(R.id.navCases);
        View navStudy = findViewById(R.id.navStudy);
        View navLeaderboard = findViewById(R.id.navLeaderboard);
        View navFeed = findViewById(R.id.navFeed);
        View navWorkspace = findViewById(R.id.navWorkspace);

        if (navCases != null) {
            navCases.setOnClickListener(v -> store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_CASE_LIST)));
        }
        if (navStudy != null) {
            navStudy.setOnClickListener(v -> store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_STUDY_PORTAL)));
        }
        if (navLeaderboard != null) {
            navLeaderboard.setOnClickListener(v -> store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_LEADERBOARD)));
        }
        if (navFeed != null) {
            navFeed.setOnClickListener(v -> store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_FEED)));
        }
        if (navWorkspace != null) {
            navWorkspace.setOnClickListener(v -> store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_WORKSPACE)));
        }
    }

    private void setupLandingViews() {
        Button btnStart = findViewById(R.id.btnStart);
        Button btnBrowse = findViewById(R.id.btnBrowse);

        btnStart.setOnClickListener(v -> store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_CASE_LIST)));
        btnBrowse.setOnClickListener(v -> store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_CASE_LIST)));
    }

    private void setupCaseListViews() {
        RecyclerView rvCases = findViewById(R.id.rvCases);
        rvCases.setLayoutManager(new LinearLayoutManager(this));
        
        CasesAdapter adapter = new CasesAdapter(CasesData.getCases(), selectedCase -> {
            store.dispatch(new AppEvent(AppEvent.Type.SELECT_CASE, selectedCase));
        });
        rvCases.setAdapter(adapter);
    }

    private void setupCaseDetailViews(Case c) {
        if (c == null) return;

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvRef = findViewById(R.id.tvDetailRef);
        TextView tvFullText = findViewById(R.id.tvDetailFullText);
        LinearLayout llEvidence = findViewById(R.id.llEvidenceContainer);
        Button btnBack = findViewById(R.id.btnBackToCases);
        View btnDefense = findViewById(R.id.btnChooseDefense);
        View btnProsecution = findViewById(R.id.btnChooseProsecution);

        tvTitle.setText(c.getTitle());
        tvRef.setText("Yargıtay Emsal Kararı — " + c.getRef());
        tvFullText.setText(c.getFull());

        llEvidence.removeAllViews();
        for (String evidence : c.getEvidence()) {
            TextView evText = new TextView(this);
            evText.setText("• " + evidence);
            evText.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            evText.setTextSize(13);
            evText.setPadding(0, 4, 0, 4);
            llEvidence.addView(evText);
        }

        btnBack.setOnClickListener(v -> store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_CASE_LIST)));

        btnDefense.setOnClickListener(v -> {
            store.dispatch(new AppEvent(AppEvent.Type.CHOOSE_ROLE, "savunma"));
            store.dispatch(new AppEvent(AppEvent.Type.START_SIMULATION));
        });

        btnProsecution.setOnClickListener(v -> {
            store.dispatch(new AppEvent(AppEvent.Type.CHOOSE_ROLE, "savcilik"));
            store.dispatch(new AppEvent(AppEvent.Type.START_SIMULATION));
        });
    }

    private void setupSimulationViews(AppState state) {
        Case c = state.getSelectedCase();
        if (c == null) return;

        TextView tvTitle = findViewById(R.id.tvSimTitle);
        TextView tvRoleBadge = findViewById(R.id.tvSimRoleBadge);
        Button btnExit = findViewById(R.id.btnExitSimulation);
        RecyclerView rvChat = findViewById(R.id.rvChat);
        LinearLayout llSteps = findViewById(R.id.llStepsIndicatorContainer);

        tvTitle.setText(c.getTitle());
        
        boolean isDefense = "savunma".equalsIgnoreCase(state.getSelectedRole());
        tvRoleBadge.setText(isDefense ? "⚖️ Savunma Avukatı" : "🔍 Savcı");
        tvRoleBadge.setTextColor(ContextCompat.getColor(this, isDefense ? R.color.defense_green : R.color.prosecution_red));
        tvRoleBadge.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_pill_accent));

        btnExit.setOnClickListener(v -> store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_CASE_LIST)));

        rvChat.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter();
        rvChat.setAdapter(chatAdapter);

        llSteps.removeAllViews();
        List<Case.TrialStep> steps = c.getTrialSteps();
        for (int i = 0; i < steps.size() + 1; i++) {
            TextView stepView = new TextView(this);
            String name = (i < steps.size()) ? steps.get(i).getStepName() : "Karar";
            stepView.setText((i + 1) + ". " + name);
            stepView.setTextSize(12);
            stepView.setPadding(12, 6, 12, 6);
            stepView.setSingleLine(true);
            
            if (i > 0) {
                TextView arrow = new TextView(this);
                arrow.setText(" → ");
                arrow.setTextColor(ContextCompat.getColor(this, R.color.text_muted));
                arrow.setTextSize(12);
                llSteps.addView(arrow);
            }
            llSteps.addView(stepView);
        }
    }

    private void setupStudyPortalViews() {
        RecyclerView rvStudy = findViewById(R.id.rvStudyTopics);
        rvStudy.setLayoutManager(new LinearLayoutManager(this));
        
        StudyAdapter adapter = new StudyAdapter(StudyData.getTopics(), topic -> {
            store.dispatch(new AppEvent(AppEvent.Type.SELECT_STUDY_TOPIC, topic));
        });
        rvStudy.setAdapter(adapter);
    }

    private void setupStudyDetailViews(StudyTopic topic) {
        if (topic == null) return;

        TextView tvTitle = findViewById(R.id.tvStudyDetailTitle);
        TextView tvCategory = findViewById(R.id.tvStudyDetailCategory);
        TextView tvContent = findViewById(R.id.tvStudyDetailContent);
        Button btnBack = findViewById(R.id.btnBackToStudyPortal);

        tvTitle.setText(topic.getTitle());
        tvCategory.setText(topic.getCategory());
        tvContent.setText(Html.fromHtml(topic.getFullContent(), Html.FROM_HTML_MODE_LEGACY));

        btnBack.setOnClickListener(v -> store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_STUDY_PORTAL)));
    }

    private void setupLeaderboardViews(String activeInterval) {
        Button btnDaily = findViewById(R.id.btnTabDaily);
        Button btnWeekly = findViewById(R.id.btnTabWeekly);
        Button btnMonthly = findViewById(R.id.btnTabMonthly);

        btnDaily.setOnClickListener(v -> store.dispatch(new AppEvent(AppEvent.Type.SET_LEADERBOARD_INTERVAL, "daily")));
        btnWeekly.setOnClickListener(v -> store.dispatch(new AppEvent(AppEvent.Type.SET_LEADERBOARD_INTERVAL, "weekly")));
        btnMonthly.setOnClickListener(v -> store.dispatch(new AppEvent(AppEvent.Type.SET_LEADERBOARD_INTERVAL, "monthly")));
        
        loadLeaderboardData(activeInterval);
    }

    private void loadLeaderboardData(String interval) {
        RecyclerView rvLeaderboard = findViewById(R.id.rvLeaderboard);
        if (rvLeaderboard == null) return;
        
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(this));

        List<LeaderboardEntry> list = new ArrayList<>();
        String currentUserName = store.getState().getUserDisplayName();
        
        if ("daily".equals(interval)) {
            list.add(new LeaderboardEntry(1, "Av. Burak Yılmaz", 280, false));
            list.add(new LeaderboardEntry(2, "Av. Aslı Kaya", 255, false));
            list.add(new LeaderboardEntry(3, "Av. Canan Demir", 220, false));
            list.add(new LeaderboardEntry(4, currentUserName + " (Sen)", userScore, true));
            list.add(new LeaderboardEntry(5, "Av. Mehmet Taş", 60, false));
        } else if ("weekly".equals(interval)) {
            list.add(new LeaderboardEntry(1, "Av. Aslı Kaya", 1250, false));
            list.add(new LeaderboardEntry(2, "Av. Burak Yılmaz", 1100, false));
            list.add(new LeaderboardEntry(3, "Av. Mehmet Taş", 950, false));
            list.add(new LeaderboardEntry(4, currentUserName + " (Sen)", userScore + 300, true));
            list.add(new LeaderboardEntry(5, "Av. Canan Demir", 720, false));
        } else {
            list.add(new LeaderboardEntry(1, "Av. Mehmet Taş", 4200, false));
            list.add(new LeaderboardEntry(2, "Av. Aslı Kaya", 3800, false));
            list.add(new LeaderboardEntry(3, "Av. Burak Yılmaz", 3600, false));
            list.add(new LeaderboardEntry(4, currentUserName + " (Sen)", userScore + 1200, true));
            list.add(new LeaderboardEntry(5, "Av. Canan Demir", 2900, false));
        }

        list.sort((e1, e2) -> Integer.compare(e2.getScore(), e1.getScore()));

        List<LeaderboardEntry> sortedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            LeaderboardEntry old = list.get(i);
            sortedList.add(new LeaderboardEntry(i + 1, old.getUsername(), old.getScore(), old.isCurrentUser()));
        }

        LeaderboardAdapter adapter = new LeaderboardAdapter(sortedList);
        rvLeaderboard.setAdapter(adapter);
    }

    private void loadBannerAd() {
        AdView adView = findViewById(R.id.adView);
        if (adView != null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
    }

    private void setupFeedViews() {
        RecyclerView rvFeed = findViewById(R.id.rvFeed);
        if (rvFeed != null) {
            rvFeed.setLayoutManager(new LinearLayoutManager(this));
            FeedAdapter adapter = new FeedAdapter(FeedData.getPosts());
            rvFeed.setAdapter(adapter);
        }
    }

    private void setupWorkspaceViews() {
        RecyclerView rvWorkspaces = findViewById(R.id.rvWorkspaces);
        if (rvWorkspaces != null) {
            rvWorkspaces.setLayoutManager(new LinearLayoutManager(this));
            WorkspaceAdapter adapter = new WorkspaceAdapter(WorkspaceData.getWorkspaces());
            rvWorkspaces.setAdapter(adapter);
        }
    }

    private void updateScreenState(AppState.Screen screen, AppState state) {
        if (screen == AppState.Screen.SIMULATION) {
            Case c = state.getSelectedCase();
            if (c == null) return;

            if (chatAdapter != null) {
                chatAdapter.setItems(state.getChatMessages(), state.getSelectedRole());

                RecyclerView rvChat = findViewById(R.id.rvChat);
                if (rvChat != null && chatAdapter.getItemCount() > 0) {
                    rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }

            LinearLayout llSteps = findViewById(R.id.llStepsIndicatorContainer);
            if (llSteps != null) {
                int activeIndex = state.getCurrentStepIndex();
                int childCount = llSteps.getChildCount();
                int textItemCount = 0;
                for (int i = 0; i < childCount; i++) {
                    View v = llSteps.getChildAt(i);
                    if (v instanceof TextView) {
                        TextView tv = (TextView) v;
                        if (!tv.getText().toString().equals(" → ")) {
                            if (textItemCount == activeIndex) {
                                tv.setTextColor(ContextCompat.getColor(this, R.color.accent_primary));
                                tv.setTypeface(null, android.graphics.Typeface.NORMAL);
                            } else if (textItemCount < activeIndex) {
                                tv.setTextColor(ContextCompat.getColor(this, R.color.defense_green));
                                tv.setText(Html.fromHtml("✓ " + tv.getText().toString().replaceAll("^[✓0-9.]+\\s*", ""), Html.FROM_HTML_MODE_LEGACY));
                            } else {
                                tv.setTextColor(ContextCompat.getColor(this, R.color.text_muted));
                            }
                            textItemCount++;
                        }
                    }
                }
            }

            LinearLayout llChoices = findViewById(R.id.llChoicesContainer);
            TextView tvInputLabel = findViewById(R.id.tvInputLabel);

            if (llChoices != null && tvInputLabel != null) {
                llChoices.removeAllViews();
                int activeIndex = state.getCurrentStepIndex();
                List<Case.TrialStep> trialSteps = c.getTrialSteps();

                if (activeIndex < trialSteps.size()) {
                    Case.TrialStep currentTrialStep = trialSteps.get(activeIndex);
                    tvInputLabel.setText((activeIndex + 1) + ". Aşama: " + currentTrialStep.getStepName());
                    
                    boolean isDefense = "savunma".equalsIgnoreCase(state.getSelectedRole());
                    List<Case.Choice> choices = isDefense ? currentTrialStep.getDefenseChoices() : currentTrialStep.getProsecutionChoices();

                    for (Case.Choice choice : choices) {
                        MaterialButton choiceBtn = new MaterialButton(this);
                        choiceBtn.setText(choice.getText());
                        choiceBtn.setAllCaps(false);
                        choiceBtn.setTextSize(13);
                        choiceBtn.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                        choiceBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.card_bg));
                        choiceBtn.setStrokeColor(ContextCompat.getColorStateList(this, R.color.card_border));
                        choiceBtn.setStrokeWidth(1);
                        
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, 8, 0, 8);
                        choiceBtn.setLayoutParams(params);
                        choiceBtn.setPadding(16, 12, 16, 12);
                        
                        choiceBtn.setOnClickListener(v -> {
                            store.dispatch(new AppEvent(AppEvent.Type.SELECT_CHOICE, choice));
                        });

                        llChoices.addView(choiceBtn);
                    }
                } else {
                    tvInputLabel.setText("Karar Açıklandı");
                    TextView completedText = new TextView(this);
                    completedText.setText("Duruşma tamamlandı. Mahkeme nihai hükmünü vermiştir.");
                    completedText.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
                    completedText.setTextSize(13);
                    completedText.setPadding(0, 12, 0, 12);
                    llChoices.addView(completedText);
                    
                    userScore = state.getAccumulatedScore();
                }
            }
        } else if (screen == AppState.Screen.LEADERBOARD) {
            String interval = state.getLeaderboardInterval();
            MaterialButton btnDaily = findViewById(R.id.btnTabDaily);
            MaterialButton btnWeekly = findViewById(R.id.btnTabWeekly);
            MaterialButton btnMonthly = findViewById(R.id.btnTabMonthly);

            if (btnDaily != null && btnWeekly != null && btnMonthly != null) {
                btnDaily.setBackgroundTintList(ContextCompat.getColorStateList(this, "daily".equals(interval) ? R.color.text_primary : R.color.transparent));
                btnDaily.setTextColor(ContextCompat.getColor(this, "daily".equals(interval) ? R.color.bg_dark : R.color.text_primary));
                btnDaily.setStrokeWidth("daily".equals(interval) ? 0 : 1);

                btnWeekly.setBackgroundTintList(ContextCompat.getColorStateList(this, "weekly".equals(interval) ? R.color.text_primary : R.color.transparent));
                btnWeekly.setTextColor(ContextCompat.getColor(this, "weekly".equals(interval) ? R.color.bg_dark : R.color.text_primary));
                btnWeekly.setStrokeWidth("weekly".equals(interval) ? 0 : 1);

                btnMonthly.setBackgroundTintList(ContextCompat.getColorStateList(this, "monthly".equals(interval) ? R.color.text_primary : R.color.transparent));
                btnMonthly.setTextColor(ContextCompat.getColor(this, "monthly".equals(interval) ? R.color.bg_dark : R.color.text_primary));
                btnMonthly.setStrokeWidth("monthly".equals(interval) ? 0 : 1);
            }
            
            loadLeaderboardData(interval);
        }
    }
}
