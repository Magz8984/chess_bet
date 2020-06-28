package chessbet.app.com.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Locale;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;

import chessbet.adapter.MatchesAdapter;
import chessbet.api.AccountAPI;
import chessbet.api.PaymentsAPI;

import chessbet.app.com.DepositActivity;

import chessbet.app.com.R;
import chessbet.app.com.TransactionsActivity;
import chessbet.domain.Account;
import chessbet.domain.MatchType;
import chessbet.domain.PaymentAccount;
import chessbet.utils.EventBroadcast;
import es.dmoral.toasty.Toasty;

public class GamesFragment extends Fragment implements View.OnClickListener, PaymentsAPI.PaymentAccountReceived, EventBroadcast.AccountUpdated ,
SwipeRefreshLayout.OnRefreshListener{
    @BindView(R.id.btnDeposit) Button btnDeposit;
    @BindView(R.id.btnTransactions) Button btnTransactions;
    @BindView(R.id.txtBalance) TextView txtBalance;
    @BindView(R.id.recBetGames) RecyclerView recBetGames;
    @BindView(R.id.swiperefresh_layout) SwipeRefreshLayout swiperefresh_layout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_games, container, false);
        ButterKnife.bind(this, root);
        btnDeposit.setOnClickListener(this);
        btnTransactions.setOnClickListener(this);
        swiperefresh_layout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        PaymentsAPI.get().addPaymentAccountListener(this);
        EventBroadcast.get().addAccountUpdated(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recBetGames.setLayoutManager(layoutManager);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        PaymentAccount paymentAccount = PaymentsAPI.get().getCurrentAccount();
        if(paymentAccount != null) {
            this.txtBalance.setText(String.format(Locale.ENGLISH,"%s %.2f", "USD", paymentAccount.getBalance()));
        }
        // Attempt to fetch current user account
        Account account = AccountAPI.get().getCurrentAccount();
        if(account != null) {
            this.onAccountUpdated(account);
        }
    }

    @Override
    public void onClick(View v) {
       if (v.equals(btnDeposit)){
           gotoDepositActivity();
       } else if (v.equals(btnTransactions)){
            gotoTransactionsActivity();
       }
    }

    private void gotoTransactionsActivity() {
        startActivity(new Intent(new Intent(getActivity(), TransactionsActivity.class)));
    }

    private void gotoDepositActivity() {
        startActivity(new Intent(new Intent(getActivity(), DepositActivity.class)));
    }

    @Override
    public void onPaymentAccountReceived(PaymentAccount account) {
        try {
            requireActivity().runOnUiThread(() -> this.txtBalance.setText(String.format(Locale.ENGLISH,"%s %.2f", "USD", account.getBalance())));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onPaymentAccountFailure() {
        try {
            requireActivity().runOnUiThread(() -> Toasty.error(requireContext(), "Error occurred while fetching payments account" ,Toasty.LENGTH_LONG).show());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onAccountUpdated(Account account) {
        try {
            if(swiperefresh_layout.isRefreshing()) {
                swiperefresh_layout.setRefreshing(false);
            }
            recBetGames.setAdapter(new MatchesAdapter(requireContext(), MatchType.BET_ONLINE));
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        // Fetch Payment Account
        PaymentsAPI.get().getPaymentAccountImplementation(AccountAPI.get().getFirebaseUser().getUid());
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(swiperefresh_layout.isRefreshing()) {
                swiperefresh_layout.setRefreshing(false);
               }
            }
        }, 5000);
    }
}
