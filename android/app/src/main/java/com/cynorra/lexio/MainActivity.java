package com.cynorra.lexio;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import android.content.res.Configuration;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import android.widget.EditText;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import android.widget.ProgressBar;

import com.cynorra.lexio.model.Case;
import com.cynorra.lexio.model.CasesData;
import com.cynorra.lexio.model.LeaderboardEntry;
import com.cynorra.lexio.model.StudyData;
import com.cynorra.lexio.model.StudyTopic;
import com.cynorra.lexio.model.FeedData;
import com.cynorra.lexio.model.FeedPost;
import com.cynorra.lexio.model.WorkspaceData;
import com.cynorra.lexio.model.Workspace;
import com.cynorra.lexio.model.DailyQuizData;
import com.cynorra.lexio.model.DailyQuizQuestion;
import com.cynorra.lexio.model.BadgeData;
import com.cynorra.lexio.mvi.AppEvent;
import com.cynorra.lexio.mvi.AppState;
import com.cynorra.lexio.mvi.AppStore;
import com.cynorra.lexio.ui.CasesAdapter;
import com.cynorra.lexio.ui.ChatAdapter;
import com.cynorra.lexio.ui.LeaderboardAdapter;
import com.cynorra.lexio.ui.StudyAdapter;
import com.cynorra.lexio.ui.FeedAdapter;
import com.cynorra.lexio.ui.WorkspaceAdapter;
import com.cynorra.lexio.ui.BadgeAdapter;

import java.util.ArrayList;
import java.util.List;

import com.cynorra.lexio.service.LexioNotificationManager;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.os.Build;

public class MainActivity extends AppCompatActivity implements AppStore.Observer {
    private static final int RC_SIGN_IN = 9001;

    private AppStore store;
    private GoogleSignInClient googleSignInClient;
    private LexioNotificationManager notificationManager;
    private AppState.Screen activeScreen = null;
    private ChatAdapter chatAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private int userScore = 0;
    private String userInstagramHandle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        store = new AppStore();
        notificationManager = new LexioNotificationManager(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Request POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("644546865365-r2j7da60f3tmcg46a6m6ts2hhcnudo7o.apps.googleusercontent.com")
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Subscribe to state updates
        store.subscribe(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        store.unsubscribe(this);
    }

    private String currentLanguage = null;

    @Override
    public void onStateChanged(AppState state) {
        userScore = state.getAccumulatedScore();

        int expectedMode = state.isDarkTheme() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        if (AppCompatDelegate.getDefaultNightMode() != expectedMode) {
            AppCompatDelegate.setDefaultNightMode(expectedMode);
        }

        if (currentLanguage == null || !currentLanguage.equals(state.getLanguage())) {
            currentLanguage = state.getLanguage();
            updateAppLocale(currentLanguage);
            if (activeScreen != null) {
                setupScreenLayout(activeScreen, state);
            }
        }

        if (activeScreen != state.getActiveScreen()) {
            activeScreen = state.getActiveScreen();
            setupScreenLayout(activeScreen, state);
        }
        updateScreenState(activeScreen, state);
    }

    private void updateAppLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
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
                setupCaseListViews(state.getCountry());
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
            case PROFILE:
                setContentView(R.layout.layout_profile);
                setupProfileViews(state);
                setupBottomNavigation();
                loadBannerAd();
                break;
            case DAILY_QUIZ:
                setContentView(R.layout.layout_daily_quiz);
                setupDailyQuizViews(state);
                break;
        }

        View root = findViewById(android.R.id.content);
        if (root != null) {
            Animation fadeAnim = AnimationUtils.loadAnimation(this, R.anim.screen_fade_in);
            root.startAnimation(fadeAnim);
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
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String name = user.getDisplayName();
                                
                                // Save to Firestore
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("name", name);
                                userData.put("email", user.getEmail());
                                userData.put("lastLogin", System.currentTimeMillis());
                                
                                db.collection("users").document(user.getUid())
                                    .set(userData, SetOptions.merge());

                                store.dispatch(new AppEvent(AppEvent.Type.LOGIN_SUCCESS, name));
                            }
                        } else {
                            Toast.makeText(this, "Firebase Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        } catch (ApiException e) {
            Toast.makeText(this, "Google Sign-In failed: " + e.getStatusCode(), Toast.LENGTH_LONG).show();
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
        View navProfile = findViewById(R.id.navProfile);

        if (navCases != null) {
            navCases.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_CASE_LIST));
            });
        }
        if (navStudy != null) {
            navStudy.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_STUDY_PORTAL));
            });
        }
        if (navLeaderboard != null) {
            navLeaderboard.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_LEADERBOARD));
            });
        }
        if (navFeed != null) {
            navFeed.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_FEED));
            });
        }
        if (navWorkspace != null) {
            navWorkspace.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_WORKSPACE));
            });
        }
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_PROFILE));
            });
        }
    }

    private void setupLandingViews() {
        Button btnStart = findViewById(R.id.btnStart);
        Button btnBrowse = findViewById(R.id.btnBrowse);

        btnStart.setOnClickListener(v -> store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_CASE_LIST)));
        btnBrowse.setOnClickListener(v -> store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_CASE_LIST)));
    }

    private void setupCaseListViews(String jurisdiction) {
        RecyclerView rvCases = findViewById(R.id.rvCases);
        if (rvCases != null) {
            rvCases.setLayoutManager(new LinearLayoutManager(this));

            CasesAdapter adapter = new CasesAdapter(CasesData.getCasesForJurisdiction(jurisdiction), selectedCase -> {
                store.dispatch(new AppEvent(AppEvent.Type.SELECT_CASE, selectedCase));
            });
            rvCases.setAdapter(adapter);
        }

        View cvDailyQuizBanner = findViewById(R.id.cvDailyQuizBanner);
        if (cvDailyQuizBanner != null) {
            cvDailyQuizBanner.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_DAILY_QUIZ));
            });
        }
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

    private void saveUserScoreToFirestore() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && db != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("score", userScore);
            data.put("name", user.getDisplayName() != null ? user.getDisplayName() : store.getState().getUserDisplayName());
            data.put("email", user.getEmail());
            data.put("lastActive", System.currentTimeMillis());
            db.collection("users").document(user.getUid()).set(data, SetOptions.merge());
        }
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

        saveUserScoreToFirestore();

        db.collection("users")
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<LeaderboardEntry> list = new ArrayList<>();
                int rank = 1;
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String currentUid = currentUser != null ? currentUser.getUid() : "";

                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    String name = doc.getString("name");
                    if (name == null || name.isEmpty()) name = "Hukukçu";
                    Long scoreLong = doc.getLong("score");
                    int score = scoreLong != null ? scoreLong.intValue() : 0;
                    boolean isCurrent = doc.getId().equals(currentUid);

                    list.add(new LeaderboardEntry(rank++, isCurrent ? name + " (Sen)" : name, score, isCurrent));
                }

                if (list.isEmpty()) {
                    String name = store.getState().getUserDisplayName();
                    list.add(new LeaderboardEntry(1, name + " (Sen)", userScore, true));
                }

                LeaderboardAdapter adapter = new LeaderboardAdapter(list);
                rvLeaderboard.setAdapter(adapter);
            })
            .addOnFailureListener(e -> {
                List<LeaderboardEntry> list = new ArrayList<>();
                String currentUserName = store.getState().getUserDisplayName();
                list.add(new LeaderboardEntry(1, currentUserName + " (Sen)", userScore, true));
                LeaderboardAdapter adapter = new LeaderboardAdapter(list);
                rvLeaderboard.setAdapter(adapter);
            });
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
            
            db.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<FeedPost> livePosts = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        FeedPost p = doc.toObject(FeedPost.class);
                        if (p != null) {
                            p.setDocId(doc.getId());
                            livePosts.add(p);
                        }
                    }
                    FeedData.setPosts(livePosts);
                    rvFeed.setAdapter(new FeedAdapter(FeedData.getPosts()));
                })
                .addOnFailureListener(e -> {
                    rvFeed.setAdapter(new FeedAdapter(FeedData.getPosts()));
                });
        }

        EditText etContent = findViewById(R.id.etNewPostContent);
        View btnPublish = findViewById(R.id.btnPublishPost);
        if (btnPublish != null && etContent != null) {
            btnPublish.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                String text = etContent.getText().toString().trim();
                if (text.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Lütfen paylaşmak istediğiniz içeriği yazın.", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUser user = mAuth.getCurrentUser();
                String author = (user != null && user.getDisplayName() != null) ? user.getDisplayName() : store.getState().getUserDisplayName();
                
                Map<String, Object> postMap = new HashMap<>();
                postMap.put("authorName", author);
                postMap.put("authorRole", "Hukukçu");
                postMap.put("content", text);
                postMap.put("likesCount", 0);
                postMap.put("commentsCount", 0);
                postMap.put("createdAt", System.currentTimeMillis());

                db.collection("posts").add(postMap).addOnSuccessListener(docRef -> {
                    etContent.setText("");
                    Toast.makeText(MainActivity.this, "Gönderiniz başarıyla paylaşıldı! 🚀", Toast.LENGTH_SHORT).show();
                    setupFeedViews();
                }).addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Gönderi paylaşılamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            });
        }
    }

    private void setupWorkspaceViews() {
        RecyclerView rvWorkspaces = findViewById(R.id.rvWorkspaces);
        if (rvWorkspaces != null) {
            rvWorkspaces.setLayoutManager(new LinearLayoutManager(this));

            db.collection("workspaces")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Workspace> list = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Workspace w = doc.toObject(Workspace.class);
                        if (w != null) {
                            list.add(w);
                        }
                    }
                    if (list.isEmpty()) {
                        List<Workspace> seeds = new ArrayList<>();
                        seeds.add(new Workspace(1, "Ceza Hukuku Tartışmaları", "Yargıtay kararları ve ceza hukuku üzerine akademik/pratik tartışmalar.", 1245, "⚖️"));
                        seeds.add(new Workspace(2, "Stajyer Avukatlar Yardımlaşma", "Adliye tecrübeleri, dilekçe örnekleri ve mesleki dayanışma.", 850, "📚"));
                        seeds.add(new Workspace(3, "İş ve Sosyal Güvenlik Hukuku", "İşçi-işveren uyuşmazlıkları ve SGK davaları hakkında güncel gelişmeler.", 620, "💼"));
                        seeds.add(new Workspace(4, "Hukuk Fakültesi Öğrencileri", "Vize/Final not paylaşımı ve genel sohbet.", 3400, "🎓"));
                        seeds.add(new Workspace(5, "Bilişim Hukuku & KVKK", "Kişisel verilerin korunması, siber suçlar ve teknoloji hukuku.", 410, "💻"));

                        for (Workspace w : seeds) {
                            db.collection("workspaces").document(String.valueOf(w.getId())).set(w);
                        }
                        list = seeds;
                    }
                    WorkspaceData.setWorkspaces(list);
                    rvWorkspaces.setAdapter(new WorkspaceAdapter(WorkspaceData.getWorkspaces()));
                })
                .addOnFailureListener(e -> {
                    rvWorkspaces.setAdapter(new WorkspaceAdapter(WorkspaceData.getWorkspaces()));
                });
        }
    }

    private void setupProfileViews(AppState state) {
        TextView tvProfileName = findViewById(R.id.tvProfileName);
        TextView tvProfileEmail = findViewById(R.id.tvProfileEmail);
        TextView tvTotalScore = findViewById(R.id.tvTotalScore);
        TextView tvCasesSolved = findViewById(R.id.tvCasesSolved);
        Switch switchTheme = findViewById(R.id.switchTheme);
        View btnSignOut = findViewById(R.id.btnSignOut);
        View btnLanguageToggle = findViewById(R.id.btnLanguageToggle);
        TextView tvLanguageValue = findViewById(R.id.tvLanguageValue);
        TextView tvCareerRankBadge = findViewById(R.id.tvCareerRankBadge);
        View btnCountryToggle = findViewById(R.id.btnCountryToggle);
        TextView tvCountryValue = findViewById(R.id.tvCountryValue);
        RecyclerView rvBadges = findViewById(R.id.rvBadges);

        if (tvProfileName != null) tvProfileName.setText(state.getUserDisplayName());
        
        FirebaseUser user = mAuth.getCurrentUser();
        if (tvProfileEmail != null && user != null) {
            tvProfileEmail.setText(user.getEmail());
        }

        if (tvCareerRankBadge != null) {
            tvCareerRankBadge.setText(state.getCareerRankTitle());
        }

        int casesSolved = userScore > 0 ? (userScore / 60) + 1 : 0;
        if (tvTotalScore != null) tvTotalScore.setText(String.valueOf(userScore));
        if (tvCasesSolved != null) tvCasesSolved.setText(String.valueOf(casesSolved));

        if (rvBadges != null) {
            rvBadges.setLayoutManager(new GridLayoutManager(this, 2));
            List<BadgeData.Badge> badges = BadgeData.getAllBadges(userScore, state.getDailyStreak(), casesSolved);
            BadgeAdapter badgeAdapter = new BadgeAdapter(badges);
            rvBadges.setAdapter(badgeAdapter);
        }

        TextView tvInstagramHandle = findViewById(R.id.tvInstagramHandle);
        View btnShareInstagramStory = findViewById(R.id.btnShareInstagramStory);

        if (tvInstagramHandle != null) {
            if (userInstagramHandle != null && !userInstagramHandle.isEmpty()) {
                tvInstagramHandle.setText("📸 @" + userInstagramHandle.replace("@", ""));
            } else {
                tvInstagramHandle.setText("📸 Instagram Ekleyin");
            }

            tvInstagramHandle.setOnClickListener(v -> {
                android.widget.EditText input = new android.widget.EditText(MainActivity.this);
                input.setHint("@kullaniciadi");
                if (userInstagramHandle != null) input.setText(userInstagramHandle);

                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Instagram Kullanıcı Adı")
                    .setMessage("Profilinizde ve paylaşımlarınızda görünecek Instagram kullanıcı adınızı girin:")
                    .setView(input)
                    .setPositiveButton("Kaydet", (dialog, which) -> {
                        userInstagramHandle = input.getText().toString().trim();
                        if (!userInstagramHandle.isEmpty()) {
                            tvInstagramHandle.setText("📸 @" + userInstagramHandle.replace("@", ""));
                            Toast.makeText(MainActivity.this, "Instagram hesabınız eklendi!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("İptal", null)
                    .show();
            });
        }

        if (btnShareInstagramStory != null) {
            btnShareInstagramStory.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                String shareText = "Lexio Hukuk Platformu Başarım Kartım ⚖️\n" +
                        "Unvan: " + state.getCareerRankTitle() + "\n" +
                        "Toplam Puan: " + userScore + "\n" +
                        "Seri: 🔥 " + state.getDailyStreak() + " Gün\n" +
                        (userInstagramHandle != null ? "Instagram: @" + userInstagramHandle + "\n" : "") +
                        "www.lexio.app";

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(Intent.createChooser(shareIntent, "Instagram Hikaye veya Sosyal Medyada Paylaş"));
            });
        }

        if (switchTheme != null) {
            switchTheme.setChecked(state.isDarkTheme());
            switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
                store.dispatch(new AppEvent(AppEvent.Type.TOGGLE_THEME));
            });
        }

        if (tvLanguageValue != null) {
            boolean isTurkish = "tr".equalsIgnoreCase(state.getLanguage());
            tvLanguageValue.setText(isTurkish ? getString(R.string.language_turkish) : getString(R.string.language_english));
        }

        if (btnLanguageToggle != null) {
            btnLanguageToggle.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                store.dispatch(new AppEvent(AppEvent.Type.TOGGLE_LANGUAGE));
            });
        }

        if (tvCountryValue != null) {
            String country = state.getCountry();
            if ("US".equals(country)) {
                tvCountryValue.setText("🇺🇸 USA (US Code)");
            } else if ("UK".equals(country)) {
                tvCountryValue.setText("🇬🇧 UK (Common Law)");
            } else if ("DE".equals(country)) {
                tvCountryValue.setText("🇩🇪 Germany (BGB/StGB)");
            } else {
                tvCountryValue.setText("🇹🇷 Türkiye (TCK/Yargıtay)");
            }
        }

        if (btnCountryToggle != null) {
            btnCountryToggle.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                String[] countries = new String[]{"🇹🇷 Türkiye (TCK/Yargıtay)", "🇺🇸 USA (US Code)", "🇬🇧 UK (Common Law)", "🇩🇪 Germany (BGB/StGB)"};
                String[] codes = new String[]{"TR", "US", "UK", "DE"};

                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Hukuk Sistemi Seçin / Select Jurisdiction")
                    .setItems(countries, (dialog, which) -> {
                        store.dispatch(new AppEvent(AppEvent.Type.SELECT_COUNTRY, codes[which], true));
                    })
                    .show();
            });
        }

        if (btnSignOut != null) {
            btnSignOut.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                mAuth.signOut();
                googleSignInClient.signOut();
                store.dispatch(new AppEvent(AppEvent.Type.LOGOUT));
            });
        }
    }

    private int quizCurrentQuestionIndex = 0;

    private void setupDailyQuizViews(AppState state) {
        Button btnBack = findViewById(R.id.btnBackFromQuiz);
        TextView tvStreak = findViewById(R.id.tvQuizStreakBadge);
        ProgressBar pbProgress = findViewById(R.id.pbQuizProgress);
        TextView tvQNum = findViewById(R.id.tvQuizQuestionNum);
        TextView tvQText = findViewById(R.id.tvQuizQuestionText);
        LinearLayout llOptions = findViewById(R.id.llQuizOptionsContainer);
        View cvExplanation = findViewById(R.id.cvQuizExplanation);
        TextView tvExplanationText = findViewById(R.id.tvExplanationText);
        Button btnNext = findViewById(R.id.btnNextQuestion);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> store.dispatch(new AppEvent(AppEvent.Type.NAVIGATE_TO_CASE_LIST)));
        }

        if (tvStreak != null) {
            tvStreak.setText("🔥 " + state.getDailyStreak() + " Gün Seri");
        }

        List<DailyQuizQuestion> questions = DailyQuizData.getQuestionsForJurisdiction(state.getCountry());
        if (questions.isEmpty()) return;

        if (quizCurrentQuestionIndex >= questions.size()) {
            quizCurrentQuestionIndex = 0;
        }

        DailyQuizQuestion q = questions.get(quizCurrentQuestionIndex);

        if (pbProgress != null) {
            pbProgress.setMax(questions.size());
            pbProgress.setProgress(quizCurrentQuestionIndex + 1);
        }

        if (tvQNum != null) {
            tvQNum.setText("Soru " + (quizCurrentQuestionIndex + 1) + " / " + questions.size());
        }

        if (tvQText != null) {
            tvQText.setText(q.getQuestion());
        }

        if (cvExplanation != null) cvExplanation.setVisibility(View.GONE);

        if (llOptions != null) {
            llOptions.removeAllViews();
            List<String> options = q.getOptions();

            for (int i = 0; i < options.size(); i++) {
                int optionIndex = i;
                String optText = options.get(i);

                MaterialButton btnOption = new MaterialButton(this);
                btnOption.setText(optText);
                btnOption.setAllCaps(false);
                btnOption.setTextSize(13);
                btnOption.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                btnOption.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.card_bg));
                btnOption.setStrokeColor(ContextCompat.getColorStateList(this, R.color.card_border));
                btnOption.setStrokeWidth(1);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 
                    ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 8, 0, 8);
                btnOption.setLayoutParams(params);
                btnOption.setPadding(16, 12, 16, 12);

                btnOption.setOnClickListener(v -> {
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    boolean isCorrect = (optionIndex == q.getCorrectIndex());

                    if (isCorrect) {
                        btnOption.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.defense_green_bg));
                        btnOption.setStrokeColor(ContextCompat.getColorStateList(MainActivity.this, R.color.defense_green));
                    } else {
                        btnOption.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.prosecution_red_bg));
                        btnOption.setStrokeColor(ContextCompat.getColorStateList(MainActivity.this, R.color.prosecution_red));
                    }

                    if (cvExplanation != null && tvExplanationText != null) {
                        cvExplanation.setVisibility(View.VISIBLE);
                        tvExplanationText.setText((isCorrect ? "✅ Doğru! " : "❌ Yanlış! ") + q.getExplanation());
                    }
                });

                llOptions.addView(btnOption);
            }
        }

        if (btnNext != null) {
            btnNext.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                quizCurrentQuestionIndex++;
                if (quizCurrentQuestionIndex < questions.size()) {
                    setupDailyQuizViews(state);
                } else {
                    quizCurrentQuestionIndex = 0;
                    Toast.makeText(MainActivity.this, "Tebrikler! Günlük Test Tamamlandı (+100 Puan)", Toast.LENGTH_LONG).show();
                    store.dispatch(new AppEvent(AppEvent.Type.COMPLETE_DAILY_QUIZ));
                }
            });
        }
    }

    private void updateScreenState(AppState.Screen screen, AppState state) {
        if (screen == AppState.Screen.SIMULATION) {
            Case c = state.getSelectedCase();
            if (c == null) return;

            if (chatAdapter != null) {
                chatAdapter.setItems(state);

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
