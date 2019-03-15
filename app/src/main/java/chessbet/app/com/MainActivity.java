package chessbet.app.com;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


//    BUTTONS
    //SettingsButton
    private Button mSettingsButton;
    //Accounts Settings
    private Button mAccountsSettings;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Settings Button
        mSettingsButton = (Button) findViewById(R.id.BtnSettings);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

    }

    public void openAccountsSettings(){
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    public void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
