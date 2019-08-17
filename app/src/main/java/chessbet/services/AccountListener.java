package chessbet.services;

import chessbet.domain.Account;
import chessbet.domain.User;

public interface AccountListener {
    void onAccountReceived(Account account);
    void onUserReceived(User user);
}
