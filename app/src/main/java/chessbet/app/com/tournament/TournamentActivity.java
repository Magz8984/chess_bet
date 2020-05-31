package chessbet.app.com.tournament;

/**
 * @author Elias Baya
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import chessbet.adapter.TournamentsAdapter;
import chessbet.app.com.R;
import chessbet.models.Tournaments;

public class TournamentActivity extends AppCompatActivity {
    private LinearLayout tournaments_linearLayout;
    private LinearLayout mytournaments_linearLayout;
    private RecyclerView tournaments_recyclerView;

    List<Tournaments> tournamentsList;
    TournamentsAdapter tournamentsAdapter;

    static String TOURNAMENT_COLLECTION = "tournaments";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament);

        tournaments_linearLayout = findViewById(R.id.tournaments_linearLayout);
        mytournaments_linearLayout = findViewById(R.id.mytournaments_linearLayout);
        tournaments_recyclerView = findViewById(R.id.tournaments_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // show newest tournament first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        // set layout to recyclerView
        tournaments_recyclerView.setLayoutManager(layoutManager);

        //init tournament lists
        tournamentsList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        loadTournaments();

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

    private void loadTournaments() {
        db.collection("tournaments")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot ds: Objects.requireNonNull(task.getResult())){
                        Tournaments tournaments = ds.toObject(Tournaments.class);
                        tournamentsList.add(tournaments);

                        //adapter
                        tournamentsAdapter = new TournamentsAdapter(TournamentActivity.this, tournamentsList);
                        //set adapter to recyclerview
                        tournaments_recyclerView.setAdapter(tournamentsAdapter);
                    }
                    Log.i("Tournaments", tournamentsList.toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("OnFailureListener", e.getMessage());
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Log.e("TASK", "Cancelled");
            }
        });
    }
}
