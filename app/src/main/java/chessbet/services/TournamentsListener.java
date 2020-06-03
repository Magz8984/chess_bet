package chessbet.services;

import java.util.List;

import chessbet.domain.Tournaments;

public interface TournamentsListener {
    void onTournamentDataReceived(List<Tournaments> tournamentsList);
    void onFetchTournamentsListener(Exception e);
}
