package chessbet.services;

import chessbet.domain.RemoteMove;

public interface RemoteMoveListener {
    void onRemoteMoveMade(RemoteMove remoteMove);
}
