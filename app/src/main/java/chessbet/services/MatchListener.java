package chessbet.services;

import chessbet.domain.MatchableAccount;

public interface MatchListener {
    void onMatchMade(MatchableAccount matchableAccount);
    void onMatchableCreatedNotification();
    void onMatchError();
}
