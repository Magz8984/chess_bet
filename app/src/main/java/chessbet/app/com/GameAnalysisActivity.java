package chessbet.app.com;

import androidx.appcompat.app.AppCompatActivity;

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
import com.chess.engine.player.Player;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        boardView.requestHint();
        Description description = new Description();
        description.setText("Centipawn Analysis");
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
        txtGameStatus.setText(player.toString() + " Checkmated");
        boardView.requestHint();
    }

    @Override
    public void isStaleMate(Player player) {
        txtGameStatus.setText("StaleMate");
    }

    @Override
    public void isCheck(Player player) {
        txtGameStatus.setText(player.toString() + " Check");
    }

    @Override
    public void isDraw() {
        txtGameStatus.setText("Draw");
    }

    @Override
    public void onGameResume() {
        txtGameStatus.setText("");
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
            float centipawnEval = getCentiPawnEvaluation(response);

            if(centipawnEval == -1000f){
                txtResponse.setText(mateString);
                return;
            }

            Entry entry = new Entry(size, centipawnEval);

            if(boardView.getCurrentPlayer().getAlliance().isWhite()){
                blackEntries.add(entry);
            } else {
                whiteEntries.add(entry);
            }

            LineDataSet whiteDataSet = new LineDataSet(whiteEntries, "White");
            LineDataSet blackDataSet = new LineDataSet(blackEntries, "Black");

            whiteDataSet.setCircleColor(Color.BLACK);
            whiteDataSet.setColor(Color.BLACK);

            blackDataSet.setCircleColor(Color.WHITE);
            blackDataSet.setColor(Color.WHITE);

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
            return (result / 100f); // Get Integer
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if(segments.contains("mate")){
            mateString =  "mate in " + Math.abs(Integer.parseInt(segments.get(segments.indexOf("mate") + 1)));
        }
        return -1000f;
    }
}