package chessbet.app.com;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Accounts Settings
        Button mAccountsSettings = findViewById(R.id.BtnAccountSettings);
        mAccountsSettings.setOnClickListener(v -> openAccountsSettings());
    }

    //Accounts Settings Button Start Activity
    public void openAccountsSettings(){
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }
}
