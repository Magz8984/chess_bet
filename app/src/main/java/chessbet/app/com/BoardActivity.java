package chessbet.app.com;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessengine.BoardView;

public class BoardActivity extends AppCompatActivity {
@BindView(R.id.chessLayout) LinearLayout chessLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        ButterKnife.bind(this);
        BoardView boardView=new BoardView(this);
        boardView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
//        boardView.setIsFilpped(true);
        chessLayout.addView(boardView);
    }
}