package chessbet.app.com;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener {
    @BindView(R.id.BtnPlaytwoplayer) Button btnTwoPlayer;
    @BindView(R.id.BtnSettings) Button mSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSettingsButton.setOnClickListener(this);
        btnTwoPlayer.setOnClickListener(this);
    }

    //Settings Button Start Activity
    private void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openBoard(){
        Intent intent=new Intent(this,BoardActivity.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        if(v.equals(mSettingsButton)){
            openSettings();
        }
        if(v.equals(btnTwoPlayer)){
            openBoard();
        }
    }
}
