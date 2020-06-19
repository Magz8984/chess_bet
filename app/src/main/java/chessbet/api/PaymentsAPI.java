package chessbet.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import chessbet.domain.Constants;
import chessbet.domain.MPESAPayoutDTO;
import chessbet.domain.MPESASavingDTO;
import chessbet.domain.PaymentAccount;
import chessbet.domain.Transaction;
import chessbet.utils.TokenGenerator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static chessbet.domain.Constants.RESPONSE_OKAY_FLAG;

/**
 * @author Collins Magondu 18/06/2020
 */
public class PaymentsAPI {
    private static PaymentsAPI INSTANCE = new PaymentsAPI();
    private List<PaymentAccountReceived> paymentAccountListeners = new ArrayList<>();
    private PaymentAccount currentAccount;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private PaymentsAPI() { }

    public void addPaymentAccountListener(PaymentAccountReceived paymentAccountListener) {
        this.paymentAccountListeners.add(paymentAccountListener);
    }

    public static PaymentsAPI get() {
        return INSTANCE;
    }

    public PaymentAccount getCurrentAccount() {
        return currentAccount;
    }

    public void broadCastCurrentAccount() {
        for(PaymentAccountReceived paymentAccountListener: paymentAccountListeners) {
            paymentAccountListener.onPaymentAccountReceived(currentAccount);
        }
    }

    public void broadCastCurrentAccountFailure() {
        for(PaymentAccountReceived paymentAccountListener: paymentAccountListeners) {
            paymentAccountListener.onPaymentAccountFailure();
        }
    }


    private void getPaymentAccount(String userId, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(null)
                .build();
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(Constants.CLOUD_FUNCTIONS_URL
                .concat(Constants.GET_PAYMENT_ACCOUNT)))
                .newBuilder()
                .addQueryParameter("userId", userId);

        String url = builder.build().toString();
        // Generate token for cloud functions to verify user
        TokenGenerator.generateToken(token -> {
            Request request = new Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + token)
                    .url(url)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(callback);
        });
    }

    private void getTransactions(String phoneNumber, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(null)
                .build();
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(Constants.CLOUD_FUNCTIONS_URL
                .concat(Constants.GET_TRANSACTIONS)))
                .newBuilder()
                .addPathSegment(phoneNumber);

        String url = builder.build().toString();
        // Generate token for cloud functions to verify user
        TokenGenerator.generateToken(token -> {
            Request request = new Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + token)
                    .url(url)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(callback);
        });
    }

    private void initiateDarajaSavings(MPESASavingDTO mpesaSavingDTO, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(null)
                .build();

        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(Constants.CLOUD_FUNCTIONS_URL.concat(Constants.SAVE_BY_DARAJA))).newBuilder();
        String url = builder.build().toString();

        RequestBody requestBody = RequestBody.create(JSON,new Gson().toJson(mpesaSavingDTO));
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

    private void initiateDarajaPayout(MPESAPayoutDTO payoutDTO, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(null)
                .build();

        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(Constants.CLOUD_FUNCTIONS_URL.concat(Constants.WITHDRAW_BY_DARAJA))).newBuilder();
        String url = builder.build().toString();

        RequestBody requestBody = RequestBody.create(JSON,new Gson().toJson(payoutDTO));
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

    public void initiateDarajaPayoutImplementation(MPESAPayoutDTO mpesaPayoutDTO, PayoutRequestReceived payoutRequestReceived) {
        this.initiateDarajaPayout(mpesaPayoutDTO, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Crashlytics.logException(e);
                payoutRequestReceived.onPayoutRequestError();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                payoutRequestReceived.onPayoutRequestReceived();
            }
        });
    }


    public void initiateDarajaSavingsImplementation(MPESASavingDTO mpesaSavingDTO, SavingsRequestReceived savingsRequestReceived) {
        this.initiateDarajaSavings(mpesaSavingDTO, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Crashlytics.logException(e);
                savingsRequestReceived.onSavingsRequestError();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                savingsRequestReceived.onSavingsRequestReceived();
            }
        });
    }

    public void getTransactionsImplementation(String phoneNumber, TransactionsReceived transactionsReceived) {
        this.getTransactions(phoneNumber, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Crashlytics.logException(e);
                transactionsReceived.onTransactionsReceivedFailure();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                transactionsReceived.onTransactionsReceived(new Gson().fromJson(Objects.requireNonNull(response.body()).string(), new TypeToken<List<Transaction>>(){}.getType()));
            }
        });
    }

    public void getPaymentAccountImplementation(String userId){
        this.getPaymentAccount(userId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Crashlytics.logException(e);
                broadCastCurrentAccountFailure();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    assert response.body() != null;
                    currentAccount = new Gson().fromJson(response.body().string(), PaymentAccount.class);
                    broadCastCurrentAccount();
            }
        });
    }

    public interface PaymentAccountReceived {
        void onPaymentAccountReceived(PaymentAccount account);
        void onPaymentAccountFailure();
    }

    public interface TransactionsReceived {
        void onTransactionsReceived(List<Transaction> transactions);
        void onTransactionsReceivedFailure();
    }

    public interface SavingsRequestReceived {
        void onSavingsRequestReceived();
        void onSavingsRequestError();
    }

    public interface PayoutRequestReceived {
        void onPayoutRequestReceived();
        void onPayoutRequestError();
    }
}
