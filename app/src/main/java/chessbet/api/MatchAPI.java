package chessbet.api;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import chessbet.domain.Constants;
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
    private DatabaseReference  databaseReference;
    private MatchListener matchListener;
    private RemoteMoveListener remoteMoveListener;
    public MatchAPI(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void setMatchListener(MatchListener matchListener) {
        this.matchListener = matchListener;
    }

    public void setRemoteMoveListener(RemoteMoveListener remoteMoveListener) {
        this.remoteMoveListener = remoteMoveListener;
    }

    public void getAccount(){
        final MatchableAccount[] matchable = {null};
        databaseReference.child(DatabaseUtil.matchables).child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                matchable[0] = dataSnapshot.getValue(MatchableAccount.class);
                MatchableAccount matchableAccount = matchable[0];
                if(matchableAccount != null){
                    if(matchableAccount.isMatched()){
                        matchListener.onMatch(matchableAccount);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void sendMoveData(MatchableAccount matchableAccount,int source, int destination){
        RemoteMove remoteMove = new RemoteMove();
        remoteMove.setOwner(matchableAccount.getOwner());
        remoteMove.setFrom(source);
        remoteMove.setTo(destination);
        databaseReference.child(DatabaseUtil.matches).child(matchableAccount.getMatchId())
                .child("players")
                .child(matchableAccount.getSelf())
                .setValue(remoteMove).addOnSuccessListener(aVoid -> {

                });
    }

    public void getRemoteMoveData(MatchableAccount matchableAccount){
        databaseReference.child(DatabaseUtil.matches).child(matchableAccount.getMatchId())
                .child("players")
                .child(matchableAccount.getOpponent())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        RemoteMove remoteMove = dataSnapshot.getValue(RemoteMove.class);
                        if(remoteMove.getFrom() !=0 && remoteMove.getTo()!=0){
                            remoteMoveListener.onRemoteMoveMade(remoteMove);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getMatchableAccountOnEloRating(String uid, MatchType matchType, Callback  callback){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(null)
                .build();

        HttpUrl.Builder builder = HttpUrl.parse(Constants.CLOUD_FUNCTIONS_URL.concat(Constants.GET_MATCH_PATH_ON_ELO_RATING)).newBuilder();
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

    public void getOnMatchNotificationOnEloRating(String uid, MatchType matchType){
        getMatchableAccountOnEloRating(uid, matchType, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                matchListener.onMatchError();
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                Log.d("Data",response.body().toString());

                try {
                    switch (response.code()) {
                        case 200 :
                            matchListener.onMatch(null);
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            MatchableAccount  matchableAccount = new MatchableAccount();
                            matchableAccount.setElo_rating(jsonObject.getInt("elo_rating"));
                            matchableAccount.setMatch_type(jsonObject.getString("match_type"));
                            matchableAccount.setMatchable(jsonObject.getBoolean("matchable"));
                            matchableAccount.setMatchable(jsonObject.getBoolean("matched"));
                            Log.d("Data",matchableAccount.getMatch_type());
                            break;
                        case 404 :
                            matchListener.onMatchError();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}