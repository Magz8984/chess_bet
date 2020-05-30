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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

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
        //path of all tournaments
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tournaments");
        // offline synchronization of data to access them even when offline
        ref.keepSynced(true);

        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tournamentsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Tournaments tournaments = ds.getValue(Tournaments.class);
                    tournamentsList.add(tournaments);

                    //adapter
                    tournamentsAdapter = new TournamentsAdapter(TournamentActivity.this, tournamentsList);
                    //set adapter to recyclerview
                    tournaments_recyclerView.setAdapter(tournamentsAdapter);
                }

                Log.e("Tournaments", tournamentsList.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // in case of error
                Log.e("DatabaseError", databaseError.getMessage());
            }
        });
    }
}
