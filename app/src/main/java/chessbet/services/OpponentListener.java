package chessbet.services;

import chessbet.domain.User;

public interface OpponentListener {
    void onOpponentReceived(User user);
}
