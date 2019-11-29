package chessbet.app.com;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.chess.engine.Alliance;
import com.chess.engine.Move;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;
import com.chess.pgn.PGNMainUtils;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.Application;
import chessbet.api.AccountAPI;
import chessbet.app.com.fragments.ColorPicker;
import chessbet.app.com.fragments.CreatePuzzle;
import chessbet.domain.MatchableAccount;
import chessbet.domain.Puzzle;
import chessbet.domain.RemoteMove;
import chessbet.domain.TimerEvent;
import chessbet.recievers.ConnectivityReceiver;
import chessbet.services.RemoteViewUpdateListener;
import chessbet.utils.DatabaseUtil;
import chessbet.utils.GameManager;
import chessbet.utils.OnTimerElapsed;
import chessengine.BoardPreference;
import chessengine.BoardView;
import chessengine.GameUtil;
import chessengine.MoveLog;
import chessengine.OnMoveDoneListener;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener, OnMoveDoneListener ,
        OnTimerElapsed, RemoteViewUpdateListener, ConnectivityReceiver.ConnectivityReceiverListener, BoardView.PuzzleMove {
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
@BindView(R.id.whitePieces) LinearLayout whitePieces;
@BindView(R.id.blackPieces) LinearLayout blackPieces;
@BindView(R.id.btnSave) Button btnSave;
@BindView(R.id.btnRecord) Button btnRecord;
@BindView(R.id.txtConnectionStatus) TextView txtConnectionStatus;

    private MatchableAccount matchableAccount;
    private boolean isGameFinished = false;
    private boolean isStoredGame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BoardPreference boardPreference;
        boardPreference = new BoardPreference(getPreferences(Context.MODE_PRIVATE));
        setContentView(R.layout.activity_board);
        ButterKnife.bind(this);
//        GameTimer gameTimer = new GameTimer.Builder()
//                .setTxtMoveTimer(txtCountDown)
//                .setOnMoveTimerElapsed(this)
//                .build();
        boardView.setDarkCellsColor(boardPreference.getDark());
        boardView.setWhiteCellsColor(boardPreference.getWhite());
        boardView.setOnMoveDoneListener(this);
        boardView.setRemoteViewUpdateListener(this);
        btnFlip.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnColorPicker.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        txtWhiteStatus.setTextColor(Color.RED);
        txtBlackStatus.setTextColor(Color.RED);

        this.onConnectionChanged(ConnectivityReceiver.isConnected());
    }

    @Override
    protected void onStart() {
        super.onStart();
        GameUtil.initialize(R.raw.chess_move, this);
        Intent intent = getIntent();
        matchableAccount = intent.getParcelableExtra(DatabaseUtil.matchables);
        if (matchableAccount != null) {
            boardView.setMode(BoardView.Modes.PLAY_ONLINE);
            boardView.setMatchableAccount(matchableAccount);
        }
        // Try To Reconstruct
        String pgn = intent.getStringExtra("pgn");
        if (pgn != null) {
            boardView.setMode(BoardView.Modes.GAME_REVIEW);
            isStoredGame = true;
            boardView.reconstructBoardFromPGN(pgn);
        }

        Puzzle puzzle = (Puzzle) getIntent().getSerializableExtra("Puzzle");
        if(puzzle != null){
            boardView.setPuzzleMove(this);
            btnRecord.setVisibility(View.INVISIBLE);
            btnSave.setVisibility(View.INVISIBLE);
            btnBack.setVisibility(View.INVISIBLE);
            btnForward.setVisibility(View.INVISIBLE);
            boardView.setPuzzle(puzzle);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(btnFlip)){
           boardView.flipBoardDirection();
        }
        else if(v.equals(btnColorPicker)){
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                ColorPicker colorPicker=new ColorPicker();
                colorPicker.setSharedPreferences(getPreferences(Context.MODE_PRIVATE));
                colorPicker.setBoardView(boardView);
                colorPicker.show(BoardActivity.this.getSupportFragmentManager(), "Color Fragment");
            } else {
                Toast.makeText(this, "Feature only available in portrait mode", Toast.LENGTH_LONG).show();
            }
        } else if (v.equals(btnBack)) {
            boardView.undoMove();
        } else if (v.equals(btnForward)) {
            boardView.redoMove();
        } else if (v.equals(btnSave)) {
            if (!isGameFinished && matchableAccount == null && !isStoredGame) { // Enable this for none online games
                Snackbar snackbar = Snackbar.make(btnSave, R.string.save_end_match, Snackbar.LENGTH_LONG)
                        .setAction(R.string.save, v1 -> {
                            storeGameAsPGN("*");
                            isGameFinished = true;
                        });
                snackbar.show();
            } else if (matchableAccount != null) {
                storeGameAsPGN("*");
            }
        }
        else if(v.equals(btnRecord)){
            if(!boardView.isRecording()){
                boardView.setRecording();
                Toast.makeText(this, "Recording Game From Next Move", Toast.LENGTH_LONG).show();
                btnRecord.setBackground(getResources().getDrawable(R.drawable.stop));
            }
            else {
                // Show snack bar to send puzzle
                btnRecord.setBackground(getResources().getDrawable(R.drawable.record_game));
                Snackbar snackbar = Snackbar.make(btnRecord, R.string.create_puzzle_from_recording, Snackbar.LENGTH_LONG)
                        .setAction(R.string.create_puzzle, v1 -> {
                            // Open Up Create Puzzle Dialog
                            Puzzle puzzle = boardView.getPuzzle();
                            if(puzzle.getMoves().size() != 0){
                                puzzle.setOwner(AccountAPI.get().getCurrentUser().getEmail());
                                puzzle.setOwnerPhotoUrl(AccountAPI.get().getCurrentUser().getProfile_photo_url());
                                CreatePuzzle createPuzzle = new CreatePuzzle(puzzle, () -> boardView.setRecording());
                                createPuzzle.show(getSupportFragmentManager(), "Create Puzzle");
                            }
                            else {
                                Toast.makeText(this, "Start Game First", Toast.LENGTH_LONG).show();
                            }
                        });
                snackbar.show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            runOnUiThread(() -> {
                outState.putBoolean("gameFinished", isGameFinished);
                outState.putBoolean("isStoredGame", isStoredGame);
                outState.putString("matchString", boardView.getPortableGameNotation());
                outState.putParcelable("matchableAccount", matchableAccount);
            });
        } catch (Exception ex) {
            Crashlytics.logException(ex);
        }
    }

    @Override
    public void onBackPressed() {
        if (!isStoredGame) {
            Snackbar snackbar = Snackbar.make(btnSave, R.string.save_end_match, Snackbar.LENGTH_LONG)
                    .setAction(R.string.forfeit, v1 -> {
                        // Handle game forfeit
                        if (isGameFinished) {
                            storeGameAsPGN("*");
                        }
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    });
            snackbar.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        runOnUiThread(() -> {
            try {
                isGameFinished = savedInstanceState.getBoolean("gameFinished");
                isStoredGame = savedInstanceState.getBoolean("isStoredGame");
                MatchableAccount matchableAccount = savedInstanceState.getParcelable("matchableAccount");
                boardView.setMatchableAccount(matchableAccount);
                String gameState = savedInstanceState.getString("matchString");
                if (gameState != null) {
                    boardView.reconstructBoardFromPGN(Objects.requireNonNull(savedInstanceState.getString("matchString")));
                }
            } catch (Exception ex) {
                Crashlytics.logException(ex);
            }
        });
    }

    @Override
    public void getMoves(MoveLog moveLog) {
        runOnUiThread(() -> {
            blackMoves.removeAllViews();
            whiteMoves.removeAllViews();
            blackPieces.removeAllViews();
            whitePieces.removeAllViews();
            for (Move move : moveLog.getMoves()) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 0, 10, 0);
                TextView textView = new TextView(this);
                textView.setLayoutParams(params);
                textView.setText(move.toString());
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                textView.setOnClickListener(v -> Log.d("MOVE", move.toString()));
                if (move.getMovedPiece().getPieceAlliance() == Alliance.BLACK) {
                    blackMoves.addView(textView);

                } else if (move.getMovedPiece().getPieceAlliance() == Alliance.WHITE) {
                    whiteMoves.addView(textView);
                }
                // Get taken pieces
                this.getTakenPieces(move);
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
        onGameResume();
        if (player.getAlliance().isBlack()) {
            txtBlackStatus.setText(getString(R.string.checkmate));
            storeGameAsPGN("0-1");
        } else if (player.getAlliance().isWhite()) {
            txtWhiteStatus.setText(getString(R.string.checkmate));
            storeGameAsPGN("1-0");
        }
        endGame();
    }

    @Override
    public void isStaleMate(Player player) {
        onGameResume();
        if (player.getAlliance().isBlack()) {
            txtBlackStatus.setText(getString(R.string.stalemate));
        } else if (player.getAlliance().isWhite()) {
            txtWhiteStatus.setText(getString(R.string.stalemate));
        }
        storeGameAsPGN("1/2-1/2");
        endGame();
    }

    @Override
    public void isCheck(Player player) {
        onGameResume();
        if (player.getAlliance().isBlack()) {
            txtBlackStatus.setText(getString(R.string.check));
        } else if (player.getAlliance().isWhite()) {
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        final int blackMoveCount = blackMoves.getChildCount();
        final int whiteMoveCount = whiteMoves.getChildCount();

        if (blackMoveCount >= 1) {
            View lastBlackMove = blackMoves.getChildAt(blackMoveCount - 1);
            blackScrollView.scrollTo(lastBlackMove.getLeft(), lastBlackMove.getTop());
        }

        if (whiteMoveCount >= 1) {
            View lastWhiteMove = whiteMoves.getChildAt(whiteMoveCount - 1);
            whiteScrollView.scrollTo(lastWhiteMove.getLeft(), lastWhiteMove.getTop());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Application.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GameUtil.getMediaPlayer().release();
    }

    @Override
    public void moveTimerElapsed() {
        try {
            TimerElapsedDialog timerElapsedDialog = new TimerElapsedDialog();
            timerElapsedDialog.setTimerEvent(TimerEvent.MOVE_TIMER_ELAPSED);
            timerElapsedDialog.setResult(boardView.getCurrentPlayer().getOpponent().getAlliance().toString().concat(" WINS"));
            timerElapsedDialog.show(this.getSupportFragmentManager(), "Timer Elapsed Dialog");
            // Board Clearing
            boardView.restartChessBoard();
            whiteMoves.removeAllViews();
            blackMoves.removeAllViews();
            txtBlackStatus.setText("");
            txtWhiteStatus.setText("");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void moveGameElapsed() {

    }

    private void endGame() {
        if (this.matchableAccount != null) {
            this.matchableAccount.endMatch(this);
        }
    }

    private void getTakenPieces(Move move) {
        if (move.getAttackedPiece() != null) {
            Piece piece = move.getAttackedPiece();
            ImageView imageView = new ImageView(this);
            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), getApplicationContext().getResources()
                    .getIdentifier(piece.toString().concat(piece.getPieceAlliance().toString()).toLowerCase(), "drawable", getPackageName()));

            assert drawable != null;
            drawable.clearColorFilter();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(40, 40);

            imageView.setLayoutParams(params);
            imageView.setBackground(drawable);
            imageView.invalidate();

            if (piece.getPieceAlliance().equals(Alliance.BLACK)) {
                blackPieces.addView(imageView);
            } else if (piece.getPieceAlliance().equals(Alliance.WHITE)) {
                whitePieces.addView(imageView);
            }
        }
    }

    @Override
    public void onRemoteMoveMade(RemoteMove remoteMove) {
        runOnUiThread(() -> boardView.translateRemoteMoveOnBoard(remoteMove));
    }

    protected void storeGameAsPGN(String result) {
        if(boardView.getMode().equals(BoardView.Modes.PUZZLE_MODE)){
            return;
        }
        isGameFinished = true; // Is game on going is false
        MoveLog moveLog = boardView.getMoveLog();
        String gameText = PGNMainUtils.writeGameAsPGN(moveLog.convertToEngineMoveLog(), "N/A", "N/A", result);
        FileOutputStream fileOutputStream = null;

        try {
            // Ensures its not a stored game and there are moves made
            if (!isStoredGame && boardView.getMoveLog().getMoves().size() != 0) {
                String file_name = String.format(GameManager.FULL_GAME_FILE, new Date().getTime());
                fileOutputStream = openFileOutput(file_name, MODE_PRIVATE);
                fileOutputStream.write(gameText.getBytes());
                Toast.makeText(this, "Saved to : " + getFilesDir() + "/" + file_name, Toast.LENGTH_LONG).show();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onConnectionChanged(boolean isConnected) {
        if(isConnected){
            txtConnectionStatus.setTextColor(Color.GREEN);
            txtConnectionStatus.setText(getResources().getString(R.string.online));
        }else{
            txtConnectionStatus.setTextColor(Color.RED);
            txtConnectionStatus.setText(getResources().getString(R.string.offline));
        }
    }

    @Override
    public void onCorrectMoveMade(boolean isCorrect) {
        if(isCorrect){
            Toast.makeText(this, "Correct Move", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Try Again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPuzzleFinished() {
        Toast.makeText(this, "Finished Puzzle", Toast.LENGTH_LONG).show();
    }
}
