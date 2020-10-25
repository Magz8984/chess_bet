package chessbet.app.com.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.app.com.R;
import chessbet.app.com.adapter.NewChallengesAdapter;
import chessbet.domain.Challenge;

public class NewChallengeFragment extends Fragment {
    @BindView(R.id.rec_new_challenges) RecyclerView recNewChallenges;

    private NewChallengesAdapter newChallengesAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_challenges, container, false);
        ButterKnife.bind(this, root);

        recNewChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
        recNewChallenges.setHasFixedSize(true);
        newChallengesAdapter = new NewChallengesAdapter(this.createOptions(),requireContext());
        recNewChallenges.setAdapter(newChallengesAdapter);
        return root;
    }

    private FirestoreRecyclerOptions<Challenge> createOptions() {
        Query query = FirebaseFirestore.getInstance().collection("challenges")
//                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .whereEqualTo("accepted", false)
                .limit(50);
        return new FirestoreRecyclerOptions.Builder<Challenge>()
                .setQuery(query, Challenge.class)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        newChallengesAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        newChallengesAdapter.stopListening();
    }
}
