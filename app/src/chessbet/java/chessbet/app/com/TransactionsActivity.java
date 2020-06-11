package chessbet.app.com;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionsActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.btnWithdraw) Button btnWithdraw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        ButterKnife.bind(this);
        btnWithdraw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnWithdraw)){
            gotoWithdrawActivity();
        }
    }

    private void gotoWithdrawActivity() {
        startActivity(new Intent(new Intent(this, WithdrawActivity.class)));
    }
}
