package chessbet.services;

import chessbet.domain.MatchableAccount;

public interface MatchListener {
    void onMatch(MatchableAccount matchableAccount);
}
