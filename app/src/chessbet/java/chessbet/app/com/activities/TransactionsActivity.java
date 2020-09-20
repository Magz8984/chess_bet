package chessbet.app.com.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.app.com.adapter.TransactionsAdapter;
import chessbet.api.AccountAPI;
import chessbet.api.PaymentsAPI;
import chessbet.app.com.R;
import chessbet.domain.Transaction;
import es.dmoral.toasty.Toasty;

public class TransactionsActivity extends AppCompatActivity implements View.OnClickListener, PaymentsAPI.TransactionsReceived {
    @BindView(R.id.btnWithdraw) Button btnWithdraw;
    @BindView(R.id.recAllTransactions) RecyclerView recAllTransactions;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        ButterKnife.bind(this);
        btnWithdraw.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recAllTransactions.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        String phoneNumber = Objects.requireNonNull(AccountAPI.get().getFirebaseUser().getPhoneNumber()).replace("+", "");
        PaymentsAPI.get().getTransactionsImplementation(phoneNumber, this);
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

    @Override
    public void onTransactionsReceived(List<Transaction> transactions) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            if(transactions.isEmpty()) {
                Toasty.info(this, "No transactions available", Toasty.LENGTH_LONG).show();
            }
            TransactionsAdapter transactionsAdapter = new TransactionsAdapter(this, transactions);
            // Get transactions
            recAllTransactions.setAdapter(transactionsAdapter);
        });
    }

    @Override
    public void onTransactionsReceivedFailure() {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            Toasty.error(this, "Error occurred while fetching transactions",Toasty.LENGTH_LONG).show();
        });
    }
}
