package chessbet.app.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import chessbet.api.AccountAPI;
import chessbet.api.PaymentsAPI;
import chessbet.domain.Account;
import chessbet.domain.PaymentAccount;
import chessbet.domain.User;
import chessbet.services.AccountListener;
import chessbet.utils.EventBroadcast;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements AccountListener, EventBroadcast.AccountUserUpdate, PaymentsAPI.PaymentAccountReceived {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView txtPhoneNumber;
    private TextView txtRating;
    private TextView txtBalance;
    private CircleImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        this.txtPhoneNumber = navigationView.getHeaderView(0).findViewById(R.id.txtPhoneNumber);
        this.txtRating = navigationView.getHeaderView(0).findViewById(R.id.txtRating);
        this.txtBalance = navigationView.getHeaderView(0).findViewById(R.id.txtBalance);
        this.imgProfile = navigationView.getHeaderView(0).findViewById(R.id.imgProfile);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        PaymentsAPI.get().addPaymentAccountListener(this);
        AccountAPI.get().setAccountListener(this);
        EventBroadcast.get().addAccountUserUpdated(this);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_games,R.id.nav_profile, R.id.nav_playOnline, R.id.nav_terms_conditions)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AccountAPI.get().getAccount();
        AccountAPI.get().getUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (item.getItemId() == R.id.action_logout) {
            auth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            // Close current activity
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onAccountReceived(Account account) {
        txtRating.setText(String.format(Locale.ENGLISH,"%d", account.getElo_rating()));
        EventBroadcast.get().broadCastAccountUpdate();
    }

    @Override
    public void onUserReceived(User user) {
        try {
            txtPhoneNumber.setText(AccountAPI.get().getFirebaseUser().getPhoneNumber());
            PaymentsAPI.get().getPaymentAccountImplementation(user.getUid());
            if(user.getProfile_photo_url() != null) {
                Glide.with(this).load(user.getProfile_photo_url()).into(imgProfile);
            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
        }
    }

    @Override
    public void onAccountUpdated(boolean status) { }

    @Override
    public void onAccountUserUpdate(User user) {
        try {
            if(user.getProfile_photo_url() != null) {
                Glide.with(this).load(user.getProfile_photo_url()).into(imgProfile);
            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
        }
    }

    @Override
    public void onPaymentAccountReceived(PaymentAccount account) {
        runOnUiThread(() -> this.txtBalance.setText(String.format(Locale.ENGLISH,"%s %.2f", "USD", account.getBalance())));
    }

    @Override
    public void onPaymentAccountFailure() {
        runOnUiThread(() -> Toasty.error(this, "Error occurred while fetching payments account",Toasty.LENGTH_LONG).show());
    }
}
