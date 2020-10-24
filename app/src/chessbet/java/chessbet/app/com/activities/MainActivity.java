package chessbet.app.com.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;
import java.util.Objects;

import chessbet.api.AccountAPI;
import chessbet.api.NotificationAPI;
import chessbet.api.PaymentsAPI;
import chessbet.app.com.R;
import chessbet.app.com.fragments.GamesFragment;
import chessbet.domain.Account;
import chessbet.domain.Constants;
import chessbet.domain.FCMMessage;
import chessbet.domain.PaymentAccount;
import chessbet.domain.User;
import chessbet.services.AccountListener;
import chessbet.utils.EventBroadcast;
import chessbet.utils.Util;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements AccountListener,
        EventBroadcast.AccountUserUpdate, PaymentsAPI.PaymentAccountReceived,
        NavigationView.OnNavigationItemSelectedListener, NotificationAPI.TokenRetrieved {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView txtPhoneNumber;
    private TextView txtRating;
    private TextView txtBalance;
    private CircleImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDrawerLayout().openDrawer(GravityCompat.START);
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        this.txtPhoneNumber = navigationView.getHeaderView(0).findViewById(R.id.txtPhoneNumber);
        this.txtRating = navigationView.getHeaderView(0).findViewById(R.id.txtRating);
        this.txtBalance = navigationView.getHeaderView(0).findViewById(R.id.txtBalance);
        this.imgProfile = navigationView.getHeaderView(0).findViewById(R.id.imgProfile);


        PaymentsAPI.get().addPaymentAccountListener(this);
        AccountAPI.get().setAccountListener(this);
        EventBroadcast.get().addAccountUserUpdated(this);
        navigationView.setNavigationItemSelectedListener(this);

        GamesFragment gamesFragment = new GamesFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.frag_container, gamesFragment,"");
        ft1.commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        AccountAPI.get().getAccount();
        AccountAPI.get().getUser();
        onConfigurationMessage();
        NotificationAPI.get().getNotificationToken(this);
    }

    /**
     * Navigates to appropriate fragment on new challenge notification
     */
    private void onConfigurationMessage() {
        String messageType = getIntent().getStringExtra(Constants.MESSAGE_TYPE);
        if(messageType != null) {
            if(messageType.equals(FCMMessage.FCMMessageType.NEW_CHALLENGE.toString())) {
                // New Challenges fragment

            }
        }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.nav_games:
                drawer.closeDrawers();
                Util.switchContent(R.id.frag_container,
                        Util.GAMES_FRAGMENT,
                        MainActivity.this,
                        Util.AnimationType.SLIDE_LEFT);
                return true;
            case R.id.nav_profile:
                drawer.closeDrawers();
                Util.switchContent(R.id.frag_container,
                        Util.PROFILE_FRAGMENT,
                        MainActivity.this,
                        Util.AnimationType.SLIDE_LEFT);
                return true;
            case R.id.nav_playOnline:
                drawer.closeDrawers();
                Util.switchContent(R.id.frag_container,
                        Util.PLAY_ONLINE_FRAGMENT,
                        MainActivity.this,
                        Util.AnimationType.SLIDE_LEFT);
                return true;
            case R.id.nav_terms_conditions:
                drawer.closeDrawers();
                Util.switchContent(R.id.frag_container,
                        Util.TERMS_FRAGMENT,
                        MainActivity.this,
                        Util.AnimationType.SLIDE_LEFT);
                return true;
            case R.id.nav_logout:
                drawer.closeDrawers();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
        }
        return false;
    }

    public DrawerLayout getDrawerLayout() {
        return drawer;
    }

    @Override
    public void onNotificationTokenReceived(String token) {
        User user = AccountAPI.get().getCurrentUser();
        if(!user.getFcmToken().equals(token)){
            NotificationAPI.get().updateUserToken(token, user);
        }
    }

    @Override
    public void onNotificationTokenErrorReceived(Exception e) {
        Toasty.error(this, Objects.requireNonNull(e.getMessage()), Toasty.LENGTH_LONG).show();
    }
}
