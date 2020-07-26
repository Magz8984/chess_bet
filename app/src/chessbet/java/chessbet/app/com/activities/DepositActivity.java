package chessbet.app.com.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.api.AccountAPI;
import chessbet.api.PaymentsAPI;
import chessbet.app.com.R;
import chessbet.domain.MPESASavingDTO;
import es.dmoral.toasty.Toasty;

public class DepositActivity extends AppCompatActivity implements View.OnClickListener, PaymentsAPI.SavingsRequestReceived {
    @BindView(R.id.txtAmount) EditText txtAmount;
    @BindView(R.id.btnDeposit) Button btnDeposit;

    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);
        ButterKnife.bind(this);
        btnDeposit.setOnClickListener(this);
        loader = new ProgressDialog(this);
        loader.setMessage("Please wait while we initiate your request ...");
    }

    @Override
    public void onClick(View view) {
        if(view.equals(btnDeposit)) {
            try {
                final String phoneNumber = Objects.requireNonNull(AccountAPI.get().getFirebaseUser().getPhoneNumber()).replace("+", "");
                final long amount = Long.parseLong(txtAmount.getText().toString());
                final MPESASavingDTO savingDTO = new MPESASavingDTO(phoneNumber, Long.toString(amount));
                loader.show();
                PaymentsAPI.get().initiateDarajaSavingsImplementation(savingDTO, this);
            } catch (NumberFormatException ex) {
                loader.dismiss();
                runOnUiThread(() -> Toasty.error(this, "Amount Is Not A Number", Toasty.LENGTH_LONG).show());
            }
            catch (Exception ex) {
                loader.dismiss();
                runOnUiThread(() -> Toasty.error(this, "An error occurred", Toasty.LENGTH_LONG).show());
                Crashlytics.logException(ex);
            }
        }
    }

    @Override
    public void onSavingsRequestReceived() {
        loader.dismiss();
        runOnUiThread(() -> Toasty.success(this, "Payment Request Sent Successfully", Toasty.LENGTH_LONG).show());
    }

    @Override
    public void onSavingsRequestError() {
        loader.dismiss();
        runOnUiThread(() -> Toasty.error(this, "Payment request was unsuccessful", Toasty.LENGTH_LONG).show());
    }
}
