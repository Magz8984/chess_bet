package chessbet.app.com;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.api.AccountAPI;
import chessbet.app.com.fragments.MainFragment;
import chessbet.domain.Account;
import chessbet.domain.User;
import chessbet.services.AccountListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AccountListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_viewer) NavigationView navigationView;
    private TextView txtEmail;
    private TextView txtRating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        txtEmail = navigationView.getHeaderView(0).findViewById(R.id.email);
        txtRating = navigationView.getHeaderView(0).findViewById(R.id.rating);
        AccountAPI.get().setUser(FirebaseAuth.getInstance().getCurrentUser());
        AccountAPI.get().getAccount();
        AccountAPI.get().getUser();
        AccountAPI.get().setAccountListener(this);
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
        navigationView.setCheckedItem(R.id.itm_play);
    }

    @Override
    public  void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer( GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.itm_play :
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
                break;
            case R.id.itm_profile:
                break;
            case R.id.itm_account_settings:
                Intent intent=new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.itm_terms:
                Toast.makeText(this, "Accept Terms", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    @Override
    public void onAccountReceived(Account account) {
        txtRating.setText(getResources().getString(R.string.rating, account.getElo_rating()));
    }

    @Override
    public void onUserReceived(User user) {
        txtEmail.setText(user.getEmail());
    }
}

