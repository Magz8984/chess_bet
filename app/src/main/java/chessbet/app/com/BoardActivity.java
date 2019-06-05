package chessbet.app.com;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chess.engine.Alliance;
import com.chess.engine.Move;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessengine.BoardPreference;
import chessengine.BoardView;
import chessengine.OnMoveDoneListener;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener, OnMoveDoneListener {
@BindView(R.id.chessLayout) LinearLayout chessLayout;
@BindView(R.id.btnFlip)Button btnFlip;
@BindView(R.id.btnColorPicker) Button btnColorPicker;
@BindView(R.id.btnBack) Button btnBack;
@BindView(R.id.btnForward) Button btnForward;
@BindView(R.id.blackMoves) LinearLayout blackMoves;
@BindView(R.id.whiteMoves) LinearLayout whiteMoves;
@BindView(R.id.blackScrollView) HorizontalScrollView blackScrollView;
@BindView(R.id.whiteScrollView) HorizontalScrollView whiteScrollView;
    BoardView boardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BoardPreference boardPreference;
        boardPreference=new BoardPreference(getPreferences(Context.MODE_PRIVATE));
        setContentView(R.layout.activity_board);
        ButterKnife.bind(this);
        boardView=new BoardView(this);
        boardView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        boardView.setDarkCellsColor(boardPreference.getDark());
        boardView.setWhiteCellsColor(boardPreference.getWhite());
        boardView.setOnMoveDoneListener(this);
        chessLayout.addView(boardView);
        btnFlip.setOnClickListener(this);
        btnColorPicker.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnForward.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(btnFlip)){
           boardView.flipBoardDirection();
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

    @Override
    public void getMove(Move move) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10,0);
        TextView textView = new TextView(this);
        textView.setLayoutParams(params);
        textView.setText(move.toString());
        textView.setTextColor(Color.WHITE);
        if(move.getMovedPiece().getPieceAlliance() == Alliance.BLACK){
            blackMoves.addView(textView);
        }
        else if (move.getMovedPiece().getPieceAlliance() == Alliance.WHITE){
            whiteMoves.addView(textView);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);

        final int blackMoveCount = blackMoves.getChildCount();
        final int whiteMoveCount = whiteMoves.getChildCount();

        if(blackMoveCount >= 1){
            View lastBlackMove = blackMoves.getChildAt(blackMoveCount-1);
            blackScrollView.scrollTo(lastBlackMove.getLeft(), lastBlackMove.getTop());
        }

        if(whiteMoveCount >= 1){
            View lastWhiteMove = whiteMoves.getChildAt(whiteMoveCount-1);
            whiteScrollView.scrollTo(lastWhiteMove.getLeft(), lastWhiteMove.getTop());
        }

    }
}
