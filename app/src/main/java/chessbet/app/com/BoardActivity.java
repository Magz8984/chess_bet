package chessbet.app.com;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessengine.BoardPreference;
import chessengine.BoardView;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener {
@BindView(R.id.chessLayout) LinearLayout chessLayout;
@BindView(R.id.btnFlip)Button btnFlip;
@BindView(R.id.btnColorPicker) Button btnColorPicker;
@BindView(R.id.btnBack) Button btnBack;
@BindView(R.id.btnForward) Button btnForward;
    BoardView boardView;
    boolean flip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BoardPreference boardPreference;
        boardPreference=new BoardPreference(getPreferences(Context.MODE_PRIVATE));
        setContentView(R.layout.activity_board);
        ButterKnife.bind(this);
        boardView=new BoardView(this);
        boardView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        boardView.setIsFilpped(false);
        boardView.setDark(boardPreference.getDark());
        boardView.setWhite(boardPreference.getWhite());
        chessLayout.addView(boardView);
        btnFlip.setOnClickListener(this);
        btnColorPicker.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnForward.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(btnFlip)){
           flip=!flip;
           boardView.setIsFilpped(flip);
        }
        else if(v.equals(btnColorPicker)){
            ColorPicker colorPicker=new ColorPicker();
            colorPicker.setSharedPreferences(getPreferences(Context.MODE_PRIVATE));
            colorPicker.setBoardView(boardView);
            colorPicker.show(BoardActivity.this.getSupportFragmentManager(),"Color Fragment");
        }
        else if(v.equals(btnBack)){
            boardView.moveBack();
        }
        else if(v.equals(btnForward)){
            boardView.moveForward();
        }
    }
}
