package chessbet.app.com.tournament;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import chessbet.app.com.R;

public class TournamentActivity extends AppCompatActivity {
    private LinearLayout tournaments_linearLayout;
    private LinearLayout mytournaments_linearLayout;
    private RecyclerView tournaments_recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament);
        initViews();
    }

    private void initViews() {
        tournaments_linearLayout = findViewById(R.id.tournaments_linearLayout);
        mytournaments_linearLayout = findViewById(R.id.mytournaments_linearLayout);
        tournaments_recyclerView = findViewById(R.id.tournaments_recyclerView);

        tournaments_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //refresh
                startActivity(new Intent(new Intent(TournamentActivity.this, TournamentActivity.class)));
                finish();
            }
        });

        mytournaments_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //refresh
                startActivity(new Intent(new Intent(TournamentActivity.this, MyTournamentsActivity.class)));
                finish();
            }
        });
    }
}
