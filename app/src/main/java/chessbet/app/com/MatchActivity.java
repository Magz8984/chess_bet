package chessbet.app.com;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.adapter.GameDurationAdapter;
import chessbet.api.MatchAPI;
import chessbet.domain.MatchType;
import chessbet.domain.MatchableAccount;
import chessbet.services.MatchListener;
import chessbet.services.MatchService;

public class MatchActivity extends AppCompatActivity implements MatchListener, View.OnClickListener, FABProgressListener {
    @BindView(R.id.btnFindMatch)
    FloatingActionButton findMatch;
    @BindView(R.id.gameDurations) GridView gameDurations;
    @BindView(R.id.fabProgressCircle)
    FABProgressCircle progressCircle;
    private MatchAPI matchAPI;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        matchAPI = new MatchAPI();
        matchAPI.setMatchListener(this);
        setContentView(R.layout.activity_match);
        ButterKnife.bind(this);
        findMatch.setOnClickListener(this);
        gameDurations.setAdapter(new GameDurationAdapter(this));
        progressCircle.attachListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        if(v.equals(findMatch)){
            progressCircle.show();
            if(user !=null){
                matchAPI.getOnMatchNotificationOnEloRating(user.getUid(), MatchType.PLAY_ONLINE);
            }
            else{
                progressCircle.hide();
            }
        }
    }

    @Override
    public void onFABProgressAnimationEnd() {
        Snackbar.make(progressCircle, "Match Created", Snackbar.LENGTH_LONG)
                .setAction("Action",null)
                .show();
    }

    @Override
    public void onMatch(MatchableAccount matchableAccount) {
        progressCircle.beginFinalAnimation();
        Intent intent = new Intent(this, MatchService.class);
        startService(intent);
        Log.d("oKAY","dONE");
    }

    @Override
    public void onMatchError() {
        runOnUiThread(() -> {
            progressCircle.hide();
            Log.d("oKAY","NdONE");
        });

    }
}
