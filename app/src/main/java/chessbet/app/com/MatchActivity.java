package chessbet.app.com;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.adapter.GameDurationAdapter;
import chessbet.domain.MatchType;

public class MatchActivity extends AppCompatActivity implements View.OnClickListener, FABProgressListener {
    @BindView(R.id.btnFindMatch)
    FloatingActionButton findMatch;
    @BindView(R.id.gameDurations) GridView gameDurations;
    @BindView(R.id.fabProgressCircle)
    FABProgressCircle progressCircle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        ButterKnife.bind(this);
        findMatch.setOnClickListener(this);
        gameDurations.setAdapter(new GameDurationAdapter(this));
        progressCircle.attachListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(findMatch)){
            progressCircle.show();
            progressCircle.beginFinalAnimation();
        }
    }

    @Override
    public void onFABProgressAnimationEnd() {
        Snackbar.make(progressCircle, "Match Created", Snackbar.LENGTH_LONG)
                .setAction("Action",null)
                .show();
    }
}
