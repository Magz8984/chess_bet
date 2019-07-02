package chessbet.app.com;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.adapter.GameDurationAdapter;
import chessbet.domain.MatchType;

public class MatchActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.btnFindMatch) Button findMatch;
    @BindView(R.id.gameDurations) GridView gameDurations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        ButterKnife.bind(this);
        findMatch.setOnClickListener(this);
        gameDurations.setAdapter(new GameDurationAdapter(this));
    }

    @Override
    public void onClick(View v) {
        if(v.equals(findMatch)){
            Intent intent=new Intent(this, BoardActivity.class);
            intent.putExtra("match_type", MatchType.PLAY_ONLINE.toString());
            startActivity(intent);
        }
    }
}
