package chessbet.app.com.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.app.com.DepositActivity;
import chessbet.app.com.R;

public class GamesFragment extends Fragment implements View.OnClickListener{
    @BindView(R.id.btnDeposit) Button btnDeposit;
    @BindView(R.id.btnPlay) Button btnPlay;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_games, container, false);
        ButterKnife.bind(this, root);
        btnDeposit.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
       if (v.equals(btnDeposit)){
           gotoDepositActivity();
       }
        if (v.equals(btnPlay)){
            gotoMatchFragment();
        }
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
}
