package chessbet.app.com;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.adapter.OnlineUsersAdapter;
import chessbet.api.ChallengeAPI;
import chessbet.domain.User;
import chessbet.utils.DatabaseUtil;

import static chessbet.Application.getContext;

public class PresenceActivity extends AppCompatActivity implements ChallengeAPI.TargetedChallengeUpdated  {
    @BindView(R.id.online_users_toolbar) Toolbar toolbar;
    @BindView(R.id.recOnlineUsers) RecyclerView recOnlineUsers;
    private OnlineUsersAdapter onlineUsersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presence);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Query query = DatabaseUtil.getOnlineUsers();
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        onlineUsersAdapter = new OnlineUsersAdapter(options, this);
        onlineUsersAdapter.setContext(this);
        recOnlineUsers.setLayoutManager(new LinearLayoutManager(this));
        recOnlineUsers.setAdapter(onlineUsersAdapter);
        recOnlineUsers.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.online_users);
        onlineUsersAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        onlineUsersAdapter.stopListening();
    }

    @Override
    public void onChallengeAccepted() {
        runOnUiThread(() -> Toast.makeText(getContext(), "Challenge Accepted", Toast.LENGTH_LONG).show());
    }

    @Override
    public void onChallengeSent() {
        runOnUiThread(() -> Toast.makeText(getContext(), "Challenge Sent", Toast.LENGTH_LONG).show());
    }

    @Override
    public void onUpdateError() {
        runOnUiThread(() -> Toast.makeText(getContext(), "An error was encountered", Toast.LENGTH_LONG).show());
    }
}
