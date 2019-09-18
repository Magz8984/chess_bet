package chessbet.app.com.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import javax.annotation.Nullable;

import chessbet.adapter.MenuOptionsAdapter;
import chessbet.app.com.R;

public class MainFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater , ViewGroup viewGroup, @Nullable Bundle bundle){
        View view = inflater.inflate(R.layout.fragment_main, viewGroup, false);
        GridView gridView = view.findViewById(R.id.menuItems);
        gridView.setAdapter(new MenuOptionsAdapter(getContext()));
        return view;
    }
}
