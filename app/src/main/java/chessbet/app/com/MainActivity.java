package chessbet.app.com;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.app.com.fragments.MainFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
//    @BindView(R.id.menuItems) GridView menuItems;
    @BindView(R.id.toolbar)
Toolbar toolbar;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_viewer) NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
        navigationView.setCheckedItem(R.id.itm_play);
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
    }

    @Override
    protected void onStop(){
        super.onStop();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.itm_play :
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
                break;
            case R.id.itm_profile:
                break;
            case R.id.itm_account_settings:
                Intent intent=new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.itm_terms:
                Toast.makeText(this, "Accept Terms", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }
}

