package chessengine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import chessbet.app.com.R;
public class Cell extends View {
    private Matrix matrix;
    private RectF mSrcRectF;
    private RectF mDestRectF;
    private static final String TAG = Cell.class.getSimpleName();
    private  int color;
    private final int col;
    private final int row;

    private final Paint squareColor;
    private Rect tileRect;
    private Context context;
    private Bitmap bitmap;
    private Component component;
    private boolean touched=false;
    Cell(int row, int column,Context context) {
        super(context);
        this.col = column;
        this.row = row;
        this.context=context;
        this.squareColor = new Paint();
        this.matrix=new Matrix();
        this.mDestRectF=new RectF();
        this.mSrcRectF=new RectF();
    }

    public void draw(final Canvas canvas) {
        super.draw(canvas);
        Drawable drawable;

        if(component.getResourceId()!=0){
            drawable= this.context.getResources().getDrawable(component.getResourceId());
        }
        else{
            drawable=this.context.getResources().getDrawable(R.drawable.select);
            drawable.setAlpha(0);
        }
        if(this.touched){
            drawable.setColorFilter(Color.YELLOW, PorterDuff.Mode.DST_OVER);
            this.touched=!this.touched;
        }
        else{
            drawable.setColorFilter(color,PorterDuff.Mode.DST_OVER);
        }

        drawable.setBounds(tileRect);
        drawable.draw(canvas);
        bitmap = ((BitmapDrawable) drawable).getBitmap();
        mSrcRectF.set(0, 0, bitmap.getWidth(), bitmap.getHeight());

        mDestRectF.set(0, 0, getWidth(),getHeight());
        matrix.setRectToRect(mSrcRectF, mDestRectF, Matrix.ScaleToFit.CENTER);
        matrix.postRotate(90);
        canvas.drawBitmap(bitmap, matrix, squareColor);
        invalidate();
    }

    private String getColumnString() {
        switch (col) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            case 4:
                return "E";
            case 5:
                return "F";
            case 6:
                return "G";
            case 7:
                return "H";
            default:
                return null;
        }
    }

    private String getRowString() {
        return String.valueOf(row + 1);
    }

    public void handleTouch() {
        this.touched =true;
    }
    public boolean isTouched(final int x, final int y) {
        return tileRect.contains(x, y);
    }

    public void setTileRect(final Rect tileRect) {
        this.tileRect = tileRect;
    }

    public String toString() {
        final String column = getColumnString();
        final String row = getRowString();
        return "<Tile " + column + row + ">";
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
