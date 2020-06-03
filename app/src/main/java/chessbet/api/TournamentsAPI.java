package chessbet.api;

/**
 * @author Elias Baya
 */

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import chessbet.domain.Tournaments;
import chessbet.services.TournamentsListener;

public class TournamentsAPI {
    private static String TOURNAMENTS_COLLECTION = "tournaments";
    private FirebaseFirestore db;
    private List<Tournaments> tournamentsList;
    private TournamentsListener tournamentsListener;

    public TournamentsAPI() {
        db = FirebaseFirestore.getInstance();
        tournamentsList = new ArrayList<>();
    }

    public void loadTournaments(){
        db.collection(TOURNAMENTS_COLLECTION)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot ds: Objects.requireNonNull(task.getResult())){
                        Tournaments tournaments = ds.toObject(Tournaments.class);
                        tournamentsList.add(tournaments);
                    }
                    tournamentsListener.onTournamentDataReceived(tournamentsList);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                tournamentsListener.onFetchTournamentsListener(e);
            }
        });
    }

    public void setTournamentsListener(TournamentsListener tournamentsListener) {
        this.tournamentsListener = tournamentsListener;
    }

}
