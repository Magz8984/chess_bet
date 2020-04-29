package chessbet.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import chessbet.domain.Challenge;
import chessbet.domain.ChallengeDTO;
import chessbet.domain.Constants;
import chessbet.domain.MatchRange;
import chessbet.domain.TargetedChallenge;
import chessbet.utils.TokenGenerator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChallengeAPI {
    private static ChallengeAPI INSTANCE = new ChallengeAPI();
    private static String CHALLENGE_COLLECTION = "challenges";
    public static String TARGETED_CHALLENGES_COLLECTION = "targeted_challenges";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private ChallengeHandler challengeHandler;
    private Challenge currentChallenge = null;
    private MatchRange matchRange;
    private boolean isOnChallenge = false;
    private boolean isChallengeAccepted;
    private boolean hasAcceptedChallenge;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private ChallengeAPI(){
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }
    public static ChallengeAPI get() {
        return INSTANCE;
    }

    private int getMinEloRating(){
        return AccountAPI.get().getCurrentAccount().getElo_rating() - matchRange.getStartAt();
    }

    private int getMaxEloRating(){
        return AccountAPI.get().getCurrentAccount().getElo_rating() + matchRange.getEndAt();
    }

    public void setMatchRange(MatchRange matchRange) {
        this.matchRange = matchRange;
    }

    public void setChallengeHandler(ChallengeHandler challengeHandler) {
        this.challengeHandler = challengeHandler;
    }

    public void setHasAcceptedChallenge(boolean hasAcceptedChallenge) {
        this.hasAcceptedChallenge = hasAcceptedChallenge;
    }

    public boolean isChallengeAccepted() {
        return isChallengeAccepted;
    }

    public boolean hasAcceptedChallenge() {
        return hasAcceptedChallenge;
    }


    public void setChallengeAccepted(boolean challengeAccepted) {
        isChallengeAccepted = challengeAccepted;
    }

    public void deleteTargetedChallenge(String challengeId, TargetChallengeUpdated targetChallengeUpdated) {
        db.collection(TARGETED_CHALLENGES_COLLECTION).document(challengeId).delete().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                targetChallengeUpdated.onUpdate();
            } else {
                targetChallengeUpdated.onUpdateError();
            }
        });
    }

    public void getChallenge(String currentChallengeId, ChallengeReceived challengeReceived){
        db.collection(CHALLENGE_COLLECTION).document(currentChallengeId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null){
                Challenge challenge = task.getResult().toObject(Challenge.class); // Challenge From Account
                if(challenge != null && challenge.getOwner()!= null){
                    challengeReceived.onChallengeReceived(challenge);
                }
            }
        });
    }


    public boolean isCurrentChallengeValid (){
        if(currentChallenge == null){
            return false;
        }
        long time =  System.currentTimeMillis();
        return currentChallenge.getTimeStamp() > (time - Constants.MAX_MATCHING_DURATION);
    }

    public interface ChallengeHandler{
        void challengeSent(String id);
        void challengeFound(String id);
        void challengeNotFound();
    }

    public interface ChallengeReceived{
        void onChallengeReceived(Challenge challenge);
    }

    public interface TargetedChallengeUpdated {
        void onChallengeAccepted();
        void onChallengeSent();
        void onUpdateError();
    }

    public interface ChallengeSent{
        void onChallengeSent();
        void onChallengeNotSent();
    }

    public interface DeleteChallenge {
        void onChallengeDeleted();
    }

    public interface TargetChallengeUpdated{
        void onUpdate();
        void  onUpdateError();
    }

    public void setOnChallenge(boolean onChallenge) {
        isOnChallenge = onChallenge;
    }

    public boolean isOnChallenge() {
        return isOnChallenge;
    }

    public FirebaseUser getUser() {
        return user;
    }


    public boolean isChallengeOwner(Challenge challenge){
        return challenge.getOwner().equals(user.getUid());
    }


    private void getSetChallenge(ChallengeDTO challengeDTO, Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(null)
                .build();

        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(Constants.CLOUD_FUNCTIONS_URL.concat(Constants.GET_SET_CHALLENGE))).newBuilder();
        String url = builder.build().toString();

        RequestBody requestBody = RequestBody.create(JSON,new Gson().toJson(challengeDTO));
        // Generate token for cloud functions to verify user
        TokenGenerator.generateToken(token -> {
            Request request = new Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + token)
                    .url(url)
                    .post(requestBody)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(callback);
        });
    }


    private void sendTargetedChallenge(TargetedChallenge challenge, Callback  callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(null)
                .build();

        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(Constants.CLOUD_FUNCTIONS_URL.concat(Constants.SEND_TARGETED_CHALLENGE))).newBuilder();
        String url = builder.build().toString();

        RequestBody requestBody = RequestBody.create(JSON,new Gson().toJson(challenge));
        // Generate token for cloud functions to verify user
        TokenGenerator.generateToken(token -> {
            Request request = new Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + token)
                    .url(url)
                    .post(requestBody)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(callback);
        });
    }

    private void acceptTargetedChallenge(TargetedChallenge challenge, Callback  callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(null)
                .build();

        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(Constants.CLOUD_FUNCTIONS_URL.concat(Constants.ACCEPT_TARGETED_CHALLENGE))).newBuilder();
        String url = builder.build().toString();

        RequestBody requestBody = RequestBody.create(JSON,new Gson().toJson(challenge));
        // Generate token for cloud functions to verify user
        TokenGenerator.generateToken(token -> {
            Request request = new Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + token)
                    .url(url)
                    .post(requestBody)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(callback);
        });
    }

    public void getSetChallengeImplementation(ChallengeDTO challengeDTO){
        this.getSetChallenge(challengeDTO, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                challengeHandler.challengeNotFound();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String responseString = response.body().string();
                challengeHandler.challengeFound(responseString);
            }
        });
    }

    /**
     * Sends Challenge To User and notifies them
     * @param challenge target challenge
     * @param challengeUpdated callback
     */
    public void sendTargetedChallengeImplementation(TargetedChallenge challenge, TargetedChallengeUpdated challengeUpdated){
        this.sendTargetedChallenge(challenge, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                challengeUpdated.onUpdateError();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String responseString = response.body().string();
                Log.d(ChallengeAPI.class.getSimpleName(), responseString);
                challengeUpdated.onChallengeSent();
            }
        });
    }

    /**
     * Accepts Challenge and notifies owner
     * @param challenge target challenge
     * @param challengeUpdated callback
     */
    public void acceptTargetedChallengeImplementation(TargetedChallenge challenge, TargetedChallengeUpdated challengeUpdated){
        this.acceptTargetedChallenge(challenge, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                challengeUpdated.onUpdateError();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String responseString = response.body().string();
                Log.d(ChallengeAPI.class.getSimpleName(), responseString);
                challengeUpdated.onChallengeAccepted();
            }
        });
    }

}
