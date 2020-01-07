package chessbet.app.com;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chess.engine.ECOBuilder;
import com.chess.engine.Move;
import com.chess.engine.board.Board;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.Player;
import com.chess.pgn.FenUtilities;
import com.chess.pgn.PGNMainUtils;
import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessengine.BoardView;
import chessengine.ECOBook;
import chessengine.GameUtil;
import chessengine.MoveLog;
import chessengine.OnMoveDoneListener;
import stockfish.InternalStockFishHandler;

public class GameAnalysisActivity extends AppCompatActivity implements
        OnMoveDoneListener, ECOBook.OnGetECOListener, View.OnClickListener, BoardView.EngineResponse {
    @BindView(R.id.board) BoardView boardView;
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.btnForward) Button btnForward;
    @BindView(R.id.moves) LinearLayout movesLog;
    @BindView(R.id.txtGameStatus) TextView txtGameStatus;
    @BindView(R.id.txtBookMove) TextView txtBookMove;
    @BindView(R.id.chart) LineChart lineChart;
    @BindView(R.id.txtResponse) TextView txtResponse;


    private int moveCursor = 0; // Move Log Cursor
    private com.chess.gui.MoveLog importMoveLog; // Move Log From PGN String
    private InternalStockFishHandler internalStockFishHandler;


    private List<Entry> whiteEntries = new ArrayList<>();
    private List<Entry> blackEntries = new ArrayList<>();

    private int size = 1;
    private String mateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_analysis);
        ButterKnife.bind(this);
        boardView.setDarkCellsColor(Color.rgb(240, 217, 181));
        boardView.setWhiteCellsColor(Color.rgb(181, 136, 99));
        boardView.setOnMoveDoneListener(this);
        boardView.setEngineResponse(this);
        boardView.setEcoBookListener(this);
        btnBack.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        internalStockFishHandler = new InternalStockFishHandler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String pgn = intent.getStringExtra("pgn");

        if(pgn != null){
            importMoveLog = createMoveLogFromPGN(pgn);
            if(importMoveLog != null){
                // Flag does not accept user moves but move log moves
                boardView.setMode(BoardView.Modes.ANALYSIS);
                handleAnalysis();
            } else {
                // Log exception
                Crashlytics.logException(new RuntimeException("Wrong PGN Format : " + pgn));
            }
        }


        boardView.requestHint();
        Description description = new Description();
        description.setText("Position Advantage");

        lineChart.setDescription(description);

        GameUtil.initialize(R.raw.chess_move, this);
    }

    @Override
    public void getMoves(MoveLog movelog) {
        runOnUiThread(() -> {
            movesLog.removeAllViews();
            for(Move move : movelog.getMoves()){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 0, 10, 0);
                TextView textView = new TextView(this);
                textView.setLayoutParams(params);
                textView.setText(move.toString());
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                textView.setOnClickListener(v -> Log.d("MOVE", move.toString()));
                movesLog.addView(textView);
            }
        });
    }

    @Override
    public void isCheckMate(Player player) {
        runOnUiThread(() -> {
            txtGameStatus.setText(String.format(Locale.US, "%s %s", player.toString(), getResources().getString(R.string.checkmate)));
            boardView.requestHint();
        });
    }

    @Override
    public void isStaleMate(Player player) {
        runOnUiThread(() -> txtGameStatus.setText(getResources().getString(R.string.stalemate)));
    }

    @Override
    public void isCheck(Player player) {
        runOnUiThread(() -> txtGameStatus.setText(String.format(Locale.US, "%s %s", player.toString(), getResources().getString(R.string.check))));
    }

    @Override
    public void isDraw() {
        runOnUiThread(() -> txtGameStatus.setText(getResources().getString(R.string.draw)));
    }

    @Override
    public void onGameResume() {
        runOnUiThread(() -> txtGameStatus.setText(""));
    }

    @Override
    public void onGetECO(ECOBuilder.ECO eco) {
        runOnUiThread(() -> txtBookMove.setText(eco.getOpening()));
    }

    @ Override
    public void onClick(View view) {
        if(view.equals(btnBack)){
            boardView.undoMove();
        } else if(view.equals(btnForward)){
            boardView.redoMove();
        }
    }

    @Override
    public void onEngineResponse(String response) {
        Log.d(GameAnalysisActivity.class.getSimpleName(), response);
        runOnUiThread(() -> {
            // Ensure the first score is plot on graph
            if(size == boardView.getMoveLog().size()){
                return;
            }

            size = boardView.getMoveLog().size();
            float centiPawnEval = getCentiPawnEvaluation(response);

            if(centiPawnEval == -1000f){
                txtResponse.setText(mateString);
                return;
            }

            Entry entry = new Entry(size, centiPawnEval);

            if(boardView.getCurrentPlayer().getAlliance().isWhite()){
                whiteEntries.add(entry);
            } else {
                blackEntries.add(entry);
            }

            LineDataSet whiteDataSet = new LineDataSet(whiteEntries, "White");
            LineDataSet blackDataSet = new LineDataSet(blackEntries, "Black");

            whiteDataSet.setCircleColor(Color.WHITE);
            whiteDataSet.setColor(Color.WHITE);

            blackDataSet.setCircleColor(Color.BLACK);
            blackDataSet.setColor(Color.BLACK);

            List<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(whiteDataSet);
            dataSets.add(blackDataSet);


            lineChart.removeAllViews();
            LineData data = new LineData(dataSets);

            lineChart.setData(data);
            lineChart.invalidate();
        });
    }

    private float getCentiPawnEvaluation(String response){
        List<String> segments = Arrays.asList(response.split(" "));
        int indexOfCP = segments.indexOf("cp");
        try {
            int result = Integer.parseInt(segments.get(indexOfCP + 1));
            Log.d(GameAnalysisActivity.class.getSimpleName(), result + " " + boardView.getFen());
            return Math.abs(result); // Get Integer
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if(segments.contains("mate")){
            mateString =  "mate in " + Math.abs(Integer.parseInt(segments.get(segments.indexOf("mate") + 1)));
        }
        return -1000f;
    }

    /**
     * Create A MoveLog List From PGN String
     * @param pgn game pgn string
     * @return Invalid move logs should not be returned
     */
    private com.chess.gui.MoveLog createMoveLogFromPGN(String pgn){
        Board board = Board.createStandardBoard();
        com.chess.gui.MoveLog moveLog = new com.chess.gui.MoveLog();
        List<String> strMoves = PGNMainUtils.processMoveText(pgn);
        for(String strMove: strMoves){
            Move move = PGNMainUtils.createMove(board, strMove);
            MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                moveLog.addMove(move);
                board = moveTransition.getTransitionBoard();
            } else {
                return null;
            }
        }
        return moveLog;
    }

    private void handleAnalysis(){
        new Thread(() -> {
            while (moveCursor < importMoveLog.getMoves().size()){
                try {
                    internalStockFishHandler.askStockFishMove(FenUtilities.createFEN(boardView.getChessBoard()), 1000, 20);
                    Thread.sleep(1000);
                    Move move = importMoveLog.getMoves().get(moveCursor);

                    // TODO Handle Logic In BoardView
                    MoveTransition moveTransition = boardView.getChessBoard().currentPlayer().makeMove(move);
                    if(moveTransition.getMoveStatus().isDone()){
                        boardView.getMoveLog().addMove(move);
                        boardView.setChessBoard(moveTransition.getTransitionBoard());
                        boardView.highlightDestinationTile(move.getDestinationCoordinate());
                        boardView.updateEcoView();
                        boardView.updateMoveView();
                        boardView.displayGameStates();
                        boardView.postInvalidate();
                    }
                    moveCursor++;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
       }).start();
    }
}