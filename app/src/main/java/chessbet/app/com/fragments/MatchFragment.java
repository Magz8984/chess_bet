package chessbet.app.com.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Switch;
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
import chessbet.services.MatchListener;
import chessbet.utils.DatabaseUtil;

public class MatchFragment extends Fragment implements MatchListener, View.OnClickListener, FABProgressListener, CompoundButton.OnCheckedChangeListener {
    private FloatingActionButton findMatch;
    private GridView gameDurations;
    private Switch matchOnRatingSwitch;
    private FABProgressCircle progressCircle;
    private LinearLayout ratingRangeView;
    private LinearLayout rangeViewHolder;
    private Button btnViewRangeViewHolder;
    private ScrollableNumberPicker startValue;
    private ScrollableNumberPicker endValue;
    private TextView txtAccountRating;

    private boolean showRatingView = false;

    private MatchAPI matchAPI;
    private FirebaseUser user;
    private MatchRange matchRange;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match,container,false);
        findMatch = view.findViewById(R.id.btnFindMatch);
        gameDurations = view.findViewById(R.id.gameDurations);
        matchOnRatingSwitch = view.findViewById(R.id.matchOnRating);
        progressCircle = view.findViewById(R.id.fabProgressCircle);
        ratingRangeView = view.findViewById(R.id.ratingRange);
        btnViewRangeViewHolder = view.findViewById(R.id.btnRatingRange);
        rangeViewHolder = view.findViewById(R.id.rangeViews);
        txtAccountRating = view.findViewById(R.id.txtAccountRating);
        txtAccountRating.setText(String.format(Locale.US,"%d", AccountAPI.get().getCurrentAccount().getElo_rating()));
        startValue = view.findViewById(R.id.startValue);
        endValue = view.findViewById(R.id.endValue);
        matchAPI = new MatchAPI();
        matchRange = new MatchRange();
        matchAPI.setMatchListener(this);
        findMatch.setOnClickListener(this);
        initializeMatchRangeListeners();
        gameDurations.setAdapter(new GameDurationAdapter(getContext()));
        progressCircle.attachListener(this);
        btnViewRangeViewHolder.setOnClickListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        rangeViewHolder.setVisibility(showRatingView ? View.VISIBLE : View.GONE);
        matchOnRatingSwitch.setOnCheckedChangeListener(this);
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
            if(user != null){
                if(matchOnRatingSwitch.isChecked()){
                    matchAPI.createUserMatchableAccountImplementation(user.getUid(), MatchType.PLAY_ONLINE, null);
                }
                else{
                    matchAPI.createUserMatchableAccountImplementation(user.getUid(), MatchType.PLAY_ONLINE, matchRange);
                }
            }
            else{
                progressCircle.hide();
            }
        }
        else if(v.equals(btnViewRangeViewHolder)){
            showRatingView =! showRatingView;
            TransitionManager.beginDelayedTransition(ratingRangeView);
            rangeViewHolder.setVisibility(showRatingView ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            btnViewRangeViewHolder.setEnabled(false);
            rangeViewHolder.setVisibility(View.GONE);
            Snackbar.make(progressCircle, getResources().getString(R.string.match_exact_rating), Snackbar.LENGTH_LONG)
                    .setAction("Action",null)
                    .show();
        }else {

            btnViewRangeViewHolder.setEnabled(true);
        }
    }

    @Override
    public void onMatchMade(MatchableAccount matchableAccount) {
        progressCircle.beginFinalAnimation();
        Intent target= new Intent(getContext(), BoardActivity.class);
        target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putParcelable(DatabaseUtil.matchables,matchableAccount);
        target.putExtras(bundle);
        startActivity(target);
    }

    @Override
    public void onMatchCreatedNotification() {
        progressCircle.beginFinalAnimation();
    }

    @Override
    public void onMatchError() {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> new Handler().postDelayed(() -> progressCircle.hide(),40000)); // Waits for 40 seconds before hiding the progress bar

    }

    @Override
    public void onFABProgressAnimationEnd() {
        Snackbar.make(progressCircle, "Match Created", Snackbar.LENGTH_LONG)
                .setAction("Action",null)
                .show();
    }
}
