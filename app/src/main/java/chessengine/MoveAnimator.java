package chessengine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * @author Collins Magondu
 */
public class MoveAnimator {
   private final static double ARROW_ANGLE = Math.PI / 6;
   private final static double ARROW_SIZE =25;
   private Rect toRect;
   private Rect fromRect;
   private Canvas canvas;
   private Paint paint;
   private boolean draw;

   private void getPainter(){
       paint = new Paint();
       paint.setStyle(Paint.Style.STROKE);
       paint.setAntiAlias(true);
       paint.setColor(Color.BLUE);
       paint.setStrokeWidth(5);
   }

    void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    void setFromRect(Rect fromRect) {
        this.fromRect = fromRect;
    }

    void setToRect(Rect toRect) {
        this.toRect = toRect;
    }

    public void saveCanvas(){
       canvas.save();
    }

    public void  restoreCanvas(){
       canvas.restore();
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    public boolean isDraw() {
        return draw;
    }

    void dispatchDraw(){
       canvas.save();
       if(draw){
           getPainter();
           drawArrowLines(getCenterPoint(fromRect), getCenterPoint(toRect));
       }
       canvas.restore();
    }

    private void drawArrowLines(Point pointFrom, Point pointTo){
       canvas.drawLine(pointFrom.x, pointFrom.y, pointTo.x, pointTo.y, paint);

       double angle = Math.atan2(pointTo.y - pointFrom.y, pointTo.x - pointFrom.x);

       int arrowX, arrowY;

       arrowX = (int) (pointTo.x - ARROW_SIZE * Math.cos(angle + ARROW_ANGLE));
       arrowY = (int) (pointTo.y - ARROW_SIZE * Math.sin(angle + ARROW_ANGLE));

       canvas.drawLine(pointTo.x, pointTo.y, arrowX, arrowY, paint);

       arrowX = (int) (pointTo.x - ARROW_SIZE * Math.cos(angle - ARROW_ANGLE));
       arrowY = (int) (pointTo.y - ARROW_SIZE * Math.sin(angle - ARROW_ANGLE));

       canvas.drawLine(pointTo.x, pointTo.y, arrowX, arrowY, paint);
    }

    /**
     * @param rect to get the center x y coordinates of the tile
        @return Point
     */
    private Point getCenterPoint(Rect rect){
       Point point = new Point();
       // Center of tile
       point.x = rect.left + (rect.right - rect.left) / 2;
       point.y = rect.top + (rect.bottom - rect.top) / 2;

       return point;
    }


}
