package chessbet.app.com;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class WithdrawActivity extends AppCompatActivity implements TextWatcher {
    @BindView(R.id.editTextAmount) EditText editTextAmount;
    @BindView(R.id.txtFee) TextView txtFee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        ButterKnife.bind(this);
        editTextAmount.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(!TextUtils.isEmpty(s.toString())) {
            double amount = Double.parseDouble(s.toString());
            if (amount < 10) txtFee.setText(getText(R.string.withdrawal_fee));
            else if (amount < 1000) txtFee.setText(getText(R.string.withdrawal_fee_15));
            else txtFee.setText(getText(R.string.withdrawal_fee_22));
        }
    }
}
