package chessbet.app.com;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.api.AccountAPI;
import chessbet.api.ChallengeAPI;
import chessbet.api.NotificationAPI;
import chessbet.api.PresenceAPI;
import chessbet.app.com.fragments.GamesFragment;
import chessbet.app.com.fragments.MainFragment;
import chessbet.app.com.fragments.MatchFragment;
import chessbet.app.com.fragments.PlayFriendFragment;
import chessbet.app.com.fragments.ProfileFragment;
import chessbet.app.com.fragments.PuzzleFragment;
import chessbet.app.com.fragments.SettingsFragment;
import chessbet.app.com.fragments.ChallengesFragment;
import chessbet.app.com.fragments.TermsOfService;
import chessbet.domain.Account;
import chessbet.domain.Constants;
import chessbet.domain.FCMMessage;
import chessbet.domain.User;
import chessbet.services.AccountListener;
import chessbet.utils.EventBroadcast;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        AccountListener, View.OnClickListener, EventBroadcast.UserUpdate, EventBroadcast.AccountUpdated ,
        ChallengeAPI.DeleteChallenge, PresenceAPI.UserOnline, NotificationAPI.TokenRetrieved {
    private ProgressDialog uploadProfileDialog;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_viewer) NavigationView navigationView;
    private TextView txtEmail;
    private TextView txtRating;
    private TextView txtUserName;
    private CircleImageView profileImage;
    private static final int REQUEST_CODE = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadProfileDialog = new ProgressDialog(this);
        uploadProfileDialog.setMessage(getResources().getString(R.string.upload_profile_photo));
        uploadProfileDialog.setCancelable(true);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        txtEmail = navigationView.getHeaderView(0).findViewById(R.id.email);
        txtUserName = navigationView.getHeaderView(0).findViewById(R.id.username);
        txtRating = navigationView.getHeaderView(0).findViewById(R.id.rating);
        profileImage = navigationView.getHeaderView(0).findViewById(R.id.profile_photo);
        profileImage.setOnClickListener(this);

        FirebaseFirestore.setLoggingEnabled(true); // Set up logging
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);


        AccountAPI.get().setUser(FirebaseAuth.getInstance().getCurrentUser());
        AccountAPI.get().setAccountListener(this);
        AccountAPI.get().getUser();
        AccountAPI.get().getAccount();

        EventBroadcast.get().addAccountUpdated(this);

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
        navigationView.setCheckedItem(R.id.itm_play_chess);
        // TODO Create private variable
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            txtUserName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }
        EventBroadcast.get().addUserUpdateObserver(this);
        initializeCrashReporter();
        freeUseFromMatch();
        // Initialize Mobile Ads SDK
        MobileAds.initialize(this, BuildConfig.AD_MOB_APP_ID);
//        MobileAds.initialize(this, initializationStatus -> Log.d(MainActivity.class.getSimpleName(), "Mobile Ads SDK Initialized"));
    }

    private void initializeCrashReporter(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Crashlytics.setUserIdentifier(user.getUid());
        }
    }

    /**
     * Called to check for matches created externally
     */
    private void onConfigurationMessage() {
        String messageType = getIntent().getStringExtra(Constants.MESSAGE_TYPE);
        if(messageType != null) {
            if(messageType.equals(FCMMessage.FCMMessageType.TARGET_CHALLENGE.toString())) {
                toolbar.setTitle(getString(R.string.challenges));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChallengesFragment()).commit();
            }
        }
    }

    /**
     * Used as a flag to notify the app if the user is in a match
     */
    private void freeUseFromMatch() {
        getApplicationContext().getSharedPreferences(Constants.IS_ON_MATCH, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(Constants.IS_ON_MATCH, false)
                .apply();
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
        onConfigurationMessage();
    }

    @Override
    protected void onDestroy() {
        PresenceAPI.get().stopListening();
        super.onDestroy();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.itm_play_chess :
                toolbar.setTitle(getString(R.string.app_name));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
                break;
            case R.id.itm_play_online:
                // TODO Remove piece of logic from UI.
                if(AccountAPI.get().getCurrentAccount() != null){
                    toolbar.setTitle(getString(R.string.play_online));
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MatchFragment()).commit();
                }
                break;
            case R.id.itm_profile:
                toolbar.setTitle(getString(R.string.profile));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case R.id.itm_account_settings:
                toolbar.setTitle(getString(R.string.settings));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();

                break;
            case R.id.itm_terms:
                if(AccountAPI.get().getCurrentAccount() != null) {
                    if(!AccountAPI.get().getCurrentAccount().isTermsOfServiceAccepted()){
                        Toast.makeText(this, "Accept Terms", Toast.LENGTH_LONG).show();
                        TermsOfService termsOfService = new TermsOfService();
                        termsOfService.show(getSupportFragmentManager(), "Terms Of Service");
                    } else {
                        Toast.makeText(this, "Terms Of Service Already Accepted", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.games:
                toolbar.setTitle(getString(R.string.games));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GamesFragment()).commit();
                break;
            case R.id.puzzles:
                toolbar.setTitle("Puzzles");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PuzzleFragment()).commit();
                break;
            case R.id.itm_play_friend:
                toolbar.setTitle(getString(R.string.play_friend));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlayFriendFragment()).commit();
                break;
            case R.id.challenges:
                toolbar.setTitle(getString(R.string.challenges));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChallengesFragment()).commit();
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
        NotificationAPI.get().getNotificationToken(this); // Get Instance Token
        if( user != null){
            PresenceAPI.get().setUser(user);
            PresenceAPI.get().getAmOnline(this);
            if(user.getProfile_photo_url() != null){
                try {
                    Glide.with(this).asBitmap().load(user.getProfile_photo_url()).into(profileImage);
                }catch (Exception ex) {
                    Crashlytics.logException(ex);
                }
            }
            txtEmail.setText(user.getEmail());
            EventBroadcast.get().broadcastUserLoaded();
        }
    }

    @Override
    public void onAccountUpdated(boolean status) {
        if(status){
            Toast.makeText(this, "Account Updated", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Account Not Updated", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(profileImage)){
           getPermissions();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(resultCode == RESULT_OK){
                if(requestCode == REQUEST_CODE && data != null){
                    Uri selectedImageUri = data.getData();
                    Glide.with(this).asBitmap().load(selectedImageUri)
                            .listener(new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                    uploadProfilePhoto(resource);
                                    return false;
                                }
                            })
                            .into(profileImage);
                }
            }
        }catch (Exception e){
            Crashlytics.logException(e);
        }
    }

    private void getPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_profile_photo)), REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_profile_photo)), REQUEST_CODE);
            }
        }
    }


    private UploadTask uploadProfilePhotoTask(Bitmap bitmap){
        byte[] bytes = {};
        try {
            uploadProfileDialog.show(); // Shows Dialog
            profileImage.setDrawingCacheEnabled(true);
            profileImage.buildDrawingCache();
//            Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
        }
        catch (Exception ex){
            uploadProfileDialog.dismiss();
            ex.printStackTrace();
        }
        return storageReference.putBytes(bytes);
    }

    private void uploadProfilePhoto(Bitmap bitmap){
        storageReference = firebaseStorage.getReference(FirebaseAuth.getInstance().getUid() + "/" + "profile_photo");
        uploadProfilePhotoTask(bitmap).addOnFailureListener(e -> {
            uploadProfileDialog.dismiss();
            Crashlytics.logException(e);
        });

        uploadProfilePhotoTask(bitmap).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {
            final Uri uri = task.getResult();
            Map<String,Object> map = new HashMap<>();
            assert uri != null;
            map.put("profile_photo_url", uri.toString());
            AccountAPI.get().getUserPath().update(map).addOnCompleteListener(task1 -> {
                Toast.makeText(MainActivity.this,R.string.upload_profile_photo,Toast.LENGTH_LONG).show();
                uploadProfileDialog.dismiss();
                AccountAPI.get().getUser();
            }).addOnCanceledListener(() -> uploadProfileDialog.dismiss());
        }));
    }

    @Override
    public void onFirebaseUserUpdate(FirebaseUser user) {
        txtUserName.setText(user.getDisplayName());
        txtEmail.setText(user.getEmail());
    }

    @Override
    public void onAccountUpdated(final Account account) {
        runOnUiThread(() -> txtRating.setText(getResources().getString(R.string.rating, account.getElo_rating())));
    }

    @Override
    public void onChallengeDeleted() {
        Log.d(MainActivity.class.getSimpleName(), "All challenges deleted");
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
        Crashlytics.logException(e);
    }

    /**
     * Checking user is online
     * @param user the user being listened to
     * @param isOnline boolean value
     */
    @Override
    public void onUserOnline(User user, boolean isOnline) {
        Log.d(MainActivity.class.getSimpleName(), "User Online " + isOnline);
    }
}

