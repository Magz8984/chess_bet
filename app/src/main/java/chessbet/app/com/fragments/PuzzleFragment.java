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

import chessbet.adapter.PuzzlesAdapter;
import chessbet.app.com.R;
import chessbet.domain.Puzzle;

public class PuzzleFragment extends Fragment {
    private PuzzlesAdapter puzzlesAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_puzzels, container, false);

        Query query = FirebaseFirestore.getInstance().collection("puzzles").orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Puzzle> options = new FirestoreRecyclerOptions.Builder<Puzzle>()
                .setQuery(query, Puzzle.class)
                .build();

        this.puzzlesAdapter = new PuzzlesAdapter(options, getContext());
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(this.puzzlesAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.puzzlesAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.puzzlesAdapter.stopListening();
    }
}