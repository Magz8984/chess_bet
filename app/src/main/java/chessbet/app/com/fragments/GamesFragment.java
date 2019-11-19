package chessbet.app.com.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import chessbet.adapter.GamesAdapter;
import chessbet.app.com.R;

public class GamesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games, container,false);
        RecyclerView recyclerView = view.findViewById(R.id.recGames);
        GamesAdapter gamesAdapter = new GamesAdapter(loadGamesFromPGN(),getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(gamesAdapter);
        return view;
    }
    private List<File> loadGamesFromPGN() {
        String path = Objects.requireNonNull(getContext()).getFilesDir().toString();
        Log.d("Files" ,"Path : " + path);
        File directory = new File(path);
        List<File> files  = Arrays.asList(directory.listFiles());
        // TODO Find a better way to work this out
        List<File> mutatedList = new ArrayList<>(files);

        for(Iterator<File> iterator = mutatedList.iterator(); iterator.hasNext();){
            File file = iterator.next();
            if (!file.getName().contains(".pgn")) {
                iterator.remove();
            }
        }
        // Recent games to oldest game
        Collections.reverse(mutatedList);
        return mutatedList;
    }
}
