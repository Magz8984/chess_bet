package chessbet.app.com;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;
import com.transitionseverywhere.TransitionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.adapter.GameDurationAdapter;
import chessbet.api.MatchAPI;
import chessbet.domain.MatchRange;
import chessbet.domain.MatchType;
import chessbet.domain.MatchableAccount;
import chessbet.services.MatchListener;
import chessbet.utils.DatabaseUtil;


public class MatchActivity extends AppCompatActivity implements MatchListener, View.OnClickListener, FABProgressListener, CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.btnFindMatch)
    FloatingActionButton findMatch;
    @BindView(R.id.gameDurations) GridView gameDurations;
    @BindView(R.id.matchOnRating) Switch matchOnRatingSwitch;
    @BindView(R.id.fabProgressCircle) FABProgressCircle progressCircle;
    @BindView(R.id.ratingRange) LinearLayout ratingRangeView;
    @BindView(R.id.rangeViews) LinearLayout rangeViewHolder;
    @BindView(R.id.btnRatingRange) Button btnViewRangeViewHolder;
    @BindView(R.id.startValue) ScrollableNumberPicker startValue;
    @BindView(R.id.endValue) ScrollableNumberPicker endValue;

    private boolean showRatingView = false;

    private MatchAPI matchAPI;
    private FirebaseUser user;
    private MatchRange matchRange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        matchAPI = new MatchAPI();
        matchRange = new MatchRange();
        matchAPI.setMatchListener(this);
        setContentView(R.layout.activity_match);
        ButterKnife.bind(this);
        findMatch.setOnClickListener(this);
        initializeMatchRangeListeners();
        gameDurations.setAdapter(new GameDurationAdapter(this));
        progressCircle.attachListener(this);
        btnViewRangeViewHolder.setOnClickListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        rangeViewHolder.setVisibility(showRatingView ? View.VISIBLE : View.GONE);
        matchOnRatingSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
        matchAPI.getAccount();
    }

    private void initializeMatchRangeListeners(){
        startValue.setListener(value -> matchRange.setStartAt(value));
        endValue.setListener(value -> matchRange.setEndAt(value));
    }

    @Override
    public void onClick(View v) {
        if(v.equals(findMatch)){
            progressCircle.show();
            if(user != null){
                if(matchOnRatingSwitch.isChecked()){
                    matchAPI.createUseAccountImplementation(user.getUid(), MatchType.PLAY_ONLINE, null);
                }
                else{
                    matchAPI.createUseAccountImplementation(user.getUid(), MatchType.PLAY_ONLINE, matchRange);
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
    public void onFABProgressAnimationEnd() {
        Snackbar.make(progressCircle, "Match Created", Snackbar.LENGTH_LONG)
                .setAction("Action",null)
                .show();
    }

    @Override
    public void onMatchMade(MatchableAccount matchableAccount) {
        progressCircle.beginFinalAnimation();
        Intent target= new Intent(this, BoardActivity.class);
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
        runOnUiThread(() -> new Handler().postDelayed(() -> progressCircle.hide(),40000)); // Waits for 40 seconds before hiding the progress bar
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            btnViewRangeViewHolder.setEnabled(false);
            rangeViewHolder.setVisibility(View.GONE);
            Snackbar.make(progressCircle, "Match Is Set On Exact Rating", Snackbar.LENGTH_LONG)
                    .setAction("Action",null)
                    .show();
        }else {

            btnViewRangeViewHolder.setEnabled(true);
        }
    }
}
