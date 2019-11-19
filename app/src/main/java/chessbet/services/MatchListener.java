package chessbet.services;

import chessbet.domain.MatchableAccount;
import chessbet.domain.User;

public interface MatchListener {
    void onMatchMade(MatchableAccount matchableAccount);
    void onMatchCreatedNotification(User user);
    void onMatchError();
}
