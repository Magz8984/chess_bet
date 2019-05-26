package chessbet.app.com;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;


import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.adapter.com.MenuOptionsAdapter;

public class MainActivity extends AppCompatActivity{
    @BindView(R.id.menuItems) GridView menuItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        menuItems.setAdapter(new MenuOptionsAdapter(this));
    }
}
