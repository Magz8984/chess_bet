package chessbet.services;

import java.util.List;

import chessbet.domain.Tournament;

public interface TournamentsListener {
    void onTournamentDataReceived(List<Tournament> tournamentsList);
    void onFetchTournamentsListener(Exception e);
}
