package chessbet.api;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import chessbet.domain.Constants;
import chessbet.domain.PaymentAccount;
import chessbet.utils.TokenGenerator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

    public void getPaymentAccountImplementation(String userId){
        this.getPaymentAccount(userId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Crashlytics.logException(e);
                broadCastCurrentAccountFailure();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.code() == RESPONSE_OKAY_FLAG){
                    assert response.body() != null;
                    currentAccount = new Gson().fromJson(response.body().string(), PaymentAccount.class);
                    broadCastCurrentAccount();
                } else {
                    broadCastCurrentAccountFailure();
                }
            }
        });
    }
    public interface PaymentAccountReceived {
        void onPaymentAccountReceived(PaymentAccount account);
        void onPaymentAccountFailure();
    }
}
