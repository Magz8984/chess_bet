package chessbet.app.com;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;


import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.adapter.MenuOptionsAdapter;

public class MainActivity extends AppCompatActivity{
    @BindView(R.id.menuItems) GridView menuItems;
    @BindView(R.id.toolBar) Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        menuItems.setAdapter(new MenuOptionsAdapter(this));
        setSupportActionBar(toolbar);
        LayoutInflater toolBarInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(toolBarInflater != null && getSupportActionBar() != null){
            View view = toolBarInflater.inflate(R.layout.toolbar_view,null);
            setSupportActionBar(view);
        }

    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }


}
