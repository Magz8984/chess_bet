package chessbet.app.com;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.adapter.OnlineUsersAdapter;
import chessbet.domain.User;
import chessbet.services.ChallengeService;
import chessbet.utils.DatabaseUtil;

public class PresenceActivity extends AppCompatActivity {
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
        onlineUsersAdapter = new OnlineUsersAdapter(options);
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
        startService(new Intent(this, ChallengeService.class)); // Listen to challenges;
    }

    @Override
    protected void onStop() {
        super.onStop();
        onlineUsersAdapter.stopListening();
        stopService(new Intent(this, ChallengeService.class)); // Listen to challenges;
    }
}
