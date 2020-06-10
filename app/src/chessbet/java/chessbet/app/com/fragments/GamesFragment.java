package chessbet.app.com.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.app.com.DepositActivity;
import chessbet.app.com.R;

public class GamesFragment extends Fragment implements View.OnClickListener{
    @BindView(R.id.deposit) Button deposit;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_games, container, false);
        ButterKnife.bind(this, root);
        deposit.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
       if (v==deposit){
           gotoDepositActivity();
       }
    }

    private void gotoDepositActivity() {
        startActivity(new Intent(new Intent(getActivity(), DepositActivity.class)));
    }
}
