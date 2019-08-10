package chessbet.api;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Objects;

import chessbet.domain.Constants;
import chessbet.domain.MatchEvent;
import chessbet.domain.MatchType;
import chessbet.domain.MatchableAccount;
import chessbet.domain.RemoteMove;
import chessbet.services.MatchListener;
import chessbet.services.RemoteMoveListener;
import chessbet.utils.DatabaseUtil;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MatchAPI {
    private FirebaseUser firebaseUser;
    private MatchListener matchListener;
    private RemoteMoveListener remoteMoveListener;
    public MatchAPI(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    public void setMatchListener(MatchListener matchListener) {
        this.matchListener = matchListener;
    }

    public void setRemoteMoveListener(RemoteMoveListener remoteMoveListener) {
        this.remoteMoveListener = remoteMoveListener;
    }

    public void getAccount(){
        final MatchableAccount[] matchable = {null};
        DatabaseUtil.getAccount(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                matchable[0] = dataSnapshot.getValue(MatchableAccount.class);
                MatchableAccount matchableAccount = matchable[0];
                if(matchableAccount != null){
                    if(matchableAccount.isMatched()){
                        // Remove listener once the match is made
                        DatabaseUtil.getAccount(firebaseUser.getUid()).removeEventListener(this);
                        // Add Started Event
                        RemoteMove.get().addEvent(MatchEvent.IN_PROGRESS);
                        RemoteMove.get().send(matchableAccount.getMatchId(),matchableAccount.getSelf());
                        // Confirms a match has been made
                        matchListener.onMatchMade(matchableAccount);

                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendMoveData(MatchableAccount matchableAccount,int source, int destination){
        RemoteMove.get().setOwner(matchableAccount.getOwner());
        RemoteMove.get().setFrom(source);
        RemoteMove.get().setTo(destination);
        RemoteMove.get().send(matchableAccount.getMatchId(),matchableAccount.getSelf());
    }
    // TODO Implement remote move events
    public void getRemoteMoveData(MatchableAccount matchableAccount){
        DatabaseUtil.getOpponentRemoteMove(matchableAccount.getMatchId(), matchableAccount.getOpponent())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        RemoteMove remoteMove = dataSnapshot.getValue(RemoteMove.class);
                        if(isMatchStarted(remoteMove)){
                            if(!isMatchInterrupted(remoteMove)) {
                                if (remoteMove.getFrom() != 0 && remoteMove.getTo() != 0) {
                                    remoteMoveListener.onRemoteMoveMade(remoteMove);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private boolean isMatchInterrupted(RemoteMove remoteMove){
        return remoteMove.getEvents() != null && remoteMove.getEvents().contains(MatchEvent.INTERRUPTED.toString());
    }

    private boolean isMatchStarted(RemoteMove remoteMove){
        return remoteMove.getEvents() != null && remoteMove.getEvents().contains(MatchEvent.IN_PROGRESS.toString());
    }

    // TODO Remove redundant code blocks
    private void getMatchableAccountOnEloRating(String uid, MatchType matchType, MatchRange matchRange, Callback  callback){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(null)
                .build();

        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(Constants.CLOUD_FUNCTIONS_URL.concat(Constants.GET_MATCH_PATH_ON_ELO_RATING))).newBuilder();
        String url;
        if(matchRange != null){
             url = builder.addQueryParameter("match_type", matchType.toString())
                    .addQueryParameter("uid", uid)
                    .addQueryParameter("start_at", Integer.toString(matchRange.getStartAt()))
                    .addQueryParameter("end_at", Integer.toString(matchRange.getEndAt()))
                    .build().toString();
        }
        else{
             url = builder.addQueryParameter("match_type", matchType.toString())
                    .addQueryParameter("uid", uid)
                    .build().toString();
        }


        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("match_type", matchType.toString())
                .addFormDataPart("uid",uid)
                .build();

        Request request = new Request.Builder()
                              .url(url)
                              .method("POST",requestBody)
                              .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    private void createUserMatchableAccount(String uid, MatchType matchType, Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                        .cache(null)
                                        .build();

        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(Constants.CLOUD_FUNCTIONS_URL.concat(Constants.CREATE_USER_MATCHABLE_ACCOUNT))).newBuilder();
        String url = builder.addQueryParameter("match_type", matchType.toString())
                .addQueryParameter("uid", uid)
                .build().toString();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("match_type", matchType.toString())
                .addFormDataPart("uid",uid)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .method("POST",requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    private void getOnMatchableAccountImplementation(String uid, MatchType matchType, MatchRange matchRange){
        getMatchableAccountOnEloRating(uid, matchType, matchRange, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                matchListener.onMatchError();
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        switch (response.code()) {
                            case 200 :
                                matchListener.onMatchCreatedNotification();
                                break;
                            case 404 :
                                matchListener.onMatchError();
                                break;
                            case 403:
                                matchListener.onMatchError();
                                break;
                            default:
                                matchListener.onMatchError();
                        }
                    } catch (Exception e) {
                        matchListener.onMatchError();
                    }
            }
        });
    }

    public void createUseAccountImplementation(String uid, MatchType matchType, MatchRange matchRange){
        createUserMatchableAccount(uid, matchType, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                matchListener.onMatchError();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response){
                // Match User after creating a matchable
                getOnMatchableAccountImplementation(uid,matchType,matchRange);
            }
        });
    }
}
