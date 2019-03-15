package chessbet.app.com;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    //Accounts Settings
    private Button mAccountsSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Accounts Settings
        mAccountsSettings = (Button) findViewById(R.id.BtnAccountSettings);
        mAccountsSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccountsSettings();
            }
        });
    }


    //Accounts Settings Button Start Activity
    public void openAccountsSettings(){
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }
}
