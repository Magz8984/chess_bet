package chessbet.app.com.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;
import com.transitionseverywhere.TransitionManager;

import java.util.Locale;
import java.util.Objects;

import chessbet.adapter.GameDurationAdapter;
import chessbet.api.AccountAPI;
import chessbet.api.MatchAPI;
import chessbet.app.com.BoardActivity;
import chessbet.app.com.R;
import chessbet.domain.MatchRange;
import chessbet.domain.MatchType;
import chessbet.domain.MatchableAccount;
import chessbet.domain.User;
import chessbet.services.MatchListener;
import chessbet.services.MatchMetricsUpdateListener;
import chessbet.utils.DatabaseUtil;

public class MatchFragment extends Fragment implements MatchListener, View.OnClickListener, FABProgressListener, MatchMetricsUpdateListener {
    private FloatingActionButton findMatch;
    private FABProgressCircle progressCircle;
    private LinearLayout ratingRangeView;
    private LinearLayout rangeViewHolder;
    private Button btnViewRangeViewHolder;
    private ScrollableNumberPicker startValue;
    private ScrollableNumberPicker endValue;

    private boolean showRatingView = false;

    private MatchAPI matchAPI;
    private FirebaseUser user;
    private MatchRange matchRange;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match,container,false);
        findMatch = view.findViewById(R.id.btnFindMatch);
        GridView gameDurations = view.findViewById(R.id.gameDurations);
        progressCircle = view.findViewById(R.id.fabProgressCircle);
        ratingRangeView = view.findViewById(R.id.ratingRange);
        btnViewRangeViewHolder = view.findViewById(R.id.btnRatingRange);
        rangeViewHolder = view.findViewById(R.id.rangeViews);
        TextView txtAccountRating = view.findViewById(R.id.txtAccountRating);
        txtAccountRating.setText(String.format(Locale.US,"%d", AccountAPI.get().getCurrentAccount().getElo_rating()));
        startValue = view.findViewById(R.id.startValue);
        endValue = view.findViewById(R.id.endValue);
        matchAPI = MatchAPI.get();
        matchRange = new MatchRange();
        matchAPI.setMatchListener(this);
        findMatch.setOnClickListener(this);
        initializeMatchRangeListeners();
        gameDurations.setAdapter(new GameDurationAdapter(getContext()));
        progressCircle.attachListener(this);
        btnViewRangeViewHolder.setOnClickListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        rangeViewHolder.setVisibility(showRatingView ? View.VISIBLE : View.GONE);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        matchAPI.getAccount();
    }

    private void initializeMatchRangeListeners() {
        startValue.setListener(value -> matchRange.setStartAt(value));
        endValue.setListener(value -> matchRange.setEndAt(value));
    }

    @Override
    public void onClick(View v) {
        if(v.equals(findMatch)){
            progressCircle.show();
            AccountAPI.get().getCurrentAccount().setLast_match_type(MatchType.PLAY_ONLINE);
            AccountAPI.get().getCurrentAccount().setMatched(false);
            // Shift Account API focus to this class onUpdate()
            AccountAPI.get().setMatchMetricsUpdateListener(this);
            AccountAPI.get().updateAccountMatchDetails();
        }

        else if(v.equals(btnViewRangeViewHolder)){
            showRatingView =! showRatingView;
            TransitionManager.beginDelayedTransition(ratingRangeView);
            rangeViewHolder.setVisibility(showRatingView ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onMatchMade(MatchableAccount matchableAccount) {
        progressCircle.beginFinalAnimation();
        new Handler().postDelayed(() -> {
            Intent target= new Intent(getContext(), BoardActivity.class);
            target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putParcelable(DatabaseUtil.matchables,matchableAccount);
            target.putExtras(bundle);
            startActivity(target);
        },3000);
    }

    @Override
    public void onMatchCreatedNotification(User user) {
        if(user.getEmail() != null){
            // Begin final animation only when user is received
            progressCircle.beginFinalAnimation();
        }
    }

    @Override
    public void onMatchError() {
        try {
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> new Handler().postDelayed(() -> progressCircle.hide(),40000)); // Waits for 40 seconds before hiding the progress bar
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onFABProgressAnimationEnd() {
        Snackbar.make(progressCircle, "Match Created", Snackbar.LENGTH_LONG)
                .setAction("Action",null)
                .show();
    }

    @Override
    public void onUpdate() {
        user = FirebaseAuth.getInstance().getCurrentUser(); // Revalidate user
        if(user != null){
            matchAPI.createUserMatchableAccountImplementation(user.getUid(), MatchType.PLAY_ONLINE, matchRange);
        }
        else{
            progressCircle.hide();
        }
    }
}
