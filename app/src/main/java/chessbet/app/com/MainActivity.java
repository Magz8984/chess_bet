package chessbet.app.com;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;


import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.adapter.MenuOptionsAdapter;
import chessbet.services.MatchService;

public class MainActivity extends AppCompatActivity{
    @BindView(R.id.menuItems) GridView menuItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        menuItems.setAdapter(new MenuOptionsAdapter(this));
    }

    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = new Intent(this, MatchService.class);
        startService(intent);
    }

    @Override
    protected void onStop(){
        super.onStop();
//        Intent intent = new Intent(this,MatchService.class);
//        stopService(intent);
    }


}
