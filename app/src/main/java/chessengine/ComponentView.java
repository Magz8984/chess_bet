package chessengine;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.content.res.TypedArray;

import chessbet.app.com.R;


public class ComponentView extends View {
    private int row;
    private int col;
    private String name;
    private int color;
    private Paint componentPaint;
    private TypedArray ats;
    public ComponentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ats=context.getTheme().obtainStyledAttributes(attrs, R.styleable.ComponentView,0,0);
        try{

            name=ats.getString(R.styleable.ComponentView_name);
            row=ats.getInteger(R.styleable.ComponentView_row,0);
            col=ats.getInteger(R.styleable.ComponentView_col,0);
            color=ats.getInteger(R.styleable.ComponentView_componentColor,0);
            componentPaint=new Paint();
        }
        finally {
            ats.recycle();
        }
    }
    @Override
    public void  onDraw(Canvas canvas){
        try {
            float length,width;
            length=45;
            width=45;
            componentPaint.setStyle(Style.FILL);
            componentPaint.setAntiAlias(true);
            componentPaint.setColor(color);
            canvas.drawRect(length,width,0,0,componentPaint);
        }catch (Exception ex){
            Log.d("ERROR MESSAGE",ex.getMessage());
        }


    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
    // Setter Methods
    public void setName(String name) {
        this.name = name;
        invalidate();
        requestLayout();
    }

    public void setRow(int row) {
        this.row = row;
        invalidate();
        requestLayout();
    }

    public void setCol(int col) {
        this.col = col;
        invalidate();
        requestLayout();
    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
        requestLayout();
    }
}
