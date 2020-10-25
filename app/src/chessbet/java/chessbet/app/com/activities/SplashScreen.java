package chessbet.app.com.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import chessbet.app.com.R;
import chessbet.services.MessagingService;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            if(!MessagingService.startExternalIntentFromFCM(getIntent().getExtras(), this)){
                this.navigateToOnBoardingActivity();
            }
        } else {
            this.navigateToOnBoardingActivity();
        }
    }

    private void navigateToOnBoardingActivity() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), OnBoardingActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}
