package chessengine;

import android.content.Context;
import android.view.View;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import 	android.graphics.Color;

public class Cell extends View {
<<<<<<< HEAD
    private int col;
    private int row;
    private boolean isWhite;
    private Drawable drawable;
    private Rect rect;
    Paint paint;

    public Cell(Context contex,AttributeSet atts){
        super(contex,atts);
        paint=new Paint();
        paint.setColor(isWhite() ? Color.WHITE : Color.BLACK); //Set White Or  Black Color;
        paint.setAntiAlias(true);
    }


    @Override
    public void onDraw(final  Canvas canvas){
      canvas.drawRect(rect,paint);
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean white) {
        isWhite = white;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);



        setMeasuredDimension(width, height);
=======
    public Cell(Context context) {
        super(context);
>>>>>>> parent of 2928cc8... Restructure App
    }
}
