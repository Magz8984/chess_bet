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
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import chessbet.adapter.MatchesAdapter;
import chessbet.api.AccountAPI;
import chessbet.api.PaymentsAPI;

import chessbet.app.com.activities.DepositActivity;

import chessbet.app.com.R;
import chessbet.app.com.activities.MainActivity;
import chessbet.app.com.activities.TransactionsActivity;
import chessbet.domain.Account;
import chessbet.domain.Match;
import chessbet.domain.MatchType;
import chessbet.domain.PaymentAccount;
import chessbet.utils.DatabaseUtil;
import chessbet.utils.EventBroadcast;
import chessbet.utils.SQLDatabaseHelper;
import chessbet.utils.Util;
import es.dmoral.toasty.Toasty;

public class GamesFragment extends Fragment implements View.OnClickListener, PaymentsAPI.PaymentAccountReceived,
        EventBroadcast.AccountUpdated , SwipeRefreshLayout.OnRefreshListener{
    @BindView(R.id.btnDeposit) Button btnDeposit;
    @BindView(R.id.btnPlayOnline) Button btnPlayOnline;
    @BindView(R.id.btnTransactions) Button btnTransactions;
    @BindView(R.id.txtBalance) TextView txtBalance;
    @BindView(R.id.txtNoMatches) TextView txtNoMatches;
    @BindView(R.id.recBetGames) RecyclerView recBetGames;
    @BindView(R.id.swiperefresh_layout) SwipeRefreshLayout swiperefresh_layout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_games, container, false);
        ButterKnife.bind(this, root);
        txtNoMatches.setSelected(true);
        btnDeposit.setOnClickListener(this);
        btnTransactions.setOnClickListener(this);
        btnPlayOnline.setOnClickListener(this);
        swiperefresh_layout.setOnRefreshListener(this);
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
        this.swiperefresh_layout.setRefreshing(true);
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
       }else if (v.equals(btnPlayOnline)){
           Util.switchFragmentWithAnimation(R.id.frag_container,
                   new MatchFragment(),
                   ((MainActivity) (requireContext())),
                   Util.PLAY_ONLINE_FRAGMENT, Util.AnimationType.SLIDE_DOWN);
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
            requireActivity().runOnUiThread(() -> {
                this.txtBalance.setText(String.format(Locale.ENGLISH,"%s %.2f", "USD", account.getBalance()));
                this.swiperefresh_layout.setRefreshing(false);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onPaymentAccountFailure() {
        try {
            requireActivity().runOnUiThread(() -> {
                Toasty.error(requireContext(), "Error occurred while fetching payments account" ,Toasty.LENGTH_LONG).show();
                this.swiperefresh_layout.setRefreshing(true);
            });
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
            // Fetch Database Matches
            List<Match> matches = DatabaseUtil.getMatchesFromLocalDB(new SQLDatabaseHelper(requireContext()).getMatches());

            if(matches.isEmpty()) {
                txtNoMatches.setVisibility(View.VISIBLE);
            } else {
                txtNoMatches.setVisibility(View.GONE);
            }

            recBetGames.setAdapter(new MatchesAdapter(requireContext(), MatchType.BET_ONLINE, matches));

        } catch (Exception ex)  {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        // Fetch Payment Account
        PaymentsAPI.get().getPaymentAccountImplementation(AccountAPI.get().getFirebaseUser().getUid());
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if(swiperefresh_layout.isRefreshing()) {
            swiperefresh_layout.setRefreshing(false);
           }
        }, 5000);
    }
}
