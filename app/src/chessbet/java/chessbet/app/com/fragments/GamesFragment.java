package chessbet.app.com.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import chessbet.api.PaymentsAPI;

import chessbet.app.com.DepositActivity;

import chessbet.app.com.R;
import chessbet.app.com.TransactionsActivity;
import chessbet.domain.PaymentAccount;

public class GamesFragment extends Fragment implements View.OnClickListener, PaymentsAPI.PaymentAccountReceived {
    @BindView(R.id.btnDeposit) Button btnDeposit;
    @BindView(R.id.btnPlay) Button btnPlay;
    @BindView(R.id.btnTransactions) Button btnTransactions;
    @BindView(R.id.txtBalance) TextView txtBalance;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_games, container, false);
        ButterKnife.bind(this, root);
        btnDeposit.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnTransactions.setOnClickListener(this);
        PaymentsAPI.get().addPaymentAccountListener(this);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        PaymentAccount account = PaymentsAPI.get().getCurrentAccount();
        if(account != null) {
            this.txtBalance.setText(String.format(Locale.ENGLISH,"%s %.2f", "USD", account.getBalance()));
        }
    }

    @Override
    public void onClick(View v) {
       if (v.equals(btnDeposit)){
           gotoDepositActivity();
       }
        if (v.equals(btnPlay)){
            gotoMatchFragment();
        }
        if (v.equals(btnTransactions)){
            gotoTransactionsActivity();
        }
    }

    private void gotoTransactionsActivity() {
        startActivity(new Intent(new Intent(getActivity(), TransactionsActivity.class)));
    }

    private void gotoMatchFragment() {
        MatchFragment matchFragment = new MatchFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.nav_host_fragment, matchFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void gotoDepositActivity() {
        startActivity(new Intent(new Intent(getActivity(), DepositActivity.class)));
    }

    @Override
    public void onPaymentAccountReceived(PaymentAccount account) {
        requireActivity().runOnUiThread(() -> this.txtBalance.setText(String.format(Locale.ENGLISH,"%s %.2f", "USD", account.getBalance())));
    }

    @Override
    public void onPaymentAccountFailure() { }
}
