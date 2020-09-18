package chessbet.app.com.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.api.AccountAPI;
import chessbet.app.com.R;
import chessbet.app.com.adapter.OnBoardingSliderAdapter;
import es.dmoral.toasty.Toasty;

import static android.view.View.GONE;

public class OnBoardingActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.controlLayout)  LinearLayout controlLayout;
    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.btnNext) Button btnNext;
    @BindView(R.id.btnBack) Button btnBack;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private TextView[] mDots;
    private boolean isFinishing;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        ButterKnife.bind(this);
        OnBoardingSliderAdapter sliderAdapter = new OnBoardingSliderAdapter(this);

        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();

        viewPager.setAdapter(sliderAdapter);
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        this.addDotsIndicator(0);
        this.initializeViewPagerListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isNewUser()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initializeViewPagerListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                currentPage = i;
                addDotsIndicator(i);

                if(i == 0) {
                    btnBack.setVisibility(GONE);
                    btnNext.setVisibility(View.VISIBLE);
                    isFinishing = false;
                } else if (i < mDots.length - 1) {
                    btnBack.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                    btnNext.setText(R.string.next);
                    isFinishing = false;
                } else if (i == mDots.length - 1) {
                    btnBack.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                    btnNext.setText(R.string.finish);
                    isFinishing = true;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public void addDotsIndicator(int position) {
        mDots = new TextView[5];
        controlLayout.removeAllViews();
        for(int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i]. setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            controlLayout.addView(mDots[i]);
        }

        if(mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.colorPrimaryYellow));
        }
    }

    boolean isNewUser() {
        return sharedPreferences.getBoolean("isNewUser", true);
    }

    void finishInfo() {
        editor.putBoolean("isNewUser", false);
        editor.commit();
        FirebaseUser user = AccountAPI.get().getFirebaseUser();
        if(user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        btnNext.setVisibility(GONE);
        finish();
    }

    @Override
    public void onClick(View view) {
        if(view.equals(btnBack)) {
            viewPager.setCurrentItem(currentPage - 1);
        } else if (view.equals(btnNext)){
            if(isFinishing) {
                Toasty.success(this,"Great! We wish you success", Toasty.LENGTH_LONG).show();
                finishInfo();
            } else {
                viewPager.setCurrentItem(currentPage + 1);
            }
        }
    }
}