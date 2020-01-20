package chessbet.api;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import chessbet.domain.Constants;
import chessbet.domain.FCMMessage;
import chessbet.domain.User;
import chessbet.utils.TokenGenerator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationAPI {
    private static NotificationAPI INSTANCE = new NotificationAPI();

    public static NotificationAPI get(){
        return INSTANCE;
    }

    public void getNotificationToken(final TokenRetrieved tokenRetrieved){
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null){
                tokenRetrieved.onNotificationTokenReceived(task.getResult().getToken());
            } else {
                tokenRetrieved.onNotificationTokenErrorReceived(task.getException());
            }
        });
    }

    public void updateUserToken(final String token, User user){
        AccountAPI.get().getAUser(user.getUid(), user1 -> {
            user1.setFcmToken(token);
            AccountAPI.get().updateUser(user1, new AccountAPI.UserUpdated() {
                @Override
                public void onUserUpdate() {
                    Log.d(NotificationAPI.class.getSimpleName(), "Token Updated " + token);
                }

                @Override
                public void onUserUpdateFail(Exception e) {
                    Crashlytics.logException(e);
                }
            });
        });
    }

    private void sendFCMMessage(FCMMessage message, Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(null)
                .build();

        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(Constants.CLOUD_FUNCTIONS_URL
                .concat(Constants.SEND_FCM_MESSAGE_TO_USERS)))
                .newBuilder();

        String url = builder.build().toString();

        RequestBody requestBody = RequestBody.create(Constants.JSON, new Gson().toJson(message));

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


    void sendFCMMessageImplementation(FCMMessage message){
        sendFCMMessage(message, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Crashlytics.logException(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code() == Constants.RESPONSE_OKAY_FLAG){
                    Log.d(NotificationAPI.class.getSimpleName(), " Message sent to devices");
                }
            }
        });
    }


    public interface TokenRetrieved {
        void onNotificationTokenReceived(String token);
        void onNotificationTokenErrorReceived(Exception e);
    }
}
