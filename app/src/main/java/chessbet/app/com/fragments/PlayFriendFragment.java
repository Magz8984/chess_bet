package chessbet.app.com.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import chessbet.adapter.SearchFriendAdapter;
import chessbet.api.AccountAPI;
import chessbet.api.ChallengeAPI;
import chessbet.app.com.R;
import chessbet.domain.User;
import chessbet.utils.Util;

import static chessbet.Application.getContext;

public class PlayFriendFragment extends Fragment implements AccountAPI.UsersReceived, View.OnClickListener, ChallengeAPI.TargetedChallengeUpdated {
    private TextView txtUserName;
    private RecyclerView recUsers;
    private SearchFriendAdapter searchFriendAdapter;
    private ImageView imgSearch;
    private ProgressBar progressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_friend, container, false);
        txtUserName = view.findViewById(R.id.txtUserName);
        recUsers = view.findViewById(R.id.recUsers);
        imgSearch = view.findViewById(R.id.imgSearch);
        imgSearch.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progress_bar);

        txtUserName.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_SEARCH){
                searchUsers();
                return true;

            }
            return false;
        });

        return view;
    }

    private void searchUsers(){
        if(Util.textViewHasText(txtUserName)){
            progressBar.setVisibility(View.VISIBLE);
            AccountAPI.get().getUsersByUserName(txtUserName.getText().toString().trim());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        AccountAPI.get().setUsersRecived(this);
        searchFriendAdapter = new SearchFriendAdapter(getContext(), this);
        recUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        recUsers.setHasFixedSize(true);
        recUsers.setAdapter(searchFriendAdapter);
    }

    @Override
    public void onUserReceived(List<User> users) {
        progressBar.setVisibility(View.GONE);
        searchFriendAdapter.setUsers(users);
    }

    @Override
    public void onUserNotFound() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(), R.string.user_not_found, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        if(view.equals(imgSearch)){
            searchUsers();
        }
    }

    @Override
    public void onChallengeAccepted() {
        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Challenge Accepted", Toast.LENGTH_LONG).show());
    }

    @Override
    public void onChallengeSent() {
        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Challenge Sent", Toast.LENGTH_LONG).show());

    }

    @Override
    public void onUpdateError() {
        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "An error was encountered", Toast.LENGTH_LONG).show());
    }
}
