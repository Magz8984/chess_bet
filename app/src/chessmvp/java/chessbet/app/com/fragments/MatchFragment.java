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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;
import com.transitionseverywhere.TransitionManager;

import java.util.Locale;
import java.util.Objects;

import chessbet.adapter.GameDurationAdapter;
import chessbet.adapter.MatchesAdapter;
import chessbet.api.AccountAPI;
import chessbet.api.ChallengeAPI;
import chessbet.api.MatchAPI;
import chessbet.app.com.BoardActivity;
import chessbet.app.com.R;
import chessbet.domain.Challenge;
import chessbet.domain.ChallengeDTO;
import chessbet.domain.MatchRange;
import chessbet.domain.MatchableAccount;
import chessbet.services.MatchListener;
import chessbet.services.MatchService;
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
    private RecyclerView recMatches;

    private boolean showRatingView = false;

    private MatchAPI matchAPI;
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
        recMatches = view.findViewById(R.id.recMatches);
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
        recMatches.setHasFixedSize(true);
        recMatches.setLayoutManager(new LinearLayoutManager(getContext()));
        initializeMatchRangeListeners();
        gameDurations.setAdapter(new GameDurationAdapter(getContext()));
        progressCircle.attachListener(this);
        btnViewRangeViewHolder.setOnClickListener(this);
        rangeViewHolder.setVisibility(showRatingView ? View.VISIBLE : View.GONE);
        ChallengeAPI.get().setChallengeHandler(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Gets the account
        findMatch.setEnabled(true);
        recMatches.setAdapter(new MatchesAdapter(getContext()));
        matchAPI.getAccount();
    }

    private void initializeMatchRangeListeners() {
        startValue.setListener(value -> matchRange.setStartAt(value));
        endValue.setListener(value -> matchRange.setEndAt(value));
    }

    @Override
    public void onClick(View v) {
        if(v.equals(findMatch)){
            if(AccountAPI.get().getCurrentAccount().getLast_match_duration() == 0){
                    Toast.makeText(getContext(), "DatabaseMatch duration not selected", Toast.LENGTH_LONG).show();
            } else {
                findMatch.setEnabled(false);
                progressCircle.show();
                ChallengeDTO challengeDTO = new ChallengeDTO.Builder()
                        .setOwner(AccountAPI.get().getCurrentAccount().getOwner())
                        .setDuration((int) AccountAPI.get().getCurrentAccount().getLast_match_duration())
                        .setEloRating(AccountAPI.get().getCurrentAccount().getElo_rating())
                        .setMaxEloRating(AccountAPI.get().getCurrentAccount().getElo_rating() + matchRange.getEndAt())
                        .setMinEloRating(AccountAPI.get().getCurrentAccount().getElo_rating() - matchRange.getStartAt())
                        .setType(Challenge.Type.CHALLENGE)
                        .build();
                ChallengeAPI.get().getSetChallengeImplementation(challengeDTO);
            }
        }

        else if(v.equals(btnViewRangeViewHolder)){
            showRatingView =! showRatingView;
            TransitionManager.beginDelayedTransition(ratingRangeView);
            rangeViewHolder.setVisibility(showRatingView ? View.VISIBLE : View.GONE);
//            rangeViewHolder.bringToFront();
        }
    }

    @Override
    public void onMatchMade(MatchableAccount matchableAccount) {
        try {
            ChallengeAPI.get().setOnChallenge(true);
            AccountAPI.get().getCurrentAccount().setLast_match_duration(0);
            progressCircle.beginFinalAnimation();
            requireContext().startService(new Intent(getContext(), MatchService.class));
            new Handler().postDelayed(() -> {
                Intent target= new Intent(getContext(), BoardActivity.class);
                target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle bundle = new Bundle();
                bundle.putParcelable(DatabaseUtil.matchables,matchableAccount);
                target.putExtras(bundle);
                startActivity(target);
            },3000);
        } catch (Exception ex){
            Crashlytics.logException(ex);
        }
    }

    @Override
    public void onMatchableCreatedNotification() {
    }

    @Override
    public void onMatchError() {
        try {
            requireActivity().runOnUiThread(() -> new Handler().postDelayed(() -> progressCircle.hide(),40000)); // Waits for 40 seconds before hiding the progress bar
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onFABProgressAnimationEnd() {
        Snackbar.make(progressCircle, "Challenge Found", Snackbar.LENGTH_LONG)
                .setAction("Action",null)
                .show();
    }

    @Override
    public void challengeSent(String id) {
        findMatch.setEnabled(true);
    }

    @Override
    public void challengeFound(String response) {
        FragmentActivity activity = getActivity();
        if(activity != null) {
            activity.runOnUiThread(() -> {
                Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                progressCircle.beginFinalAnimation();
                progressCircle.hide();
            });
        }
    }

    @Override
    public void challengeNotFound() {
        FragmentActivity activity = getActivity();
        if(activity != null) {
            activity.runOnUiThread(() -> {
                Toast.makeText(getContext(), "ERROR WHILE GETTING CHALLENGE", Toast.LENGTH_LONG).show();
                findMatch.setEnabled(true);
            });
        }
    }
}
