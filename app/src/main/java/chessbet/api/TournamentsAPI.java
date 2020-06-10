package chessbet.api;

/**
 * @author Elias Baya
 */

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import chessbet.domain.Tournament;
import chessbet.services.TournamentsListener;

public class TournamentsAPI {
    private static String TOURNAMENTS_COLLECTION = "tournaments";
    private FirebaseFirestore db;
    private List<Tournament> tournamentsList;
    private TournamentsListener tournamentsListener;

    public TournamentsAPI() {
        db = FirebaseFirestore.getInstance();
        tournamentsList = new ArrayList<>();
    }

    public void loadTournaments(){
        db.collection(TOURNAMENTS_COLLECTION)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot ds: Objects.requireNonNull(task.getResult())){
                            Tournament tournament = ds.toObject(Tournament.class);
                            tournamentsList.add(tournament);
                        }
                        tournamentsListener.onTournamentDataReceived(tournamentsList);
                    }
                }).addOnFailureListener(e -> tournamentsListener.onFetchTournamentsListener(e));
    }

    public void setTournamentsListener(TournamentsListener tournamentsListener) {
        this.tournamentsListener = tournamentsListener;
    }

}
