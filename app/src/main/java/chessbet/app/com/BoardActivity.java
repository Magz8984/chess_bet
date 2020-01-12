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
import com.chess.engine.ECOBuilder;
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
import chessbet.api.ChallengeAPI;
import chessbet.api.MatchAPI;
import chessbet.app.com.fragments.ColorPicker;
import chessbet.app.com.fragments.CreatePuzzle;
import chessbet.app.com.fragments.EvaluateGame;
import chessbet.domain.Constants;
import chessbet.domain.MatchEvent;
import chessbet.domain.MatchStatus;
import chessbet.domain.MatchType;
import chessbet.domain.MatchableAccount;
import chessbet.domain.Puzzle;
import chessbet.domain.RemoteMove;
import chessbet.recievers.ConnectivityReceiver;
import chessbet.services.MatchListener;
import chessbet.services.MatchService;
import chessbet.services.OpponentListener;
import chessbet.services.RemoteViewUpdateListener;
import chessbet.utils.ConnectivityManager;
import chessbet.utils.DatabaseUtil;
import chessbet.utils.GameHandler;
import chessbet.utils.GameManager;
import chessbet.utils.GameTimer;
import chessbet.utils.OnTimerElapsed;
import chessengine.BoardPreference;
import chessengine.BoardView;
import chessengine.ECOBook;
import chessengine.GameUtil;
import chessengine.MoveLog;
import chessengine.OnMoveDoneListener;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener, OnMoveDoneListener ,
        OnTimerElapsed, RemoteViewUpdateListener, ConnectivityReceiver.ConnectivityReceiverListener, BoardView.PuzzleMove,
        ConnectivityManager.ConnectionStateListener, ECOBook.OnGetECOListener, MatchAPI.OnMatchEnd, OpponentListener , GameHandler.NoMoveReactor.NoMoveEndMatch {
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
@BindView(R.id.btnHint) Button btnHint;
@BindView(R.id.btnRecord) Button btnRecord;
@BindView(R.id.blackTimer) TextView txtBlackTimer;
@BindView(R.id.whiteTimer) TextView txtWhiteTimer;
@BindView(R.id.txtConnectionStatus) TextView txtConnectionStatus;
@BindView(R.id.imgConnectionStatus) CircleImageView imgConnectionStatus;
@BindView(R.id.txtWhite) TextView txtWhite;
@BindView(R.id.btnAnalysis) Button btnAnalysis;
@BindView(R.id.txtBlack) TextView txtBlack;

private MatchableAccount matchableAccount;
private ConnectivityManager connectivityManager;
private boolean isGameFinished = false;
private boolean isStoredGame = false;
private EvaluateGame evaluateGame;
private GameHandler.NoMoveReactor noMoveReactor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BoardPreference boardPreference;
        boardPreference = new BoardPreference(getPreferences(Context.MODE_PRIVATE));
        setContentView(R.layout.activity_board);
        ButterKnife.bind(this);
        connectivityManager = new ConnectivityManager();
        boardView.setDarkCellsColor(boardPreference.getDark());
        boardView.setWhiteCellsColor(boardPreference.getWhite());
        boardView.setOnMoveDoneListener(this);
        boardView.setRemoteViewUpdateListener(this);
        boardView.setEcoBookListener(this);
        btnFlip.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnColorPicker.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnHint.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        btnAnalysis.setOnClickListener(this);
        txtWhiteStatus.setTextColor(Color.RED);
        txtBlackStatus.setTextColor(Color.RED);
        connectivityManager.setConnectionStateListener(this);
        connectivityManager.startListening();
        this.onConnectionChanged(ConnectivityReceiver.isConnected());
    }

    @Override
    protected void onStart() {
        super.onStart();
        isGameFinished = false;

        GameUtil.initialize(R.raw.chess_move, this);
        Intent intent = getIntent();

        String challengeId = intent.getStringExtra(Constants.CHALLENGE_ID);
        if(challengeId != null){
            Toast.makeText(this, "Accepting Challenge", Toast.LENGTH_LONG).show();
            boardView.setEnabled(false);
            ChallengeTaker challengeTaker = new ChallengeTaker(challengeId);
            challengeTaker.acceptChallenge();
        }

        String matchType = intent.getStringExtra("match_type");

        if(matchType != null && matchType.equals(MatchType.SINGLE_PLAYER.toString())){
            txtWhite.setText(AccountAPI.get().getFirebaseUser().getDisplayName());
            txtBlack.setText(getResources().getString(R.string.computer));
            boardView.setMode(BoardView.Modes.PLAY_COMPUTER);
        }

        matchableAccount = intent.getParcelableExtra(DatabaseUtil.matchables);
        if (matchableAccount != null) {
            configureMatch(matchableAccount);
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


    private void configureMatch(MatchableAccount matchableAccount){
        // Remove advantage controls
        btnHint.setVisibility(View.INVISIBLE);
        btnForward.setVisibility(View.INVISIBLE);
        btnBack.setVisibility(View.INVISIBLE);
        noMoveReactor = new GameHandler.NoMoveReactor(matchableAccount);
        noMoveReactor.setNoMoveEndMatch(this);
        noMoveReactor.execute();

        this.matchableAccount = matchableAccount;
        boardView.setMode(BoardView.Modes.PLAY_ONLINE);
        boardView.setMatchableAccount(matchableAccount);
        boardView.getMatchAPI().setOnMatchEnd(this);
        boardView.getMatchAPI().setRecentMatchEvaluated(false);
        setNoMoveReactorPly();

        // Get opponent details and place in SQLiteDB
        GameHandler.BackgroundMatchBuilder backgroundMatchBuilder = new GameHandler.BackgroundMatchBuilder();
        backgroundMatchBuilder.setOpponentListener(this);
        backgroundMatchBuilder.setMatchableAccount(matchableAccount);
        backgroundMatchBuilder.execute(this);

        // Set timer online game and make sure you do not set two different timers
        if(boardView.getGameTimer() == null){
            GameTimer.Builder builder = new GameTimer.Builder()
                    .setTxtBlackMoveTimer(txtBlackTimer)
                    .setTxtWhiteMoveTimer(txtWhiteTimer)
                    .setOnTimerElapsed(this);
            if(GameTimer.get() == null){
                boardView.setGameTimer(builder.build());

            } else {
                // Reset timer in board view;
                GameTimer.get().setBuilder(builder);
                // Do not set a new builder it will set two new timers
                boardView.setGameTimer(GameTimer.get());
            }
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
            } else if (!isStoredGame) {
                storeGameAsPGN(storageGameProxy());
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
                            } else {
                                Toast.makeText(this, "Start Game First", Toast.LENGTH_LONG).show();
                            }
                        });
                snackbar.show();
            }
        } else if(v.equals(btnHint)) {
            boardView.requestHint();
            if(boardView.isHinting()){
                Toast.makeText(this, "Hinting On", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Hinting Off", Toast.LENGTH_LONG).show();
            }
        }  else if (v.equals(btnAnalysis)){
            if (!isGameFinished) {
                Snackbar snackbar = Snackbar.make(btnAnalysis, R.string.analysis_prompt, Snackbar.LENGTH_LONG)
                        .setAction(R.string.yes, view -> {
                            if(matchableAccount != null){
                                endGame(GameHandler.GAME_INTERRUPTED_FLAG);
                            }
                            goToGameAnalysisActivity();
                        });
                snackbar.show();
            } else {
                goToGameAnalysisActivity();
            }
        }
    }

    private String storageGameProxy(){
        if(isGameFinished){
            if(boardView.getCurrentPlayer().isInStaleMate() || boardView.isGameDrawn()){
                return "1/2-1/2";
            } else if(boardView.getCurrentPlayer().isInCheckMate()) {
                if(boardView.getCurrentPlayer().getAlliance().isBlack()){
                    return "1-0";
                } else {
                    return "0-1";
                }
            }
        }
        return "*";
    }

    private void goToGameAnalysisActivity(){
        Intent intent = new Intent(this, GameAnalysisActivity.class);
        intent.putExtra("pgn", boardView.getPortableGameNotation());
        startActivity(intent);
        finish();
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

    private void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!isStoredGame) {
            Snackbar snackbar = Snackbar.make(btnSave, R.string.save_end_match, Snackbar.LENGTH_LONG)
                    .setAction(R.string.forfeit, v1 -> {
                        if(matchableAccount != null && !isGameFinished){
                           endGame(GameHandler.GAME_INTERRUPTED_FLAG);
                        } else {
                            goToMainActivity();
                        }

                    });
            snackbar.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
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
            setNoMoveReactorPly();
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
    }

    public void setNoMoveReactorPly(){
        if(noMoveReactor != null){
            noMoveReactor.clearNoMoveSeconds();
            noMoveReactor.setMyPly(boardView.isMyPly());
            // Notify NoMoveReactor You Have Made A Move
            if(boardView.isMyPly() && !boardView.getMoveLog().getMoves().isEmpty()){
                noMoveReactor.setHasMadeMove(true);
            }
        }
    }

    @Override
    public void isCheckMate(Player player) {
        onGameResume();
        if (player.getAlliance().isBlack()) {
            txtBlackStatus.setText(getString(R.string.checkmate));
        } else if (player.getAlliance().isWhite()) {
            txtWhiteStatus.setText(getString(R.string.checkmate));
        }
        endGame(GameHandler.GAME_FINISHED_FLAG);
    }

    @Override
    public void isStaleMate(Player player) {
        onGameResume();
        if (player.getAlliance().isBlack()) {
            txtBlackStatus.setText(getString(R.string.stalemate));
        } else if (player.getAlliance().isWhite()) {
            txtWhiteStatus.setText(getString(R.string.stalemate));
        }
        endGame(GameHandler.GAME_DRAWN_FLAG);
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
        endGame(GameHandler.GAME_DRAWN_FLAG);
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
        boardView.setOnlineBoardDirection();
        Application.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectivityManager.stopListening();
        if(noMoveReactor != null){
            noMoveReactor.stopTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GameUtil.getMediaPlayer().release();
        this.stopService(new Intent(this, MatchService.class));
    }


    private void endGame(int flag) {
        this.isGameFinished = true;
        if (this.matchableAccount != null) {
            if(boardView.getMatchAPI().isRecentMatchEvaluated()){
                return;
            }
            storeGameOnCloud();
            // Stop service
            this.stopService(new Intent(this, MatchService.class));
            GameTimer.get().stopAllTimers();

            // Listens for an elo update
            AccountAPI.get().listenToAccountUpdate();

            evaluateGame = new EvaluateGame(); // Game evaluation fragment
            evaluateGame.setInitialPoints(AccountAPI.get().getCurrentAccount().getElo_rating());
            evaluateGame.setOpponent(MatchAPI.get().getCurrentMatch().getOpponentUserName());

            if (flag == GameHandler.GAME_DRAWN_FLAG) {
                matchableAccount.endMatch(boardView.getPortableGameNotation(), GameHandler.GAME_DRAWN_FLAG, MatchStatus.DRAW, null, null);
                evaluateGame.setMatchStatus(MatchStatus.DRAW);
            } else if (flag == GameHandler.GAME_INTERRUPTED_FLAG) {
                evaluateGame.setMatchStatus(MatchStatus.INTERRUPTED);
                RemoteMove.get().addEvent(MatchEvent.INTERRUPTED);
                RemoteMove.get().send(matchableAccount.getMatchId(), matchableAccount.getSelf());
                matchableAccount.endMatch(boardView.getPortableGameNotation(), GameHandler.GAME_INTERRUPTED_FLAG, MatchStatus.INTERRUPTED, matchableAccount.getOpponentId(), matchableAccount.getOwner());
            } else if (flag == GameHandler.GAME_FINISHED_FLAG) {
                if(boardView.isLocalWinner()){
                    evaluateGame.setMatchStatus(MatchStatus.WON);
                    matchableAccount.endMatch(boardView.getPortableGameNotation(),
                            GameHandler.GAME_FINISHED_FLAG, MatchStatus.WON,
                            matchableAccount.getOwner(), matchableAccount.getOpponentId());
                } else {
                    evaluateGame.setMatchStatus(MatchStatus.LOSS);
                }
            } else if (flag == GameHandler.GAME_TIMER_LAPSED){
                evaluateGame.setMatchStatus(MatchStatus.TIMER_LAPSED);
                RemoteMove.get().addEvent(MatchEvent.TIMER_LAPSED);
                RemoteMove.get().send(matchableAccount.getMatchId(), matchableAccount.getSelf());
                matchableAccount.endMatch(boardView.getPortableGameNotation(), GameHandler.GAME_TIMER_LAPSED,
                        MatchStatus.TIMER_LAPSED, matchableAccount.getOpponentId(), matchableAccount.getOwner());
            }
            // End Game
            // Deletes challenge when the game ends
            ChallengeAPI.get().deleteChallenge();
            AccountAPI.get().getAccount();
//            goToMainActivity();
            MatchAPI.get().removeCurrentMatch();
            evaluateGame.show(getSupportFragmentManager(), "EvaluateGame"); // Span Up Ad
        }
    }

    private void storeGameOnCloud(){
        if(matchableAccount != null && boardView.getLocalAlliance().equals(Alliance.WHITE)){
            MatchAPI.get().storeCurrentMatchOnCloud(PGNMainUtils.writeGameAsPGN(boardView.getMoveLog().convertToEngineMoveLog(),
                    AccountAPI.get().getCurrentUser().getUser_name(),
                    MatchAPI.get().getCurrentMatch().getOpponentUserName(), "*"),
                    taskSnapshot -> Log.d(BoardActivity.class.getSimpleName(), "Match Uploaded"));
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
//                GameTimer.get().stopAllTimers();
                blackPieces.addView(imageView);
            } else if (piece.getPieceAlliance().equals(Alliance.WHITE)) {
                whitePieces.addView(imageView);
            }
        }
    }

    @Override
    public void onRemoteMoveMade(RemoteMove remoteMove) {
        runOnUiThread(() -> {
            boardView.translateRemoteMoveOnBoard(remoteMove);
            noMoveReactor.clearNoMoveSeconds();
            noMoveReactor.setHasOpponentMoved(true);
            noMoveReactor.setPgn(boardView.getPortableGameNotation()); // Set current game status
        });
    }

    protected void storeGameAsPGN(String result) {
        // Ignore game storage if game is a puzzle
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

    @Override
    public void playerTimeLapsed(chessbet.domain.Player player) {
        if(player.equals(chessbet.domain.Player.WHITE) && boardView.getLocalAlliance().equals(Alliance.WHITE)){
            endGame(GameHandler.GAME_TIMER_LAPSED);
        } else if (player.equals(chessbet.domain.Player.BLACK) && boardView.getLocalAlliance().equals(Alliance.BLACK)){
            endGame(GameHandler.GAME_TIMER_LAPSED);
        }
    }

    @Override
    public void onConnectionStateChanged(ConnectivityManager.ConnectionQuality connectionQuality) {
        Log.d("SPEED_NET", connectionQuality.toString());
        runOnUiThread(() -> {
            switch (connectionQuality) {
                case UNKNOWN:
                    notifyLowInternetConnection();
                    imgConnectionStatus.setImageDrawable(getResources().getDrawable(R.drawable.low_red));
                    break;
                case GOOD:
                    notifyGoodInternetConnection();
                    imgConnectionStatus.setImageDrawable(getResources().getDrawable(R.drawable.good_connection));
                    break;
                case AVERAGE:
                    notifyGoodInternetConnection();
                    imgConnectionStatus.setImageDrawable(getResources().getDrawable(R.drawable.medium_connection));
                    break;
                case POOR:
                    notifyLowInternetConnection();
                    imgConnectionStatus.setImageDrawable(getResources().getDrawable(R.drawable.low_red));
                    break;
                case EXCELLENT:
                    notifyGoodInternetConnection();
                    imgConnectionStatus.setImageDrawable(getResources().getDrawable(R.drawable.high_connection));
                    break;
            }
        });
    }

    private void notifyLowInternetConnection(){
        if(matchableAccount != null){
            Toasty.warning(this, R.string.low_internet_connection, Toasty.LENGTH_LONG).show();
            boardView.setEnabled(false);
        }
    }

    private void notifyGoodInternetConnection() {
        if (matchableAccount != null) {
            boardView.setEnabled(true);
        }
    }

    @Override
    public void onGetECO(ECOBuilder.ECO eco) {
        runOnUiThread(() -> Toast.makeText(this, eco.getOpening(), Toast.LENGTH_LONG).show());
    }

    @Override
    public void onMatchEnd(MatchStatus matchStatus) {
        // Stop service
        this.stopService(new Intent(this, MatchService.class));
        storeGameOnCloud();
        ChallengeAPI.get().deleteChallenge(); // Delete current challenge
        isGameFinished = true; // Flag Game Has Ended
        evaluateGame = new EvaluateGame();
        GameTimer.get().stopAllTimers();
        AccountAPI.get().listenToAccountUpdate();
        evaluateGame.setInitialPoints(AccountAPI.get().getCurrentAccount().getElo_rating());
        evaluateGame.setMatchStatus(matchStatus);
        evaluateGame.show(getSupportFragmentManager(), "EvaluateGame");
    }

    @Override
    public void onOpponentReceived(String opponent) {
        runOnUiThread(() -> {
            if (boardView.getLocalAlliance().isWhite()) {
                txtWhite.setText(AccountAPI.get().getCurrentUser().getUser_name());
                txtBlack.setText(opponent);
            }

            if(boardView.getLocalAlliance().isBlack())  {
                txtWhite.setText(opponent);
                txtBlack.setText(AccountAPI.get().getCurrentUser().getUser_name());
            }
        });
    }

    /**
     * @author Collins Magondu 10/01/2020
     * Used to accept challenges from notifications
     */
    private class ChallengeTaker implements MatchListener {
        private String challengeId;

        ChallengeTaker(String challengeId){
            this.challengeId = challengeId;
            MatchAPI.get().setMatchListener(this);
            MatchAPI.get().getAccount();
        }

        void acceptChallenge(){
            MatchableAccount matchableAccount = new MatchableAccount();
            matchableAccount.setOwner(AccountAPI.get().getCurrentUser().getUid());
            matchableAccount.setMatch_type(MatchType.PLAY_ONLINE.toString());
            matchableAccount.setDuration(Constants.DEFAULT_MATCH_DURATION);
            matchableAccount.setElo_rating(AccountAPI.get().getCurrentAccount().getElo_rating());
            MatchAPI.get().createUserMatchableAccountImplementation(matchableAccount);
        }

        @Override
        public void onMatchMade(MatchableAccount matchableAccount) {
            configureMatch(matchableAccount);
        }

        @Override
        public void onMatchableCreatedNotification() {
            ChallengeAPI.get().acceptChallenge(challengeId);
        }

        @Override
        public void onMatchError() {
            Crashlytics.logException(new RuntimeException("Match could was not created for "
                    + AccountAPI.get().getCurrentUser().getUid()
                    + " " + new Date().toString()));
        }
    }

    @Override
    public void onNoMoveEndMatch(MatchStatus matchStatus) {
        noMoveReactor.stopTimer();
        onMatchEnd(matchStatus);
    }

    @Override
    public void onNoMoveOpponentReacting() {
        runOnUiThread(() -> Toasty.warning(this, "10 seconds left for opponent", Toasty.LENGTH_LONG).show());
    }

    @Override
    public void onNoMoveSelfReacting() {
        runOnUiThread(() -> Toasty.warning(this, "10 seconds left to make move", Toasty.LENGTH_LONG).show());
    }

    @Override
    public void onLoseNoMove(MatchStatus matchStatus) {
        noMoveReactor.stopTimer();
        onMatchEnd(matchStatus);
    }
}
