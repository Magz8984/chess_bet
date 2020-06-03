package chessbet.app.com.tournament;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import chessbet.app.com.R;

public class MyTournamentsActivity extends AppCompatActivity {
    private LinearLayout tournaments_linearLayout;
    private LinearLayout mytournaments_linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tournaments);
        initViews();
    }

    private void initViews() {
        tournaments_linearLayout = findViewById(R.id.tournaments_linearLayout);
        mytournaments_linearLayout = findViewById(R.id.mytournaments_linearLayout);

        tournaments_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //refresh
                startActivity(new Intent(new Intent(MyTournamentsActivity.this, TournamentActivity.class)));
                finish();
            }
        });

        mytournaments_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //refresh
                startActivity(new Intent(new Intent(MyTournamentsActivity.this, MyTournamentsActivity.class)));
                finish();
            }
        });
    }

}
