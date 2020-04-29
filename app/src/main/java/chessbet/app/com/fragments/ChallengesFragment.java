package chessbet.app.com.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import chessbet.adapter.TargetedChallengesAdapter;
import chessbet.api.ChallengeAPI;
import chessbet.api.MatchAPI;
import chessbet.app.com.BoardActivity;
import chessbet.app.com.R;
import chessbet.domain.MatchableAccount;
import chessbet.domain.TargetedChallenge;
import chessbet.services.MatchListener;
import chessbet.utils.DatabaseUtil;

public class ChallengesFragment extends Fragment implements View.OnClickListener, ChallengeAPI.TargetedChallengeUpdated {
    private TargetedChallengesAdapter targetedChallengesAdapter;
    private ProgressDialog progressDialog;
    private Button btnAcceptedChallenges;
    private Button btnMyChallenges;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenges, container,false);
        user = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.recChallenges);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        if(user != null) {
           getTargetedChallenges("target", false);
        }
        progressDialog = new ProgressDialog(getContext());
        btnMyChallenges = view.findViewById(R.id.btnMyChallenges);
        btnAcceptedChallenges = view.findViewById(R.id.btnAcceptedChallenges);
        btnMyChallenges.setOnClickListener(this);
        btnAcceptedChallenges.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(targetedChallengesAdapter != null) {
            Log.d(ChallengesFragment.class.getSimpleName(), "Here");
            targetedChallengesAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(targetedChallengesAdapter != null) {
            targetedChallengesAdapter.stopListening();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.equals(btnAcceptedChallenges)) {
            btnAcceptedChallenges.setBackground(getResources().getDrawable(R.drawable.rounded_selected_button));
            btnMyChallenges.setBackground(getResources().getDrawable(R.drawable.rounded_button));
            if(user != null) {
                getTargetedChallenges("owner", true);
                targetedChallengesAdapter.startListening();
            }
        } else if (view.equals(btnMyChallenges)){
            btnAcceptedChallenges.setBackground(getResources().getDrawable(R.drawable.rounded_button));
            btnMyChallenges.setBackground(getResources().getDrawable(R.drawable.rounded_selected_button));
            if(user != null) {
                getTargetedChallenges("target", false);
                targetedChallengesAdapter.startListening();
            }
        }
    }


    private void getTargetedChallenges(String side, boolean accepted) {
        Log.d(ChallengesFragment.class.getSimpleName(), user.getUid());
        Query query = FirebaseFirestore.getInstance().collection("targeted_challenges")
                .whereEqualTo(side, user.getUid())
                .whereEqualTo("accepted", false)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .limit(30);
        FirestoreRecyclerOptions<TargetedChallenge> options = new FirestoreRecyclerOptions.Builder<TargetedChallenge>()
                .setQuery(query, TargetedChallenge.class)
                .build();
        targetedChallengesAdapter = new TargetedChallengesAdapter(options, getContext(), accepted, this);
        recyclerView.setAdapter(this.targetedChallengesAdapter);
    }

    @Override
    public void onChallengeAccepted() {
        requireActivity().runOnUiThread(() -> {
            progressDialog.setMessage("Setting Up Challenge");
            progressDialog.show();

            MatchAPI.get().setMatchListener(new MatchListener() {
                @Override
                public void onMatchMade(MatchableAccount matchableAccount) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Match Created", Toast.LENGTH_LONG).show();
                    Intent target = new Intent(getContext(), BoardActivity.class);
                    target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(DatabaseUtil.matchables, matchableAccount);
                    target.putExtras(bundle);
                    requireContext().startActivity(target);
                }

                @Override
                public void onMatchableCreatedNotification() {
                }

                @Override
                public void onMatchError() {
                    progressDialog.dismiss();
                }

            });
        });
    }

    @Override
    public void onChallengeSent() {

    }

    @Override
    public void onUpdateError() {
        Toast.makeText(getContext(), "An error occurred while matching", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
    }

}
