package chessbet.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

import chessbet.domain.Constants;
import chessbet.domain.MatchEvent;
import chessbet.domain.MatchableAccount;
import chessbet.domain.RemoteMove;
import chessbet.services.MatchListener;
import chessbet.services.RemoteMoveListener;
import chessbet.utils.DatabaseUtil;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MatchAPI implements Serializable {
    private FirebaseUser firebaseUser;
    private MatchListener matchListener;
    private RemoteMoveListener remoteMoveListener;
    private static MatchAPI INSTANCE = new MatchAPI();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private MatchAPI(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    public static MatchAPI get(){
        return INSTANCE;
    }

    public void setMatchListener(MatchListener matchListener) {
        this.matchListener = matchListener;
    }

    public void setRemoteMoveListener(RemoteMoveListener remoteMoveListener) {
        this.remoteMoveListener = remoteMoveListener;
    }

    public void getAccount(){
        RemoteMove.get().clear();
        final MatchableAccount[] matchable = {null};
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        DatabaseUtil.getAccount(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                matchable[0] = dataSnapshot.getValue(MatchableAccount.class);
                MatchableAccount matchableAccount = matchable[0];
                if(matchableAccount != null){
                    if(matchableAccount.isMatched()){
                        // Remove listener once the match is made
                        DatabaseUtil.getAccount(firebaseUser.getUid()).removeEventListener(this);
                        // Add Started Event
                        RemoteMove.get().addEvent(MatchEvent.IN_PROGRESS);
                        RemoteMove.get().setOwner(matchableAccount.getOwner());
                        RemoteMove.get().send(matchableAccount.getMatchId(),matchableAccount.getSelf());
                        // Confirms a match has been made
                        matchListener.onMatchMade(matchableAccount);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void sendMoveData(MatchableAccount matchableAccount,int source, int destination, String pgn, long gameTimeLeft){
        RemoteMove.get().setOwner(matchableAccount.getOwner());
        RemoteMove.get().setFrom(source);
        RemoteMove.get().setGameTimeLeft(gameTimeLeft); // Regulate game play
        RemoteMove.get().setTo(destination);
        RemoteMove.get().setPgn(pgn); // Pgn string of the match
        RemoteMove.get().send(matchableAccount.getMatchId(),matchableAccount.getSelf());
    }

    // TODO Implement remote move events
    public void getRemoteMoveData(MatchableAccount matchableAccount){
        DatabaseUtil.getOpponentRemoteMove(matchableAccount.getMatchId(), matchableAccount.getOpponent())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        RemoteMove remoteMove = dataSnapshot.getValue(RemoteMove.class);
                        if(remoteMove != null) {
                            if(isMatchStarted(remoteMove)){
                                if(!isMatchInterrupted(remoteMove)) {
                                    remoteMoveListener.onRemoteMoveMade(remoteMove);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Crashlytics.log(databaseError.getMessage());
                    }
                });
    }

    private boolean isMatchInterrupted(RemoteMove remoteMove){
        return remoteMove != null && remoteMove.getEvents().contains(MatchEvent.INTERRUPTED.toString());
    }

    private boolean isMatchStarted(RemoteMove remoteMove){
        return remoteMove != null && remoteMove.getEvents().contains(MatchEvent.IN_PROGRESS.toString());
    }

    private void createUserMatchableAccount(MatchableAccount matchableAccount, Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                        .cache(null)
                                        .build();

        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(Constants.CLOUD_FUNCTIONS_URL.concat(Constants.CREATE_USER_MATCHABLE_ACCOUNT))).newBuilder();
        String url = builder.build().toString();

        RequestBody requestBody = RequestBody.create(JSON,new Gson().toJson(matchableAccount));
        Log.d("MATCH_ERROR", new Gson().toJson(matchableAccount) + "\n" + url);

        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    public void createUserMatchableAccountImplementation(MatchableAccount matchableAccount){
        createUserMatchableAccount(matchableAccount, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                matchListener.onMatchError();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Match User after creating a matchable
                if(response.code() == 200){
                    matchListener.onMatchableCreatedNotification();
                }
                else{
                    Log.d("MATCH_ERROR", Objects.requireNonNull(response.body()).string());
                    matchListener.onMatchError();
                }
            }
        });
    }
}
