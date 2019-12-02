package chessbet.app.com.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import chessbet.adapter.GameDurationAdapter;
import chessbet.api.AccountAPI;
import chessbet.api.ChallengeAPI;
import chessbet.api.MatchAPI;
import chessbet.app.com.BoardActivity;
import chessbet.app.com.R;
import chessbet.domain.Account;
import chessbet.domain.Challenge;
import chessbet.domain.MatchRange;
import chessbet.domain.MatchType;
import chessbet.domain.MatchableAccount;
import chessbet.services.MatchListener;
import chessbet.utils.DatabaseUtil;

public class MatchFragment extends Fragment implements MatchListener, View.OnClickListener,
        FABProgressListener, ChallengeAPI.ChallengeHandler {
    private FloatingActionButton findMatch;
    private FABProgressCircle progressCircle;
    private LinearLayout ratingRangeView;
    private LinearLayout rangeViewHolder;
    private Button btnViewRangeViewHolder;
    private ScrollableNumberPicker startValue;
    private ScrollableNumberPicker endValue;
    private Challenge challenge;

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
        txtAccountRating.setText(String.format(Locale.US,"%d", (AccountAPI.get().getCurrentAccount() != null) ? AccountAPI.get().getCurrentAccount().getElo_rating() : 0));
        startValue = view.findViewById(R.id.startValue);
        endValue = view.findViewById(R.id.endValue);
        matchAPI = MatchAPI.get();
        matchRange = new MatchRange();
        challenge = new Challenge();
        matchAPI.setMatchListener(this);
        findMatch.setOnClickListener(this);
        initializeMatchRangeListeners();
        gameDurations.setAdapter(new GameDurationAdapter(getContext()));
        progressCircle.attachListener(this);
        btnViewRangeViewHolder.setOnClickListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        rangeViewHolder.setVisibility(showRatingView ? View.VISIBLE : View.GONE);
        ChallengeAPI.get().setChallengeHandler(this);
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

    private void createChallenge(){
        challenge.setMatchType(MatchType.PLAY_ONLINE);
        challenge.setEloRating(AccountAPI.get().getCurrentAccount().getElo_rating());
        challenge.setAccepted(false);
        challenge.setOwner(AccountAPI.get().getCurrentUser().getUid());
        challenge.setTimeStamp(System.currentTimeMillis());
        challenge.setDuration(AccountAPI.get().getCurrentAccount().getLast_match_duration());
        challenge.setDateCreated(new Date().toString()); // Help us trouble shoot errors;
        ChallengeAPI.get().setChallenge(challenge);
    }

    private MatchableAccount createMatchableAccount(){
        MatchableAccount matchableAccount = new MatchableAccount();
        matchableAccount.setOwner(user.getUid());
        matchableAccount.setMatch_type(MatchType.PLAY_ONLINE.toString());
        matchableAccount.setDuration(AccountAPI.get().getCurrentAccount().getLast_match_duration());
        matchableAccount.setElo_rating(AccountAPI.get().getCurrentAccount().getElo_rating());
        return matchableAccount;
    }

    @Override
    public void onClick(View v) {
        if(v.equals(findMatch)){
            progressCircle.show();
            createChallenge();
            matchAPI.createUserMatchableAccountImplementation(createMatchableAccount());
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
    public void onMatchableCreatedNotification() {
        ChallengeAPI.get().setMatchRange(matchRange);
        ChallengeAPI.get().getExistingChallenges();
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
        Snackbar.make(progressCircle, "Challenge Found", Snackbar.LENGTH_LONG)
                .setAction("Action",null)
                .show();
    }

    @Override
    public void challengeSent(String id) {
        Toast.makeText(getContext(), "Challenge sent " + id, Toast.LENGTH_LONG).show();
    }

    @Override
    public void challengeFound(String id) {
        progressCircle.beginFinalAnimation();
        Toast.makeText(getContext(), "Challenge found " + id, Toast.LENGTH_LONG).show();
        progressCircle.hide();
    }

    @Override
    public void challengeNotFound() {
        Toast.makeText(getContext(), "Challenge Not Found", Toast.LENGTH_LONG).show();
        ChallengeAPI.get().sendChallenge(challenge);
        progressCircle.hide();
        Log.d("Challenge Found", "Nope");
    }
}
