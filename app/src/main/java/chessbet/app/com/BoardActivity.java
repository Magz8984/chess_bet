package chessbet.app.com;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessengine.BoardView;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener {
@BindView(R.id.chessLayout) LinearLayout chessLayout;
@BindView(R.id.btnFlip)Button btnFlip;
    BoardView boardView;
    boolean flip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        ButterKnife.bind(this);
        boardView=new BoardView(this);
        boardView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        boardView.setIsFilpped(false);
        chessLayout.addView(boardView);
        btnFlip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(btnFlip)){
           flip=!flip;
           boardView.setIsFilpped(flip);
        }
    }
}