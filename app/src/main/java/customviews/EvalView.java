package customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import chessbet.app.com.R;

/**
 * @author Collins Magondu 28/12/19
 */

public class EvalView extends View {
    private Paint textPaint;
    private String points = "599(+9) 608";
    private String gameResult = "Match Won";
    private String winnerInfo = "Collins Won By CheckMate";
    private Typeface typeface;

    private void initialize(){
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);
        typeface = Typeface.createFromAsset(getContext().getAssets(), "viewfonts/chakrapetchbold.ttf");
    }

    /**
     * Convert density pixels to pixels
     * @see #onDraw(Canvas)
     * @param dp
     * @return
     */
    private int getTextFromDpToPx(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    public EvalView(Context context) {
        super(context);
        initialize();
    }

    public EvalView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        initialize();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        textPaint.setTypeface(typeface);
        textPaint.setTextSize(getTextFromDpToPx(20));
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(winnerInfo,width/2f , height/4f, textPaint);
        textPaint.setTextSize(getTextFromDpToPx(15));
        canvas.drawText(points, width/2f, height/4f + 75, textPaint); // Game Result
        textPaint.setTextSize(getTextFromDpToPx(20));
        textPaint.setColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
        canvas.drawText(gameResult, width/2f, height/4f + 150, textPaint); // Winner
    }

    public void setPoints(String points) {
        this.points = points;
        invalidate();
    }

    public void setGameResult(String gameResult) {
        this.gameResult = gameResult;
        invalidate();
    }

    public String getGameResult() {
        return gameResult;
    }

    public void setWinnerInfo(String winnerInfo) {
        this.winnerInfo = winnerInfo;
    }

    public String getWinnerInfo() {
        return winnerInfo;
    }

    public String getPoints() {
        return points;
    }
}
