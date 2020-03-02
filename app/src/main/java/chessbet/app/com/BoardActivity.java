package chessbet.app.com;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
import chessbet.api.PresenceAPI;
import chessbet.app.com.fragments.ColorPicker;
import chessbet.app.com.fragments.CreatePuzzle;
import chessbet.app.com.fragments.EvaluateGame;
import chessbet.domain.Account;
import chessbet.domain.Constants;
import chessbet.domain.MatchEvent;
import chessbet.domain.MatchStatus;
import chessbet.domain.MatchType;
import chessbet.domain.MatchableAccount;
import chessbet.domain.Puzzle;
import chessbet.domain.RemoteMove;
import chessbet.domain.User;
import chessbet.recievers.ConnectivityReceiver;
import chessbet.services.AccountListener;
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
import stockfish.EngineUtil;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener, OnMoveDoneListener ,
        OnTimerElapsed, RemoteViewUpdateListener, ConnectivityReceiver.ConnectivityReceiverListener, BoardView.PuzzleMove,
        ConnectivityManager.ConnectionStateListener, ECOBook.OnGetECOListener, MatchAPI.OnMatchEnd, OpponentListener , GameHandler.NoMoveReactor.NoMoveEndMatch, PresenceAPI.UserOnline {
@BindView(R.id.chessLayout) BoardView boardView;
@BindView(R.id.btnFlip)Button btnFlip;
@BindView(R.id.txtOwnerStatus) TextView txtOwnerStatus;
@BindView(R.id.txtOpponentStatus) TextView txtOpponentStatus;
@BindView(R.id.btnColorPicker) Button btnColorPicker;
@BindView(R.id.btnBack) Button btnBack;
@BindView(R.id.btnForward) Button btnForward;
@BindView(R.id.opponentMoves) LinearLayout opponentMoves;
@BindView(R.id.ownerMoves) LinearLayout ownerMoves;
@BindView(R.id.blackScrollView) HorizontalScrollView blackScrollView;
@BindView(R.id.whiteScrollView) HorizontalScrollView whiteScrollView;
@BindView(R.id.ownerPieces) LinearLayout ownerPieces;
@BindView(R.id.opponentPieces) LinearLayout opponentPieces;
@BindView(R.id.btnSave) Button btnSave;
@BindView(R.id.btnHint) Button btnHint;
@BindView(R.id.btnRecord) Button btnRecord;
@BindView(R.id.txtOpponentTimer) TextView txtOpponentTimer;
@BindView(R.id.txtOwnerTimer) TextView txtOwnerTimer;
@BindView(R.id.txtConnectionStatus) TextView txtConnectionStatus;
@BindView(R.id.imgConnectionStatus) CircleImageView imgConnectionStatus;
@BindView(R.id.txtOwner) TextView txtOwner;
@BindView(R.id.btnAnalysis) Button btnAnalysis;
@BindView(R.id.txtOpponent) TextView txtOpponent;

private MatchableAccount matchableAccount;
private ConnectivityManager connectivityManager;
private boolean isGameFinished = false;
private boolean isStoredGame = false;
private EvaluateGame evaluateGame;
private boolean isSavedState; // Checks if activity has been stopped
private GameHandler.NoMoveReactor noMoveReactor;
private boolean isActiveConnectionFlag = false;
private ProgressDialog challengeProgressDialog;
private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BoardPreference boardPreference;
        boardPreference = new BoardPreference(getPreferences(Context.MODE_PRIVATE));
        setContentView(R.layout.activity_board);
        ButterKnife.bind(this);
        connectivityManager = new ConnectivityManager();
        challengeProgressDialog = new ProgressDialog(this);
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
        txtOpponentStatus.setTextColor(Color.RED);
        txtOwnerStatus.setTextColor(Color.RED);
        connectivityManager.setConnectionStateListener(this);
        connectivityManager.startListening();
        this.onConnectionChanged(ConnectivityReceiver.isConnected());
    }

    private Alliance getOpponentAlliance(){
       if(matchableAccount != null){
        return (this.boardView.getLocalAlliance() == Alliance.WHITE) ? Alliance.BLACK : Alliance.WHITE;
       }
       return Alliance.BLACK;
    }

    private Alliance getOwnerAlliance(){
        if(matchableAccount != null){
            return (this.boardView.getLocalAlliance() == Alliance.WHITE) ? Alliance.WHITE : Alliance.BLACK;
        }
        return Alliance.WHITE;
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        isGameFinished = false;

        GameUtil.initialize(R.raw.chess_move, this);
        Intent intent = getIntent();

        setExternalPlayers();
        configureChallenge(intent);
        String matchType = intent.getStringExtra("match_type");
        long skillLevel = intent.getLongExtra("skill_level", 20);
        EngineUtil.setSkillLevel(skillLevel);

        if(matchType != null && matchType.equals(MatchType.SINGLE_PLAYER.toString())){
            EngineUtil.setUCIELORating(EngineUtil.getEloFromSkillLevel());
            btnHint.setVisibility(View.GONE);
            txtOwner.setText(AccountAPI.get().getFirebaseUser().getDisplayName());
            txtOpponent.setText(getResources().getString(R.string.computer, skillLevel));
            boardView.setMode(BoardView.Modes.PLAY_COMPUTER);
        }

        matchableAccount = intent.getParcelableExtra(DatabaseUtil.matchables);
        if (matchableAccount != null && ChallengeAPI.get().isOnChallenge()) {
            configureMatch(matchableAccount);
        }
        // Try To Reconstruct
        String pgn = intent.getStringExtra(Constants.PGN);
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

    /**
     * Get players from external activities or apps
     */
    private void setExternalPlayers(){
        Intent intent = getIntent();
        String white = intent.getStringExtra(Constants.WHITE);
        String black = intent.getStringExtra(Constants.BLACK);
        if(black != null && white != null){
            txtOpponent.setText(black);
            txtOwner.setText(white);
        }
    }


    private void configureMatch(MatchableAccount matchableAccount){
        // Disable advantage controls
        btnHint.setEnabled(false);
        btnForward.setEnabled(false);
        btnBack.setEnabled(false);

        noMoveReactor = new GameHandler.NoMoveReactor(matchableAccount);
        noMoveReactor.setNoMoveEndMatch(this);
        noMoveReactor.execute();

        GameHandler.getInstance().setNoMoveReactor(noMoveReactor); // Save this in current single tone

        this.matchableAccount = matchableAccount;
        boardView.setMode(BoardView.Modes.PLAY_ONLINE);
        boardView.setMatchableAccount(matchableAccount);
        boardView.setOnlineBoardDirection();
        boardView.getMatchAPI().setOnMatchEnd(this);
        boardView.getMatchAPI().setRecentMatchEvaluated(false);
        setNoMoveReactorPly();

        // Get opponent details and place in SQLiteDB
        GameHandler.BackgroundMatchBuilder backgroundMatchBuilder = new GameHandler.BackgroundMatchBuilder();
        backgroundMatchBuilder.setOpponentListener(this);
        backgroundMatchBuilder.setMatchableAccount(matchableAccount);
        backgroundMatchBuilder.execute(this);
        setBoardViewGameTimer();
    }

    /**
     * Helper Method
     * Sets BoardView game timer if game timer is new or has been lost by configuration changes
     */
    private void setBoardViewGameTimer(){
        // Set timer online game and make sure you do not set two different timers
        if(boardView.getGameTimer() == null){
            GameTimer.Builder builder = new GameTimer.Builder()
                    .setTxtOpponent(txtOpponentTimer)
                    .setTxtOwner(txtOwnerTimer)
                    .setOpponentAlliance(getOpponentAlliance())
                    .setOwnerAlliance(getOwnerAlliance())
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

    private void configureChallenge(Intent intent){
        // Disable challenge creation if user has either accepted challenge or has an accepted challenge
        if(ChallengeAPI.get().isChallengeAccepted() ||  ChallengeAPI.get().hasAcceptedChallenge()){
            return;
        }
        String challengeId = intent.getStringExtra(Constants.CHALLENGE_ID);
        String challenger = intent.getStringExtra(Constants.CHALLENGER);
        boolean isChallenger = intent.getBooleanExtra(Constants.IS_CHALLENGER, false);
        if(challengeId != null){
            // Start challenge dialog
            challengeProgressDialog.setMessage(getResources().getString(R.string.accepting_challenge));
            challengeProgressDialog.setCancelable(false);
            challengeProgressDialog.show();
            boardView.setEnabled(false);
            ChallengeTaker challengeTaker = new ChallengeTaker(challengeId);
            challengeProgressDialog.show();
            if(!isChallenger){
                ChallengeAPI.get().sendChallengeNotification(challengeId, challenger); // Send notification to the challenger
                ChallengeAPI.get().setChallengeByAccount(challenger, challengeId, new ChallengeAPI.ChallengeSent() {
                    @Override
                    public void onChallengeSent() {
                        Log.d(BoardActivity.class.getSimpleName(), "Challenge sent to sender");
                    }

                    @Override
                    public void onChallengeNotSent() {
                        Crashlytics.logException(new RuntimeException("Challenge not sent back"));
                    }
                });
                challengeTaker.acceptChallenge();
            } else {
                ChallengeAPI.get().setChallengeAccepted(true);
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
            boardView.reHint();
        } else if (v.equals(btnForward)) {
            boardView.redoMove();
            boardView.reHint();
        } else if (v.equals(btnSave)) {
            if (!isGameFinished && matchableAccount == null && !isStoredGame) { // Enable this for none online games
                Snackbar snackbar = Snackbar.make(btnSave, R.string.save, Snackbar.LENGTH_LONG)
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
                isSavedState = true;
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
        PresenceAPI.get().stopListening();
        this.stopGameTimers();
        this.stopNoMoveReactor();
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!isStoredGame) {
            Snackbar snackbar = Snackbar.make(btnSave, R.string.end_match, Snackbar.LENGTH_LONG)
                    .setAction(R.string.forfeit, v1 -> {
                        MatchAPI.get().setMatchCreated(false); // FLAG MatchAPI
                        PresenceAPI.get().stopListening(); // Stop listening to opponent online state
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
                if(matchableAccount != null){
                    configureMatch(matchableAccount);
                }
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
            opponentMoves.removeAllViews();
            ownerMoves.removeAllViews();
            opponentPieces.removeAllViews();
            ownerPieces.removeAllViews();
            setNoMoveReactorPly();
            for (Move move : moveLog.getMoves()) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 0, 10, 0);
                TextView textView = new TextView(this);
                textView.setLayoutParams(params);
                textView.setText(move.toString());
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                if (move.getMovedPiece().getPieceAlliance().equals(getOpponentAlliance())) {
                    opponentMoves.addView(textView);

                } else if (move.getMovedPiece().getPieceAlliance().equals(getOwnerAlliance())) {
                    ownerMoves.addView(textView);
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
        runOnUiThread(() -> {
            onGameResume();
            if (player.getAlliance().equals(getOpponentAlliance())) {
                txtOpponentStatus.setText(getString(R.string.checkmate));
            } else if (player.getAlliance().equals(getOwnerAlliance())) {
                txtOwnerStatus.setText(getString(R.string.checkmate));
            }
            endGame(GameHandler.GAME_FINISHED_FLAG);
        });
    }

    @Override
    public void isStaleMate(Player player) {
        runOnUiThread(() -> {
            onGameResume();
            if (player.getAlliance().equals(getOpponentAlliance())) {
                txtOpponentStatus.setText(getString(R.string.stalemate));
            } else if (player.getAlliance().equals(getOwnerAlliance())) {
                txtOwnerStatus.setText(getString(R.string.stalemate));
            }
            endGame(GameHandler.GAME_DRAWN_FLAG);
        });
    }

    @Override
    public void isCheck(Player player) {
        runOnUiThread(() -> {
            onGameResume();
            if (player.getAlliance().equals(getOpponentAlliance())) {
                txtOpponentStatus.setText(getString(R.string.check));
            } else if (player.getAlliance().equals(getOwnerAlliance())) {
                txtOwnerStatus.setText(getString(R.string.check));
            }
        });
    }

    @Override
    public void isDraw() {
        runOnUiThread(() -> {
            txtOwnerStatus.setText(getString(R.string.draw));
            txtOpponentStatus.setText(getString(R.string.draw));
            endGame(GameHandler.GAME_DRAWN_FLAG);
        });
    }

    @Override
    public void onGameResume() {
        runOnUiThread(() -> {
            txtOwnerStatus.setText("");
            txtOpponentStatus.setText("");
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        final int blackMoveCount = opponentMoves.getChildCount();
        final int whiteMoveCount = ownerMoves.getChildCount();

        if (blackMoveCount >= 1) {
            View lastBlackMove = opponentMoves.getChildAt(blackMoveCount - 1);
            blackScrollView.scrollTo(lastBlackMove.getLeft(), lastBlackMove.getTop());
        }

        if (whiteMoveCount >= 1) {
            View lastWhiteMove = ownerMoves.getChildAt(whiteMoveCount - 1);
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
        MatchAPI.get().setMatchCreated(false); // FLAG MatchAPI
        GameUtil.getMediaPlayer().release();
        PresenceAPI.get().stopListening(); // Stop listening to opponent
        this.stopService(new Intent(this, MatchService.class));
    }


    private void endGame(int flag) {
        this.isGameFinished = true;
        if (this.matchableAccount != null) {
            if(boardView.getMatchAPI().isRecentMatchEvaluated()){
                return;
            }
            ChallengeAPI.get().setOnChallenge(false);
            storeGameOnCloud();
            // Stop service
            this.stopService(new Intent(this, MatchService.class));
            this.stopGameTimers();
            this.noMoveReactor.stopTimer();

            // Listens for an elo update
            AccountAPI.get().listenToAccountUpdate();

            evaluateGame = new EvaluateGame(); // Game evaluation fragment

            evaluateGame.setOpponent(MatchAPI.get().getCurrentDatabaseMatch().getOpponentUserName());

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
            if(AccountAPI.get().getCurrentAccount() != null ) {
                evaluateGame.setInitialPoints(AccountAPI.get().getCurrentAccount().getElo_rating());
                ChallengeAPI.get().deleteChallenge();
            } else {
                evaluateGame.setInitialPoints(0);
            }

            ChallengeAPI.get().setNotify(true);

            AccountAPI.get().getAccount();
//            goToMainActivity();
            MatchAPI.get().removeCurrentMatch();
            evaluateGame.show(getSupportFragmentManager(), "EvaluateGame"); // Span Up Ad
        }
    }

    private void storeGameOnCloud(){
        if(matchableAccount != null && boardView.getLocalAlliance().equals(Alliance.WHITE) && AccountAPI.get().getCurrentUser() != null){
            MatchAPI.get().storeCurrentMatchOnCloud(PGNMainUtils.writeGameAsPGN(boardView.getMoveLog().convertToEngineMoveLog(),
                    AccountAPI.get().getCurrentUser().getUser_name(),
                    MatchAPI.get().getCurrentDatabaseMatch().getOpponentUserName(), "*"),
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

            drawable.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.DST_OVER);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(35, 35);

            imageView.setLayoutParams(params);
            imageView.setBackground(drawable);
            imageView.invalidate();

            if (piece.getPieceAlliance().equals(getOpponentAlliance())) {
                ownerPieces.addView(imageView);
            } else if (piece.getPieceAlliance().equals(getOwnerAlliance())) {
                opponentPieces.addView(imageView);
            }
        }
    }

    @Override
    public void onRemoteMoveMade(RemoteMove remoteMove) {
        runOnUiThread(() -> {
            checkIfOpponentIsOnline(remoteMove);
            boardView.translateRemoteMoveOnBoard(remoteMove);
            noMoveReactor.clearNoMoveSeconds();
            noMoveReactor.setHasOpponentMoved(true);
            noMoveReactor.setPgn(boardView.getPortableGameNotation()); // Set current game status
        });
    }

    /**
     * See if opponent disconnected;
     * @param remoteMove Remote move from opponent
     */
    public void checkIfOpponentIsOnline(RemoteMove remoteMove){
        if(remoteMove.isLastEventDisconnected()){
            Toasty.warning(this,R.string.opponent_disconnected, Toasty.LENGTH_LONG).show();
            noMoveReactor.setOpponentDisconnected(true);
        }
    }

    public void stopNoMoveReactor(){
        if(this.noMoveReactor != null){
            this.noMoveReactor.stopTimer();
        }
    }

    protected void storeGameAsPGN(String result) {
        // Ignore game storage if game is a puzzle
        if(boardView.getMode().equals(BoardView.Modes.PUZZLE_MODE)){
            return;
        }
        isGameFinished = true; // Is game on going is false
        MoveLog moveLog = boardView.getMoveLog();
        String gameText = PGNMainUtils.writeGameAsPGN(moveLog.convertToEngineMoveLog(), txtOwner.getText().toString(), txtOpponent.getText().toString(), result);
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
                case POOR:
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
                case EXCELLENT:
                    notifyGoodInternetConnection();
                    imgConnectionStatus.setImageDrawable(getResources().getDrawable(R.drawable.high_connection));
                    break;
            }
        });
    }

    private void notifyLowInternetConnection(){
        if(matchableAccount != null){
            isActiveConnectionFlag = false;
            sendDisconnectedEvent();
            boardView.setEnabled(false);
            if(noMoveReactor != null){
                noMoveReactor.setDisconnected(true);
            }
        }
    }

    private void notifyGoodInternetConnection() {
        if (matchableAccount != null) {
            boardView.setEnabled(true);
            sendOnlineEvent();
            if(noMoveReactor != null){
                noMoveReactor.setDisconnected(false);
            }
            // If the connection was previously changed then notify that you are back
            if(!isActiveConnectionFlag){
                PresenceAPI.get().setUserOnline(AccountAPI.get().getCurrentUser());
                isActiveConnectionFlag = true;
            }
        }
    }

    @Override
    public void onGetECO(ECOBuilder.ECO eco) {
        runOnUiThread(() -> Toast.makeText(this, eco.getOpening(), Toast.LENGTH_LONG).show());
    }

    @Override
    public void onMatchEnd(MatchStatus matchStatus) {
        // Stop service
        this.noMoveReactor.stopTimer();
        ChallengeAPI.get().setOnChallenge(false);
        this.stopService(new Intent(this, MatchService.class));
        MatchAPI.get().setMatchCreated(false);
        PresenceAPI.get().stopListening();
        storeGameOnCloud();
        ChallengeAPI.get().deleteChallenge(); // Delete current challenge
        ChallengeAPI.get().setNotify(true);
        isGameFinished = true; // Flag Game Has Ended
        evaluateGame = new EvaluateGame();
        this.stopGameTimers();
        AccountAPI.get().listenToAccountUpdate();
        evaluateGame.setInitialPoints(AccountAPI.get().getCurrentAccount().getElo_rating());
        evaluateGame.setMatchStatus(matchStatus);
        if(!isSavedState){
            evaluateGame.show(getSupportFragmentManager(), "EvaluateGame");
        }
    }

    /**
     * Ensure we do not get null pointers when stopping timers
      */
    private void stopGameTimers(){
        if(GameTimer.get() != null) {
            GameTimer.get().stopAllTimers();
        }
    }

    @Override
    public void onOpponentReceived(User user) {
        PresenceAPI.get().getUserOnline(user, this); // Listen to user disconnection
        runOnUiThread(() -> {
            txtOwner.setText(AccountAPI.get().getFirebaseUser().getDisplayName());
            txtOpponent.setText(user.getUser_name());
        });
    }


    private void sendDisconnectedEvent(){
        RemoteMove.get().addEvent(MatchEvent.DISCONNECTED);
        RemoteMove.get().setOwner(matchableAccount.getOwner());
        RemoteMove.get().send(matchableAccount.getMatchId(),matchableAccount.getSelf());
    }

    /**
     * If user was disconnected notify opponent user is back online
     */
    private void sendOnlineEvent(){
        if(RemoteMove.get().isLastEventDisconnected()){
            RemoteMove.get().addEvent(MatchEvent.ONLINE);
            RemoteMove.get().setOwner(matchableAccount.getOwner());
            RemoteMove.get().send(matchableAccount.getMatchId(),matchableAccount.getSelf());
        }
    }

    @Override
    public void onUserOnline(User user, boolean isOnline) {
        // If Listening to another user and the user is offline. Notify me
        if(!AccountAPI.get().isUser(user.getUid()) && !isOnline){
            Toasty.info(this, user.getUser_name() + " is offline").show();
        }
    }

    /**
     * @author Collins Magondu 10/01/2020
     * Used to accept challenges from notifications
     */
    private class ChallengeTaker implements MatchListener, AccountListener  {
        private String challengeId;

        ChallengeTaker(String challengeId){
            this.challengeId = challengeId;
            MatchAPI.get().setMatchListener(this);
            MatchAPI.get().setMatchCreated(true);
            MatchAPI.get().getAccount();
        }

        void acceptChallenge(){
            if(AccountAPI.get().getCurrentUser() == null) {
                AccountAPI.get().getAccount(user.getUid(), account -> {
                    AccountAPI.get().setCurrentAccount(account);
                    AccountAPI.get().setUser(FirebaseAuth.getInstance().getCurrentUser());
                    createMatchableAccount(account);
                    AccountAPI.get().setAccountListener(this);
                    AccountAPI.get().getAccount();
                    AccountAPI.get().getUserByUid(account.getOwner());
                });
            } else {
                createMatchableAccount(AccountAPI.get().getCurrentAccount());
            }
        }

        private void createMatchableAccount(Account account){
            MatchableAccount matchableAccount = new MatchableAccount();
            matchableAccount.setOwner(account.getOwner());
            matchableAccount.setMatch_type(MatchType.PLAY_ONLINE.toString());
            matchableAccount.setDuration(Constants.DEFAULT_MATCH_DURATION);
            matchableAccount.setElo_rating(account.getElo_rating());
            MatchAPI.get().createUserMatchableAccountImplementation(matchableAccount);
        }

        @Override
        public void onMatchMade(MatchableAccount matchableAccount) {
            challengeProgressDialog.dismiss(); // Close challenge acceptance
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

        @Override
        public void onAccountReceived(Account account) {
            // Handle Account Received Logic
            Log.d(BoardActivity.class.getSimpleName(), "Account Received");
        }

        @Override
        public void onUserReceived(User user) {
            // Handle User Received Logic
            Log.d(BoardActivity.class.getSimpleName(), "User Received");
        }

        @Override
        public void onAccountUpdated(boolean status) {
            // Handle Account Updated Logic
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

    @Override
    public void onDisconnected(MatchStatus matchStatus) {
        noMoveReactor.stopTimer();
        onMatchEnd(matchStatus);
    }
}
