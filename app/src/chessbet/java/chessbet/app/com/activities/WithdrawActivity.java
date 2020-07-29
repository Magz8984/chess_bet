package chessbet.app.com.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.api.AccountAPI;
import chessbet.api.PaymentsAPI;
import chessbet.app.com.R;
import chessbet.domain.Amount;
import chessbet.domain.MPESAPayoutDTO;
import es.dmoral.toasty.Toasty;

public class WithdrawActivity extends AppCompatActivity implements View.OnClickListener, PaymentsAPI.PayoutRequestReceived {
    @BindView(R.id.txtAmount) EditText txtAmount;
    @BindView(R.id.txtPhoneNumber) EditText txtPhoneNumber;
    @BindView(R.id.btnPayout) Button btnPayout;
    private String phoneNumber;
    private ProgressDialog loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        ButterKnife.bind(this);
        btnPayout.setOnClickListener(this);
        loader = new ProgressDialog(this);
        loader.setMessage("Please wait while we initiate your request ...");
        phoneNumber = Objects.requireNonNull(AccountAPI.get().getFirebaseUser().getPhoneNumber()).replace("+", "");
        txtPhoneNumber.setText(phoneNumber);
    }

    @Override
    public void onClick(View view) {
        if(view.equals(btnPayout)) {
            try {
                final String recipient = txtPhoneNumber.getText().toString();
                // TODO Add Support for other countries
                if(recipient.startsWith("254")) {
                    final long amount = Long.parseLong(txtAmount.getText().toString());
                    Amount payoutAmount = new Amount();
                    payoutAmount.setAmount(amount);
                    payoutAmount.setCurrency("KES");
                    final MPESAPayoutDTO mpesaPayoutDTO = new MPESAPayoutDTO(payoutAmount, phoneNumber, recipient);
                    // Confirm sending of funds
                    Snackbar.make(btnPayout, String.format(Locale.ENGLISH,"Confirm send %s %.2f to %s",
                            payoutAmount.getCurrency(),
                            payoutAmount.getAmount(),
                            phoneNumber),
                            Snackbar.LENGTH_LONG).setAction("Send", v -> {
                                loader.show();
                                PaymentsAPI.get().initiateDarajaPayoutImplementation(mpesaPayoutDTO, this);
                            }).show();

                } else {
                    Toasty.error(this, "Phone number must start with 254", Toasty.LENGTH_LONG).show();
                }
            } catch (NumberFormatException ex) {
                loader.dismiss();
                runOnUiThread(() -> Toasty.error(this, "Amount Is Not A Number", Toasty.LENGTH_LONG).show());
            } catch (Exception ex) {
                loader.dismiss();
                runOnUiThread(() -> Toasty.error(this, "An error occurred", Toasty.LENGTH_LONG).show());
                Crashlytics.logException(ex);
            }
        }
    }

    @Override
    public void onPayoutRequestReceived() {
        loader.dismiss();
        runOnUiThread(() -> Toasty.success(this, "Withdrawal Request Sent Successfully", Toasty.LENGTH_LONG).show());
    }

    @Override
    public void onPayoutRequestError() {
        loader.dismiss();
        runOnUiThread(() -> Toasty.error(this, "Withdrawal request was unsuccessful", Toasty.LENGTH_LONG).show());
    }
}
