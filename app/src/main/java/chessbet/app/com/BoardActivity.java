package chessbet.app.com;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chess.engine.Alliance;
import com.chess.engine.Move;
import com.chess.engine.player.Player;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.domain.MatchableAccount;
import chessbet.domain.TimerEvent;
import chessbet.utils.DatabaseUtil;
import chessbet.utils.GameManager;
import chessbet.utils.OnTimerElapsed;
import chessengine.BoardPreference;
import chessengine.BoardView;
import chessengine.GameUtil;
import chessengine.MoveLog;
import chessengine.OnMoveDoneListener;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener, OnMoveDoneListener , OnTimerElapsed{
@BindView(R.id.chessLayout) BoardView boardView;
@BindView(R.id.btnFlip)Button btnFlip;
@BindView(R.id.txtWhiteStatus) TextView txtWhiteStatus;
@BindView(R.id.txtBlackStatus) TextView txtBlackStatus;
@BindView(R.id.btnColorPicker) Button btnColorPicker;
@BindView(R.id.btnBack) Button btnBack;
@BindView(R.id.btnForward) Button btnForward;
@BindView(R.id.blackMoves) LinearLayout blackMoves;
@BindView(R.id.whiteMoves) LinearLayout whiteMoves;
@BindView(R.id.blackScrollView) HorizontalScrollView blackScrollView;
@BindView(R.id.whiteScrollView) HorizontalScrollView whiteScrollView;
@BindView(R.id.txtCountDown) TextView txtCountDown;
private  MatchableAccount matchableAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BoardPreference boardPreference;
        boardPreference=new BoardPreference(getPreferences(Context.MODE_PRIVATE));
        setContentView(R.layout.activity_board);
        ButterKnife.bind(this);
//        GameTimer gameTimer = new GameTimer.Builder()
//                .setTxtMoveTimer(txtCountDown)
//                .setOnMoveTimerElapsed(this)
//                .build();
        boardView.setDarkCellsColor(boardPreference.getDark());
        boardView.setWhiteCellsColor(boardPreference.getWhite());
        boardView.setOnMoveDoneListener(this);
        btnFlip.setOnClickListener(this);
        btnColorPicker.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        txtWhiteStatus.setTextColor(Color.RED);
        txtBlackStatus.setTextColor(Color.RED);
    }

    @Override
    protected void onStart(){
        super.onStart();
        GameUtil.initialize(R.raw.chess_move,this);
        Intent intent = getIntent();
        matchableAccount = intent.getParcelableExtra(DatabaseUtil.matchables);
        if(matchableAccount !=null){
            Toast.makeText(this, matchableAccount.getMatch_type(),Toast.LENGTH_LONG).show();
            boardView.setMatchableAccount(matchableAccount);
        }
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
            boardView.undoMove();
        }
        else if(v.equals(btnForward)){
            boardView.redoMove();
        }
    }

    @Override
    public void getMove(MoveLog moveLog) {
        runOnUiThread(() -> {
            blackMoves.removeAllViews();
            whiteMoves.removeAllViews();
            for (Move move : moveLog.getMoves()){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 0, 10,0);
                TextView textView = new TextView(this);
                textView.setLayoutParams(params);
                textView.setText(move.toString());
                textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        textView.setOnClickListener(v -> {
            Log.d("MOVE", move.toString());
        });
                if(move.getMovedPiece().getPieceAlliance() == Alliance.BLACK){
                    blackMoves.addView(textView);

                }
                else if (move.getMovedPiece().getPieceAlliance() == Alliance.WHITE){
                    whiteMoves.addView(textView);
                }
            }
        });
//        gameTimer.invalidateTimer();
//
//        gameTimer= new GameTimer.Builder()
//                .setTxtMoveTimer(txtCountDown)
//                .setOnMoveTimerElapsed(this)
//                .build();
//        gameTimer.setMoveCountDownTask((1000 * 30) , 1000);
    }

    @Override
    public void isCheckMate(Player player) {
        if(player.getAlliance().isBlack()){
            txtBlackStatus.setText(getString(R.string.checkmate));
        }
        else if(player.getAlliance().isWhite()){
            txtWhiteStatus.setText(getString(R.string.checkmate));
        }
        endGame();
    }

    @Override
    public void isStaleMate(Player player) {
        if(player.getAlliance().isBlack()){
            txtBlackStatus.setText(getString(R.string.stalemate));
        }
        else if(player.getAlliance().isWhite()){
            txtWhiteStatus.setText(getString(R.string.stalemate));
        }
        endGame();
    }

    @Override
    public void isCheck(Player player) {
        if(player.getAlliance().isBlack()){
            txtBlackStatus.setText(getString(R.string.check));
        }
        else if(player.getAlliance().isWhite()){
            txtWhiteStatus.setText(getString(R.string.check));
        }
    }

    @Override
    public void isDraw() {
        txtWhiteStatus.setText(getString(R.string.draw));
        txtBlackStatus.setText(getString(R.string.draw));
        endGame();
    }

    @Override
    public void onGameResume() {
        txtWhiteStatus.setText("");
        txtBlackStatus.setText("");
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

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
       super.onStop();
        GameUtil.getMediaPlayer().release();
    }

    @Override
    public void moveTimerElapsed() {
        try {
            TimerElapsedDialog timerElapsedDialog = new TimerElapsedDialog();
            timerElapsedDialog.setTimerEvent(TimerEvent.MOVE_TIMER_ELAPSED);
            timerElapsedDialog.setResult(boardView.getCurrentPlayer().getOpponent().getAlliance().toString().concat(" WINS"));
            timerElapsedDialog.show(this.getSupportFragmentManager(),"Timer Elapsed Dialog");
            // Board Clearing
            boardView.restartChessBoard();
            whiteMoves.removeAllViews();
            blackMoves.removeAllViews();
            txtBlackStatus.setText("");
            txtWhiteStatus.setText("");

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void moveGameElapsed() {

    }

    private void endGame(){
        if(this.matchableAccount != null){
            this.matchableAccount.endMatch(this);
        }
    }
}
