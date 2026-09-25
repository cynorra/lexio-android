package com.cynorra.lexio;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cynorra.lexio.data.ContentRepository;
import com.cynorra.lexio.data.DateKeys;
import com.cynorra.lexio.data.LeaderboardRepository;
import com.cynorra.lexio.data.ProgressRepository;
import com.cynorra.lexio.data.UserProgress;
import com.cynorra.lexio.model.BadgeData;
import com.cynorra.lexio.model.Case;
import com.cynorra.lexio.model.CasesData;
import com.cynorra.lexio.model.DailyQuizQuestion;
import com.cynorra.lexio.model.FeedData;
import com.cynorra.lexio.model.FeedPost;
import com.cynorra.lexio.model.Jurisdictions;
import com.cynorra.lexio.model.LeaderboardEntry;
import com.cynorra.lexio.model.StudyTopic;
import com.cynorra.lexio.model.Workspace;
import com.cynorra.lexio.model.WorkspaceData;
import com.cynorra.lexio.mvi.AppEvent;
import com.cynorra.lexio.mvi.AppState;
import com.cynorra.lexio.mvi.AppStore;
import com.cynorra.lexio.mvi.StoreHolder;
import com.cynorra.lexio.service.LexioNotificationManager;
import com.cynorra.lexio.ui.BadgeAdapter;
import com.cynorra.lexio.ui.CasesAdapter;
import com.cynorra.lexio.ui.ChatAdapter;
import com.cynorra.lexio.ui.FeedAdapter;
import com.cynorra.lexio.ui.LeaderboardAdapter;
import com.cynorra.lexio.ui.StudyAdapter;
import com.cynorra.lexio.ui.WorkspaceAdapter;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AppStore.Observer {
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_NOTIFICATIONS = 101;

    private static final String PREFS_NAME = "lexio_prefs";
    private static final String PREF_LANGUAGE = "language";
    private static final String PREF_DARK_THEME = "dark_theme";
    private static final String PREF_COUNTRY = "country";

    private StoreHolder holder;
    private AppStore store;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressRepository progressRepo;
    private LeaderboardRepository leaderboardRepo;
    private ContentRepository contentRepo;

    private AppState.Screen activeScreen = null;
    private String currentLanguage = null;
    private ChatAdapter chatAdapter;

    // Award tracking: career-score increases (not wholesale progress reloads) feed the leaderboards.
    private int lastProgressEpoch = -1;
    private int lastCareerScore = 0;

    // Guards against stale async results landing on a screen the user already left.
    private String leaderboardLoadedInterval = null;
    private int leaderboardRequest = 0;
    private int feedRequest = 0;
    private int workspaceRequest = 0;
    private int studyRequest = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        holder = new ViewModelProvider(this).get(StoreHolder.class);
        store = holder.store;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        progressRepo = new ProgressRepository(this);
        leaderboardRepo = new LeaderboardRepository();
        contentRepo = new ContentRepository();
        // Creates the notification channels; the reminder itself is scheduled by LexioApplication.
        new LexioNotificationManager(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("644546865365-r2j7da60f3tmcg46a6m6ts2hhcnudo7o.apps.googleusercontent.com")
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        registerBackHandler();

        // The store outlives this Activity (see StoreHolder), so session/preferences are only
        // restored the first time; a recreate (theme, rotation) just re-attaches to live state.
        if (!holder.initialized) {
            holder.initialized = true;
            requestNotificationPermission();
            restorePreferences();
            FirebaseUser existingUser = mAuth.getCurrentUser();
            if (existingUser != null) {
                onSignedIn(existingUser);
            }
        }

        // A recreate re-delivers the original intent; only a real launch may trigger the quiz shortcut.
        if (savedInstanceState == null) {
            captureLaunchIntent(getIntent());
        }

        store.subscribe(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        captureLaunchIntent(intent);
        openPendingQuiz(store.getState());
    }

    /** The reminder notification asks to land on the daily quiz. */
    private void captureLaunchIntent(@Nullable Intent intent) {
        if (intent != null && intent.getBooleanExtra(LexioNotificationManager.EXTRA_OPEN_DAILY_QUIZ, false)) {
            holder.openQuizPending = true;
            intent.removeExtra(LexioNotificationManager.EXTRA_OPEN_DAILY_QUIZ);
        }
    }

    /** Opens the quiz once the user is signed in (immediately if they already are). */
    private void openPendingQuiz(AppState state) {
        if (!holder.openQuizPending || !state.isAuthenticated() || state.getActiveScreen() == AppState.Screen.LOGIN) {
            return;
        }
        holder.openQuizPending = false;
        store.dispatch(AppEvent.of(AppEvent.Type.NAVIGATE_TO_DAILY_QUIZ));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        store.unsubscribe(this);
    }

    // ---------------------------------------------------------------- lifecycle helpers

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, RC_NOTIFICATIONS);
        }
    }

    /** Language/theme are device preferences (not account data), restored before the first render. */
    private void restorePreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // First launch: follow the device language (Turkish or English) and start on the US.
        String defaultLanguage = "tr".equals(Locale.getDefault().getLanguage()) ? "tr" : "en";
        String country = prefs.getString(PREF_COUNTRY, Jurisdictions.DEFAULT);
        store.dispatch(AppEvent.setLanguage(prefs.getString(PREF_LANGUAGE, defaultLanguage)));
        store.dispatch(AppEvent.setDarkTheme(prefs.getBoolean(PREF_DARK_THEME, true)));
        store.dispatch(AppEvent.selectCountry(Jurisdictions.isSupported(country) ? country : Jurisdictions.DEFAULT));
    }

    private void persistPreferences(AppState state) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                .putString(PREF_LANGUAGE, state.getLanguage())
                .putBoolean(PREF_DARK_THEME, state.isDarkTheme())
                .putString(PREF_COUNTRY, state.getCountry())
                .apply();
    }

    private void registerBackHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                switch (store.getState().getActiveScreen()) {
                    case CASE_DETAIL:
                    case SIMULATION:
                    case DAILY_QUIZ:
                    case STUDY_PORTAL:
                    case FEED:
                    case WORKSPACE:
                    case LEADERBOARD:
                    case PROFILE:
                        store.dispatch(AppEvent.of(AppEvent.Type.NAVIGATE_TO_CASE_LIST));
                        break;
                    case STUDY_DETAIL:
                        store.dispatch(AppEvent.of(AppEvent.Type.NAVIGATE_TO_STUDY_PORTAL));
                        break;
                    default:
                        finish();
                }
            }
        });
    }

    // ---------------------------------------------------------------- account & progress

    private String resolveDisplayName(FirebaseUser user) {
        String name = user.getDisplayName();
        return (name != null && !name.trim().isEmpty()) ? name : getString(R.string.leaderboard_default_name);
    }

    private boolean isSignedIn(String uid) {
        FirebaseUser u = mAuth.getCurrentUser();
        return u != null && u.getUid().equals(uid);
    }

    private void onSignedIn(FirebaseUser user) {
        holder.remoteSynced = false;
        holder.lastSynced = null;
        store.dispatch(AppEvent.loginSuccess(resolveDisplayName(user)));
        loadProgress(user.getUid());
    }

    /**
     * Shows the locally cached progress immediately, then merges in the server copy. Local
     * writes are held back ({@link StoreHolder#remoteSynced}) until the server was read, so a
     * failed read can never overwrite the real score with zeros.
     */
    private void loadProgress(String uid) {
        final String today = DateKeys.today();
        final UserProgress local = progressRepo.loadLocal(uid);
        if (local != null) {
            store.dispatch(AppEvent.progressLoaded(local, today));
        }

        progressRepo.loadRemote(uid, new ProgressRepository.Callback() {
            @Override
            public void onLoaded(UserProgress remote) {
                if (!isSignedIn(uid)) return;
                AppState now = store.getState();
                // Prefer what the user has on screen right now: they may have played meanwhile.
                UserProgress base = now.isProgressLoaded() ? now.toProgress() : local;
                UserProgress merged = UserProgress.merge(base, remote);
                if (merged == null) merged = UserProgress.empty();

                holder.remoteSynced = true;
                holder.lastSynced = null; // force one write-through of the merged result
                store.dispatch(AppEvent.progressLoaded(merged, today));
            }

            @Override
            public void onError() {
                if (!isSignedIn(uid)) return;
                if (!store.getState().isProgressLoaded()) {
                    store.dispatch(AppEvent.progressLoaded(UserProgress.empty(), today));
                }
                // remoteSynced stays false: progress keeps being cached locally and is merged
                // with the server copy on the next successful sign-in.
            }
        });
    }

    /** Persists changed progress and credits newly earned points to the period leaderboards. */
    private void syncProgress(AppState state) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || !state.isAuthenticated() || !state.isProgressLoaded()) {
            lastProgressEpoch = -1;
            return;
        }

        if (state.getProgressEpoch() == lastProgressEpoch && state.getCareerScore() > lastCareerScore) {
            String name = state.getUserDisplayName().isEmpty() ? resolveDisplayName(user) : state.getUserDisplayName();
            leaderboardRepo.addPoints(user.getUid(), name, state.getCareerScore() - lastCareerScore);
        }
        lastProgressEpoch = state.getProgressEpoch();
        lastCareerScore = state.getCareerScore();

        UserProgress progress = state.toProgress();
        if (progress.equals(holder.lastSynced)) return;
        holder.lastSynced = progress;
        progressRepo.saveLocal(user.getUid(), progress);
        if (holder.remoteSynced) {
            progressRepo.pushRemote(user.getUid(), progress, state.getUserDisplayName(), user.getEmail());
        }
    }

    // ---------------------------------------------------------------- state -> UI

    @Override
    public void onStateChanged(AppState state) {
        persistPreferences(state);

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
        syncProgress(state);
        openPendingQuiz(state);
    }

    @SuppressWarnings("deprecation")
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
                setupLeaderboardViews();
                setupBottomNavigation();
                loadBannerAd();
                break;
            case PROFILE:
                setContentView(R.layout.layout_profile);
                setupProfileViews();
                setupBottomNavigation();
                loadBannerAd();
                break;
            case DAILY_QUIZ:
                setContentView(R.layout.layout_daily_quiz);
                setupDailyQuizViews();
                break;
        }

        View root = findViewById(android.R.id.content);
        if (root != null) {
            Animation fadeAnim = AnimationUtils.loadAnimation(this, R.anim.screen_fade_in);
            root.startAnimation(fadeAnim);
        }
    }

    /** Re-binds everything that depends on state and can change while a screen is showing. */
    private void updateScreenState(AppState.Screen screen, AppState state) {
        switch (screen) {
            case SIMULATION:
                updateSimulation(state);
                break;
            case LEADERBOARD:
                updateLeaderboardTabs(state.getLeaderboardInterval());
                if (!state.getLeaderboardInterval().equals(leaderboardLoadedInterval)) {
                    loadLeaderboardData(state.getLeaderboardInterval());
                }
                break;
            case PROFILE:
                bindProfile(state);
                break;
            case DAILY_QUIZ:
                ensureQuizLoaded(state);
                renderDailyQuiz(state);
                break;
            default:
                break;
        }
    }

    // ---------------------------------------------------------------- sign in

    @Override
    @SuppressWarnings("deprecation")
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
            if (account == null) return;
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
                FirebaseUser user = mAuth.getCurrentUser();
                if (task.isSuccessful() && user != null) {
                    onSignedIn(user);
                } else {
                    Toast.makeText(this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (ApiException e) {
            Toast.makeText(this, getString(R.string.google_sign_in_failed) + " " + e.getStatusCode(), Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("deprecation")
    private void setupLoginViews() {
        View btnSignIn = findViewById(R.id.btnGoogleSignIn);
        if (btnSignIn != null) {
            btnSignIn.setOnClickListener(v -> startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN));
        }
    }

    // ---------------------------------------------------------------- shared chrome

    private void setupBottomNavigation() {
        bindNav(R.id.navCases, AppEvent.Type.NAVIGATE_TO_CASE_LIST);
        bindNav(R.id.navStudy, AppEvent.Type.NAVIGATE_TO_STUDY_PORTAL);
        bindNav(R.id.navLeaderboard, AppEvent.Type.NAVIGATE_TO_LEADERBOARD);
        bindNav(R.id.navFeed, AppEvent.Type.NAVIGATE_TO_FEED);
        bindNav(R.id.navWorkspace, AppEvent.Type.NAVIGATE_TO_WORKSPACE);
        bindNav(R.id.navProfile, AppEvent.Type.NAVIGATE_TO_PROFILE);
    }

    private void bindNav(int viewId, AppEvent.Type type) {
        View nav = findViewById(viewId);
        if (nav != null) {
            nav.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                store.dispatch(AppEvent.of(type));
            });
        }
    }

    private void loadBannerAd() {
        AdView adView = findViewById(R.id.adView);
        if (adView != null) {
            adView.loadAd(new AdRequest.Builder().build());
        }
    }

    /** Swaps a list in, or shows {@code emptyMessage} in its place when there is nothing to show. */
    private void showList(int recyclerId, RecyclerView.Adapter<?> adapter, int itemCount, int emptyMessage) {
        RecyclerView rv = findViewById(recyclerId);
        TextView empty = findViewById(R.id.tvEmptyState);
        if (rv == null) return;
        rv.setAdapter(adapter);
        boolean hasItems = itemCount > 0;
        rv.setVisibility(hasItems ? View.VISIBLE : View.GONE);
        if (empty != null) {
            empty.setText(emptyMessage);
            empty.setVisibility(hasItems ? View.GONE : View.VISIBLE);
        }
    }

    private boolean isUiGone() {
        return isFinishing() || isDestroyed();
    }

    // ---------------------------------------------------------------- landing & cases

    private void setupLandingViews() {
        TextView tvCasesCount = findViewById(R.id.tvStatCasesCount);
        if (tvCasesCount != null) {
            tvCasesCount.setText(String.valueOf(CasesData.getTotalCaseCount()));
        }
        TextView tvJurisdictionCount = findViewById(R.id.tvStatJurisdictionCount);
        if (tvJurisdictionCount != null) {
            tvJurisdictionCount.setText(String.valueOf(Jurisdictions.CODES.length));
        }
        Button btnStart = findViewById(R.id.btnStart);
        Button btnBrowse = findViewById(R.id.btnBrowse);
        btnStart.setOnClickListener(v -> store.dispatch(AppEvent.of(AppEvent.Type.NAVIGATE_TO_CASE_LIST)));
        btnBrowse.setOnClickListener(v -> store.dispatch(AppEvent.of(AppEvent.Type.NAVIGATE_TO_CASE_LIST)));
    }

    private void setupCaseListViews(String jurisdiction) {
        RecyclerView rvCases = findViewById(R.id.rvCases);
        if (rvCases != null) {
            rvCases.setLayoutManager(new LinearLayoutManager(this));
            List<Case> cases = CasesData.getCasesForJurisdiction(jurisdiction);
            showList(R.id.rvCases, new CasesAdapter(cases,
                    selectedCase -> store.dispatch(AppEvent.selectCase(selectedCase))),
                    cases.size(), R.string.case_list_empty);
        }

        View cvDailyQuizBanner = findViewById(R.id.cvDailyQuizBanner);
        if (cvDailyQuizBanner != null) {
            cvDailyQuizBanner.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                store.dispatch(AppEvent.of(AppEvent.Type.NAVIGATE_TO_DAILY_QUIZ));
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
        String refLabel = "TR".equals(c.getJurisdiction()) ? "İlgili Mevzuat" : "Legal Basis";
        tvRef.setText(refLabel + " — " + c.getRef());
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

        btnBack.setOnClickListener(v -> store.dispatch(AppEvent.of(AppEvent.Type.NAVIGATE_TO_CASE_LIST)));
        btnDefense.setOnClickListener(v -> startSimulationAs("savunma"));
        btnProsecution.setOnClickListener(v -> startSimulationAs("savcilik"));
    }

    private void startSimulationAs(String role) {
        store.dispatch(AppEvent.chooseRole(role));
        store.dispatch(AppEvent.of(AppEvent.Type.START_SIMULATION));
    }

    // ---------------------------------------------------------------- simulation

    private void setupSimulationViews(AppState state) {
        Case c = state.getSelectedCase();
        if (c == null) return;

        TextView tvTitle = findViewById(R.id.tvSimTitle);
        TextView tvRoleBadge = findViewById(R.id.tvSimRoleBadge);
        Button btnExit = findViewById(R.id.btnExitSimulation);
        RecyclerView rvChat = findViewById(R.id.rvChat);
        LinearLayout llSteps = findViewById(R.id.llStepsIndicatorContainer);

        tvTitle.setText(c.getTitle());

        boolean simScreenIsTrCase = "TR".equals(c.getJurisdiction());
        boolean isDefense = "savunma".equalsIgnoreCase(state.getSelectedRole());
        tvRoleBadge.setText(isDefense
                ? (simScreenIsTrCase ? "⚖️ Savunma Avukatı" : "⚖️ Defense Counsel")
                : (simScreenIsTrCase ? "🔍 Savcı" : "🔍 Prosecutor"));
        tvRoleBadge.setTextColor(ContextCompat.getColor(this, isDefense ? R.color.defense_green : R.color.prosecution_red));
        tvRoleBadge.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_pill_accent));

        btnExit.setOnClickListener(v -> store.dispatch(AppEvent.of(AppEvent.Type.NAVIGATE_TO_CASE_LIST)));

        rvChat.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter();
        rvChat.setAdapter(chatAdapter);

        llSteps.removeAllViews();
        List<Case.TrialStep> steps = c.getTrialSteps();
        for (int i = 0; i < steps.size() + 1; i++) {
            TextView stepView = new TextView(this);
            String name = (i < steps.size()) ? steps.get(i).getStepName() : (simScreenIsTrCase ? "Karar" : "Verdict");
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

    private void updateSimulation(AppState state) {
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
            int stepNumber = 0;
            for (int i = 0; i < llSteps.getChildCount(); i++) {
                View v = llSteps.getChildAt(i);
                if (!(v instanceof TextView)) continue;
                TextView tv = (TextView) v;
                if (tv.getText().toString().equals(" → ")) continue;

                if (stepNumber == activeIndex) {
                    tv.setTextColor(ContextCompat.getColor(this, R.color.accent_primary));
                } else if (stepNumber < activeIndex) {
                    tv.setTextColor(ContextCompat.getColor(this, R.color.defense_green));
                    String plain = tv.getText().toString().replaceAll("^[✓0-9.]+\\s*", "");
                    tv.setText("✓ " + plain);
                } else {
                    tv.setTextColor(ContextCompat.getColor(this, R.color.text_muted));
                }
                stepNumber++;
            }
        }

        LinearLayout llChoices = findViewById(R.id.llChoicesContainer);
        TextView tvInputLabel = findViewById(R.id.tvInputLabel);
        if (llChoices == null || tvInputLabel == null) return;

        llChoices.removeAllViews();
        int activeIndex = state.getCurrentStepIndex();
        List<Case.TrialStep> trialSteps = c.getTrialSteps();
        boolean simIsTrCase = "TR".equals(c.getJurisdiction());

        if (activeIndex < trialSteps.size()) {
            Case.TrialStep currentTrialStep = trialSteps.get(activeIndex);
            String stageLabel = simIsTrCase ? ". Aşama: " : ". Stage: ";
            tvInputLabel.setText((activeIndex + 1) + stageLabel + currentTrialStep.getStepName());

            boolean isDefense = "savunma".equalsIgnoreCase(state.getSelectedRole());
            List<Case.Choice> choices = isDefense ? currentTrialStep.getDefenseChoices() : currentTrialStep.getProsecutionChoices();
            for (Case.Choice choice : choices) {
                MaterialButton choiceBtn = newOptionButton(choice.getText());
                choiceBtn.setOnClickListener(v -> store.dispatch(AppEvent.selectChoice(choice)));
                llChoices.addView(choiceBtn);
            }
        } else {
            tvInputLabel.setText(simIsTrCase ? "Karar Açıklandı" : "Verdict Announced");
            TextView completedText = new TextView(this);
            completedText.setText(simIsTrCase
                    ? "Duruşma tamamlandı. Mahkeme nihai hükmünü vermiştir."
                    : "Trial completed. The court has rendered its final verdict.");
            completedText.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            completedText.setTextSize(13);
            completedText.setPadding(0, 12, 0, 12);
            llChoices.addView(completedText);
        }
    }

    /** A full-width outlined button, used for simulation choices and quiz options. */
    private MaterialButton newOptionButton(String text) {
        MaterialButton btn = new MaterialButton(this);
        btn.setText(text);
        btn.setAllCaps(false);
        btn.setTextSize(13);
        btn.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.card_bg));
        btn.setStrokeColor(ContextCompat.getColorStateList(this, R.color.card_border));
        btn.setStrokeWidth(1);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 8, 0, 8);
        btn.setLayoutParams(params);
        btn.setPadding(16, 12, 16, 12);
        return btn;
    }

    // ---------------------------------------------------------------- study

    private void setupStudyPortalViews() {
        RecyclerView rvStudy = findViewById(R.id.rvStudyTopics);
        rvStudy.setLayoutManager(new LinearLayoutManager(this));

        AppState state = store.getState();
        final int request = ++studyRequest;
        contentRepo.loadStudySets(state.getCountry(), state.getLanguage(), new ContentRepository.StudyCallback() {
            @Override
            public void onLoaded(List<StudyTopic> topics) {
                if (isUiGone() || request != studyRequest) return;
                showList(R.id.rvStudyTopics,
                        new StudyAdapter(topics, topic -> store.dispatch(AppEvent.selectStudyTopic(topic))),
                        topics.size(), R.string.study_empty);
            }

            @Override
            public void onError() {
                if (isUiGone() || request != studyRequest) return;
                showList(R.id.rvStudyTopics, new StudyAdapter(new ArrayList<>(), topic -> { }), 0, R.string.study_load_failed);
            }
        });
    }

    private void setupStudyDetailViews(StudyTopic topic) {
        if (topic == null) return;

        TextView tvTitle = findViewById(R.id.tvStudyDetailTitle);
        TextView tvCategory = findViewById(R.id.tvStudyDetailCategory);
        TextView tvReference = findViewById(R.id.tvStudyDetailReference);
        TextView tvContent = findViewById(R.id.tvStudyDetailContent);
        Button btnBack = findViewById(R.id.btnBackToStudyPortal);

        tvTitle.setText(topic.getTitle());
        tvCategory.setText(topic.getCategory());
        if (!topic.getReference().isEmpty()) {
            tvReference.setText(getString(R.string.study_reference, topic.getReference()));
            tvReference.setVisibility(View.VISIBLE);
        }
        tvContent.setText(Html.fromHtml(topic.getFullContent(), Html.FROM_HTML_MODE_LEGACY));
        renderStudyQuiz(topic);

        btnBack.setOnClickListener(v -> store.dispatch(AppEvent.of(AppEvent.Type.NAVIGATE_TO_STUDY_PORTAL)));
    }

    /** Self-check questions under a lesson. Answers are local to the view; they don't earn points. */
    private void renderStudyQuiz(StudyTopic topic) {
        LinearLayout container = findViewById(R.id.llStudyQuizContainer);
        TextView header = findViewById(R.id.tvStudyQuizHeader);
        if (container == null || header == null) return;

        container.removeAllViews();
        List<DailyQuizQuestion> questions = topic.getQuestions();
        header.setVisibility(questions.isEmpty() ? View.GONE : View.VISIBLE);

        for (int i = 0; i < questions.size(); i++) {
            DailyQuizQuestion q = questions.get(i);
            TextView tvQuestion = new TextView(this);
            tvQuestion.setText((i + 1) + ". " + q.getQuestion());
            tvQuestion.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            tvQuestion.setTextSize(15);
            tvQuestion.setTypeface(null, android.graphics.Typeface.BOLD);
            tvQuestion.setPadding(0, 24, 0, 4);
            container.addView(tvQuestion);

            TextView tvExplanation = new TextView(this);
            tvExplanation.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            tvExplanation.setTextSize(13);
            tvExplanation.setPadding(0, 4, 0, 8);
            tvExplanation.setVisibility(View.GONE);

            List<MaterialButton> buttons = new ArrayList<>();
            boolean[] answered = {false};
            for (int optionIndex = 0; optionIndex < q.getOptions().size(); optionIndex++) {
                final int chosen = optionIndex;
                MaterialButton btn = newOptionButton(q.getOptions().get(optionIndex));
                btn.setOnClickListener(v -> {
                    if (answered[0]) return;
                    answered[0] = true;
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    boolean correct = chosen == q.getCorrectIndex();
                    markOption(buttons.get(q.getCorrectIndex()), true);
                    if (!correct) markOption(btn, false);
                    tvExplanation.setText((correct ? getString(R.string.quiz_correct_prefix) : getString(R.string.quiz_incorrect_prefix)) + q.getExplanation());
                    tvExplanation.setVisibility(View.VISIBLE);
                });
                buttons.add(btn);
                container.addView(btn);
            }
            container.addView(tvExplanation);
        }
    }

    private void markOption(MaterialButton btn, boolean correct) {
        btn.setBackgroundTintList(ContextCompat.getColorStateList(this, correct ? R.color.defense_green_bg : R.color.prosecution_red_bg));
        btn.setStrokeColor(ContextCompat.getColorStateList(this, correct ? R.color.defense_green : R.color.prosecution_red));
    }

    // ---------------------------------------------------------------- leaderboard

    private void setupLeaderboardViews() {
        RecyclerView rv = findViewById(R.id.rvLeaderboard);
        if (rv != null) rv.setLayoutManager(new LinearLayoutManager(this));

        bindLeaderboardTab(R.id.btnTabDaily, "daily");
        bindLeaderboardTab(R.id.btnTabWeekly, "weekly");
        bindLeaderboardTab(R.id.btnTabMonthly, "monthly");

        // The freshly inflated screen has no data yet; updateScreenState() loads it once.
        leaderboardLoadedInterval = null;
    }

    private void bindLeaderboardTab(int viewId, String interval) {
        View tab = findViewById(viewId);
        if (tab != null) {
            tab.setOnClickListener(v -> store.dispatch(AppEvent.setLeaderboardInterval(interval)));
        }
    }

    private void updateLeaderboardTabs(String interval) {
        styleTab(R.id.btnTabDaily, "daily".equals(interval));
        styleTab(R.id.btnTabWeekly, "weekly".equals(interval));
        styleTab(R.id.btnTabMonthly, "monthly".equals(interval));
    }

    private void styleTab(int viewId, boolean selected) {
        MaterialButton tab = findViewById(viewId);
        if (tab == null) return;
        tab.setBackgroundTintList(ContextCompat.getColorStateList(this, selected ? R.color.text_primary : R.color.transparent));
        tab.setTextColor(ContextCompat.getColor(this, selected ? R.color.bg_dark : R.color.text_primary));
        tab.setStrokeWidth(selected ? 0 : 1);
    }

    private void loadLeaderboardData(String interval) {
        leaderboardLoadedInterval = interval;
        final int request = ++leaderboardRequest;
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user != null ? user.getUid() : "";

        leaderboardRepo.load(interval, uid, getString(R.string.leaderboard_default_name),
                getString(R.string.you_suffix), new LeaderboardRepository.Callback() {
                    @Override
                    public void onLoaded(List<LeaderboardEntry> entries) {
                        if (isUiGone() || request != leaderboardRequest) return;
                        showList(R.id.rvLeaderboard, new LeaderboardAdapter(entries), entries.size(), R.string.leaderboard_empty);
                    }

                    @Override
                    public void onError() {
                        if (isUiGone() || request != leaderboardRequest) return;
                        // Let the next visit / tab tap retry instead of caching the failure.
                        leaderboardLoadedInterval = null;
                        showList(R.id.rvLeaderboard, new LeaderboardAdapter(new ArrayList<>()), 0, R.string.leaderboard_load_failed);
                    }
                });
    }

    // ---------------------------------------------------------------- feed

    private void setupFeedViews() {
        RecyclerView rvFeed = findViewById(R.id.rvFeed);
        if (rvFeed != null) rvFeed.setLayoutManager(new LinearLayoutManager(this));
        loadFeed();

        EditText etContent = findViewById(R.id.etNewPostContent);
        View btnPublish = findViewById(R.id.btnPublishPost);
        if (btnPublish == null || etContent == null) return;

        btnPublish.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            String text = etContent.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, getString(R.string.feed_empty_content_toast), Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) return;

            Map<String, Object> postMap = new HashMap<>();
            postMap.put("authorUid", user.getUid());
            postMap.put("authorName", store.getState().getUserDisplayName());
            postMap.put("authorRole", getString(R.string.feed_default_author_role));
            postMap.put("content", text);
            postMap.put("likesCount", 0);
            postMap.put("commentsCount", 0);
            postMap.put("createdAt", System.currentTimeMillis());

            db.collection("posts").add(postMap).addOnSuccessListener(docRef -> {
                if (isUiGone()) return;
                etContent.setText("");
                Toast.makeText(this, getString(R.string.feed_post_success_toast), Toast.LENGTH_SHORT).show();
                loadFeed();
            }).addOnFailureListener(e -> {
                if (isUiGone()) return;
                Toast.makeText(this, getString(R.string.feed_post_failure_toast) + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void loadFeed() {
        final int request = ++feedRequest;
        db.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (isUiGone() || request != feedRequest) return;
                    List<FeedPost> posts = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        FeedPost p = doc.toObject(FeedPost.class);
                        if (p != null) {
                            p.setDocId(doc.getId());
                            posts.add(p);
                        }
                    }
                    FeedData.setPosts(posts);
                    showList(R.id.rvFeed, new FeedAdapter(FeedData.getPosts()), posts.size(), R.string.feed_empty);
                })
                .addOnFailureListener(e -> {
                    if (isUiGone() || request != feedRequest) return;
                    // Fall back to the last successfully loaded posts, if any.
                    List<FeedPost> cached = FeedData.getPosts();
                    showList(R.id.rvFeed, new FeedAdapter(cached), cached.size(), R.string.feed_load_failed);
                });
    }

    // ---------------------------------------------------------------- workspaces

    private void setupWorkspaceViews() {
        RecyclerView rv = findViewById(R.id.rvWorkspaces);
        if (rv != null) rv.setLayoutManager(new LinearLayoutManager(this));

        final int request = ++workspaceRequest;
        db.collection("workspaces")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (isUiGone() || request != workspaceRequest) return;
                    List<Workspace> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Workspace w = doc.toObject(Workspace.class);
                        if (w != null) list.add(w);
                    }
                    WorkspaceData.setWorkspaces(list);
                    showList(R.id.rvWorkspaces, new WorkspaceAdapter(WorkspaceData.getWorkspaces()), list.size(), R.string.workspace_empty);
                })
                .addOnFailureListener(e -> {
                    if (isUiGone() || request != workspaceRequest) return;
                    List<Workspace> cached = WorkspaceData.getWorkspaces();
                    showList(R.id.rvWorkspaces, new WorkspaceAdapter(cached), cached.size(), R.string.workspace_load_failed);
                });
    }

    // ---------------------------------------------------------------- profile

    /** Wires the listeners that don't depend on state; values are filled by {@link #bindProfile}. */
    private void setupProfileViews() {
        View btnLanguageToggle = findViewById(R.id.btnLanguageToggle);
        if (btnLanguageToggle != null) {
            btnLanguageToggle.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                store.dispatch(AppEvent.of(AppEvent.Type.TOGGLE_LANGUAGE));
            });
        }

        View btnCountryToggle = findViewById(R.id.btnCountryToggle);
        if (btnCountryToggle != null) {
            btnCountryToggle.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                String[] codes = Jurisdictions.CODES;
                String[] countries = new String[codes.length];
                for (int i = 0; i < codes.length; i++) {
                    countries[i] = jurisdictionDisplayName(codes[i]);
                }
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.jurisdiction_dialog_title))
                        .setItems(countries, (dialog, which) -> store.dispatch(AppEvent.selectCountry(codes[which])))
                        .show();
            });
        }

        View btnSignOut = findViewById(R.id.btnSignOut);
        if (btnSignOut != null) {
            btnSignOut.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                mAuth.signOut();
                googleSignInClient.signOut();
                holder.remoteSynced = false;
                holder.lastSynced = null;
                store.dispatch(AppEvent.of(AppEvent.Type.LOGOUT));
            });
        }

        TextView tvInstagramHandle = findViewById(R.id.tvInstagramHandle);
        if (tvInstagramHandle != null) {
            tvInstagramHandle.setOnClickListener(v -> showInstagramDialog());
        }

        View btnShare = findViewById(R.id.btnShareInstagramStory);
        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                shareProgress(store.getState());
            });
        }

        RecyclerView rvBadges = findViewById(R.id.rvBadges);
        if (rvBadges != null) rvBadges.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void bindProfile(AppState state) {
        TextView tvProfileName = findViewById(R.id.tvProfileName);
        TextView tvProfileEmail = findViewById(R.id.tvProfileEmail);
        TextView tvTotalScore = findViewById(R.id.tvTotalScore);
        TextView tvCasesSolved = findViewById(R.id.tvCasesSolved);
        TextView tvCareerRankBadge = findViewById(R.id.tvCareerRankBadge);
        TextView tvLanguageValue = findViewById(R.id.tvLanguageValue);
        TextView tvCountryValue = findViewById(R.id.tvCountryValue);
        TextView tvInstagramHandle = findViewById(R.id.tvInstagramHandle);
        Switch switchTheme = findViewById(R.id.switchTheme);
        RecyclerView rvBadges = findViewById(R.id.rvBadges);

        if (tvProfileName != null) tvProfileName.setText(state.getUserDisplayName());

        FirebaseUser user = mAuth.getCurrentUser();
        if (tvProfileEmail != null && user != null) tvProfileEmail.setText(user.getEmail());

        int score = state.getCareerScore();
        int casesSolved = state.getCasesSolved();
        if (tvCareerRankBadge != null) tvCareerRankBadge.setText(getCareerRankTitle(score));
        if (tvTotalScore != null) tvTotalScore.setText(String.valueOf(score));
        if (tvCasesSolved != null) tvCasesSolved.setText(String.valueOf(casesSolved));

        if (rvBadges != null) {
            rvBadges.setAdapter(new BadgeAdapter(BadgeData.getAllBadges(score, state.getDailyStreak(), casesSolved)));
        }

        if (tvInstagramHandle != null) {
            String handle = state.getInstagramHandle();
            tvInstagramHandle.setText(handle != null && !handle.isEmpty()
                    ? getString(R.string.instagram_handle_prefix, handle.replace("@", ""))
                    : getString(R.string.instagram_handle_placeholder));
        }

        if (switchTheme != null) {
            // Detach first so programmatic setChecked() doesn't dispatch a toggle.
            switchTheme.setOnCheckedChangeListener(null);
            switchTheme.setChecked(state.isDarkTheme());
            switchTheme.setOnCheckedChangeListener((buttonView, isChecked) ->
                    store.dispatch(AppEvent.setDarkTheme(isChecked)));
        }

        if (tvLanguageValue != null) {
            boolean isTurkish = "tr".equalsIgnoreCase(state.getLanguage());
            tvLanguageValue.setText(isTurkish ? getString(R.string.language_turkish) : getString(R.string.language_english));
        }
        if (tvCountryValue != null) tvCountryValue.setText(jurisdictionDisplayName(state.getCountry()));
    }

    private void showInstagramDialog() {
        String current = store.getState().getInstagramHandle();
        EditText input = new EditText(this);
        input.setHint(getString(R.string.instagram_handle_hint));
        if (current != null) input.setText(current);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.instagram_handle_dialog_title))
                .setMessage(getString(R.string.instagram_handle_dialog_message))
                .setView(input)
                .setPositiveButton(getString(R.string.btn_save), (dialog, which) -> {
                    String handle = input.getText().toString().trim().replace("@", "");
                    store.dispatch(AppEvent.setInstagramHandle(handle.isEmpty() ? null : handle));
                    if (!handle.isEmpty()) {
                        Toast.makeText(this, getString(R.string.instagram_added_toast), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show();
    }

    private void shareProgress(AppState state) {
        String handle = state.getInstagramHandle();
        String shareText = getString(R.string.instagram_share_text,
                getCareerRankTitle(state.getCareerScore()), state.getCareerScore(), state.getDailyStreak())
                + (handle != null && !handle.isEmpty() ? getString(R.string.instagram_share_handle_line, handle) : "")
                + getString(R.string.share_app_link);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.instagram_share_chooser_title)));
    }

    private String jurisdictionDisplayName(String code) {
        if ("US".equals(code)) return getString(R.string.jurisdiction_us);
        if ("UK".equals(code)) return getString(R.string.jurisdiction_uk);
        if ("DE".equals(code)) return getString(R.string.jurisdiction_de);
        if ("FR".equals(code)) return getString(R.string.jurisdiction_fr);
        if ("ES".equals(code)) return getString(R.string.jurisdiction_es);
        if ("IT".equals(code)) return getString(R.string.jurisdiction_it);
        return getString(R.string.jurisdiction_tr);
    }

    private String getCareerRankTitle(int score) {
        if (score >= 1000) return getString(R.string.rank_supreme_justice);
        if (score >= 500) return getString(R.string.rank_senior_judge);
        if (score >= 200) return getString(R.string.rank_specialist_attorney);
        if (score >= 50) return getString(R.string.rank_legal_intern);
        return getString(R.string.rank_law_student);
    }

    // ---------------------------------------------------------------- daily quiz

    private void setupDailyQuizViews() {
        Button btnBack = findViewById(R.id.btnBackFromQuiz);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> store.dispatch(AppEvent.of(AppEvent.Type.NAVIGATE_TO_CASE_LIST)));
        }
        // A fresh visit gets a fresh attempt at loading, even if the last one failed.
        holder.quizFailed = false;

        TextView status = findViewById(R.id.tvQuizStatus);
        if (status != null) {
            status.setOnClickListener(v -> {
                if (holder.quizFailed) {
                    holder.quizFailed = false;
                    AppState state = store.getState();
                    ensureQuizLoaded(state);
                    renderDailyQuiz(state);
                }
            });
        }
    }

    /** Fetches the quiz for the current country/date/language unless it is loaded or loading. */
    private void ensureQuizLoaded(AppState state) {
        final String key = state.getCountry() + "|" + DateKeys.today() + "|" + state.getLanguage();
        if (key.equals(holder.quizKey) || holder.quizLoading || holder.quizFailed) return;

        holder.quizLoading = true;
        holder.quiz = null;
        contentRepo.loadDailyQuiz(state.getCountry(), DateKeys.today(), state.getLanguage(), new ContentRepository.QuizCallback() {
            @Override
            public void onLoaded(List<DailyQuizQuestion> questions) {
                holder.quizLoading = false;
                holder.quiz = questions;
                holder.quizKey = key;
                refreshQuizScreen();
            }

            @Override
            public void onError() {
                holder.quizLoading = false;
                holder.quizFailed = true;
                refreshQuizScreen();
            }
        });
    }

    private void refreshQuizScreen() {
        if (isUiGone() || activeScreen != AppState.Screen.DAILY_QUIZ) return;
        renderDailyQuiz(store.getState());
    }

    private void renderDailyQuiz(AppState state) {
        TextView status = findViewById(R.id.tvQuizStatus);
        View content = findViewById(R.id.svQuizContent);
        TextView tvStreak = findViewById(R.id.tvQuizStreakBadge);
        if (tvStreak != null) tvStreak.setText(getString(R.string.quiz_streak_badge, state.getDailyStreak()));

        List<DailyQuizQuestion> questions = holder.quiz;
        if (questions == null || questions.isEmpty()) {
            if (status != null) {
                status.setText(holder.quizFailed ? R.string.quiz_load_failed : R.string.quiz_loading);
                status.setVisibility(View.VISIBLE);
            }
            if (content != null) content.setVisibility(View.GONE);
            return;
        }
        if (status != null) status.setVisibility(View.GONE);
        if (content != null) content.setVisibility(View.VISIBLE);

        int index = Math.min(state.getQuizIndex(), questions.size() - 1);
        DailyQuizQuestion q = questions.get(index);
        int selected = state.getQuizSelected();
        boolean answered = selected != -1;
        boolean isLast = index == questions.size() - 1;

        ProgressBar pbProgress = findViewById(R.id.pbQuizProgress);
        TextView tvQNum = findViewById(R.id.tvQuizQuestionNum);
        TextView tvQText = findViewById(R.id.tvQuizQuestionText);
        LinearLayout llOptions = findViewById(R.id.llQuizOptionsContainer);
        View cvExplanation = findViewById(R.id.cvQuizExplanation);
        TextView tvExplanationText = findViewById(R.id.tvExplanationText);
        Button btnNext = findViewById(R.id.btnNextQuestion);

        if (pbProgress != null) {
            pbProgress.setMax(questions.size());
            pbProgress.setProgress(index + 1);
        }
        if (tvQNum != null) tvQNum.setText(getString(R.string.quiz_question_num, index + 1, questions.size()));
        if (tvQText != null) tvQText.setText(q.getQuestion());

        if (llOptions != null) {
            llOptions.removeAllViews();
            List<String> options = q.getOptions();
            for (int i = 0; i < options.size(); i++) {
                final int optionIndex = i;
                MaterialButton btn = newOptionButton(options.get(i));

                if (answered && i == q.getCorrectIndex()) {
                    markOption(btn, true);
                } else if (answered && i == selected) {
                    markOption(btn, false);
                }

                btn.setOnClickListener(v -> {
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    store.dispatch(AppEvent.quizAnswer(optionIndex, optionIndex == q.getCorrectIndex()));
                });
                llOptions.addView(btn);
            }
        }

        if (cvExplanation != null) cvExplanation.setVisibility(answered ? View.VISIBLE : View.GONE);
        if (answered && tvExplanationText != null) {
            boolean correct = selected == q.getCorrectIndex();
            tvExplanationText.setText((correct ? getString(R.string.quiz_correct_prefix) : getString(R.string.quiz_incorrect_prefix)) + q.getExplanation());
        }

        if (btnNext != null) {
            btnNext.setText(isLast ? R.string.btn_finish_quiz : R.string.btn_next_question);
            btnNext.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                AppState before = store.getState();
                store.dispatch(AppEvent.quizNext(questions.size(), DateKeys.today()));
                AppState after = store.getState();
                if (before.getActiveScreen() == AppState.Screen.DAILY_QUIZ
                        && after.getActiveScreen() != AppState.Screen.DAILY_QUIZ) {
                    int earned = after.getCareerScore() - before.getCareerScore();
                    boolean alreadyRewarded = DateKeys.today().equals(before.getLastQuizDate());
                    Toast.makeText(this,
                            alreadyRewarded ? getString(R.string.quiz_already_done_toast)
                                    : getString(R.string.quiz_completed_toast, earned),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
