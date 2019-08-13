package chessbet.services;

import chessbet.domain.RemoteMove;

public interface RemoteViewUpdateListener {
    void onRemoteMoveMade(RemoteMove remoteMove);
}
