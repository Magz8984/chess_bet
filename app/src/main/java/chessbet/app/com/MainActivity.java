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

    //Settings Button Start Activity
    public void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
