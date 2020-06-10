package chessbet.app.com.tournament;

/**
 * @author Elias Baya
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import chessbet.adapter.TournamentsAdapter;
import chessbet.api.TournamentsAPI;
import chessbet.app.com.R;
import chessbet.domain.Tournament;
import chessbet.services.TournamentsListener;

public class TournamentActivity extends AppCompatActivity implements TournamentsListener, View.OnClickListener {
    private LinearLayout tournaments_linearLayout;
    private LinearLayout mytournaments_linearLayout;
    private RecyclerView tournaments_recyclerView;
    TournamentsAdapter tournamentsAdapter;
    private TournamentsAPI tournamentsAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament);

        //init views
        tournaments_linearLayout = findViewById(R.id.tournaments_linearLayout);
        mytournaments_linearLayout = findViewById(R.id.mytournaments_linearLayout);
        tournaments_recyclerView = findViewById(R.id.tournaments_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // show newest tournament first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        // set layout to recyclerView
        tournaments_recyclerView.setLayoutManager(layoutManager);

        tournaments_linearLayout.setOnClickListener(this);
        mytournaments_linearLayout.setOnClickListener(this);


        tournamentsAPI = new TournamentsAPI();
        tournamentsAPI.loadTournaments();
        tournamentsAPI.setTournamentsListener(this);
    }

    @Override
    public void onTournamentDataReceived(List<Tournament> tournamentsList) {
        //adapter
        tournamentsAdapter = new TournamentsAdapter(TournamentActivity.this, tournamentsList);
        //set adapter to recyclerview
        tournaments_recyclerView.setAdapter(tournamentsAdapter);
    }

    @Override
    public void onFetchTournamentsListener(Exception e) {
        Log.e("OnFailureListener", e.getMessage());
    }

    @Override
    public void onClick(View v) {
        if(v == tournaments_linearLayout){
            //refresh
            startActivity(new Intent(new Intent(TournamentActivity.this, TournamentActivity.class)));
            finish();
        }
        if (v == mytournaments_linearLayout){
            //got to my tournaments Activity
            startActivity(new Intent(new Intent(TournamentActivity.this, MyTournamentsActivity.class)));
            finish();
        }

    }
}
