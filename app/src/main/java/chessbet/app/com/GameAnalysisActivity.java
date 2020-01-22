package chessbet.app.com;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessengine.BoardView;
import chessengine.ECOBook;
import chessengine.GameUtil;
import chessengine.MoveLog;
import chessengine.OnMoveDoneListener;
import stockfish.InternalStockFishHandler;

public class GameAnalysisActivity extends AppCompatActivity implements
        OnMoveDoneListener, ECOBook.OnGetECOListener, BoardView.EngineResponse, OnChartValueSelectedListener {
    @BindView(R.id.board) BoardView boardView;
    @BindView(R.id.txtBookMove) TextView txtBookMove;
    @BindView(R.id.chart) LineChart lineChart;


    private int moveCursor = 0; // Move Log Cursor
    private com.chess.gui.MoveLog importMoveLog; // Move Log From PGN String
    private InternalStockFishHandler internalStockFishHandler;


    private List<Entry> whiteEntries = new ArrayList<>();
    private List<Entry> blackEntries = new ArrayList<>();

    private volatile int size = 1;

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
                boardView.setEnabled(false);
                handleAnalysis();
            } else {
                // Log exception
                Crashlytics.logException(new RuntimeException("Wrong PGN Format : " + pgn));
            }
        }

        Description description = new Description();
        description.setText("Position Advantage");

        lineChart.setDescription(description);
        lineChart.setOnChartValueSelectedListener(this);

        GameUtil.initialize(R.raw.chess_move, this);
    }

    @Override
    public void getMoves(MoveLog movelog) {
    }

    @Override
    public void isCheckMate(Player player) {
        runOnUiThread(() -> boardView.requestHint());
    }

    @Override
    public void isStaleMate(Player player) {
    }

    @Override
    public void isCheck(Player player) {
    }

    @Override
    public void isDraw() {
    }

    @Override
    public void onGameResume() {
    }

    @Override
    public void onGetECO(ECOBuilder.ECO eco) {
        runOnUiThread(() -> txtBookMove.setText(eco.getOpening()));
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
                return;
            }

            Entry entry = new Entry(size, centiPawnEval);
            if(!boardView.getMoveLog().getMoves().isEmpty()){
                entry.setData(boardView.getMoveLog().getMoves().get(size - 1));
            }

            if(boardView.getCurrentPlayer().getAlliance().isWhite()){
                blackEntries.add(entry);
            } else {
                whiteEntries.add(entry);

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
            return result/100f; // Get Integer
        } catch (Exception ex) {
            ex.printStackTrace();
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
                    size = boardView.getMoveLog().size();
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

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        try{
            if(size == importMoveLog.size()){
                Move move = (Move) e.getData();
                Board board = move.undo();
                board = board.currentPlayer().makeMove(move).getTransitionBoard();
                boardView.setChessBoard(board);
                boardView.invalidate();

            } else {
                Toast.makeText(this, "Cannot change board during analysis", Toast.LENGTH_LONG).show();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected() {

    }
}