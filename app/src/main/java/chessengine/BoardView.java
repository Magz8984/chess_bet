package chessengine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;

import chessbet.app.com.R;


public class BoardView extends View {
    private static String app_name=BoardView.class.getSimpleName();
    private final int row =8;
    private final int column =8;
    private Context context;

    private int x0=0; // X and Y coordinates
    private int y0=0;
    private int square_size =0;

    public  boolean isFlipped=false;
    private Cell[][] cells;

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public  BoardView(Context context){
        super(context);
        this.context=context;
        cells=new Cell[row][column]; // Initialize Cell Size
        setFocusable(true);

        buildCells(); // Create an instance of the cell;
    }

    private  void buildCells(){
        Log.d("INIT","INITIALIZED");
        for(int i=0;i<row;i++){
            for(int j=0;j<column;j++){
                cells[i][j]= new Cell(i,j,this.context);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();

        Cell cell;
        for (int c = 0; c < row; c++) {
            for (int r = 0; r < column; r++) {
                cell = cells[c][r];
                if (cell.isTouched(x, y))
                    cell.handleTouch();
            }
        }

        return true;
    }

    @Override
    protected  void onDraw(Canvas canvas){
        final int width=getWidth();
        final int height=getHeight();

        square_size=Math.min(getCellWidth(width),getCellHeight(height));

        for (int c = 0; c < row; c++) {
            for (int r = 0; r < column; r++) {
                final int xCoord = getXCoord(c);
                final int yCoord = getYCoord(r);

                final Rect tileRect = new Rect(
                        xCoord,               // left
                        yCoord,               // top
                        xCoord + square_size,  // right
                        yCoord + square_size   // bottom
                );

                cells[c][r].setTileRect(tileRect);
                cells[c][r].draw(canvas);
            }
        }

    }

    private int getCellWidth(int width){
        return  (width /8);
    }

    private int getCellHeight(int height){
        return  (height /8);
    }
    private void compute_orign(int width,int height){
        this.x0= (width -square_size) / 2;
        this.y0= (height -square_size) / 2;

    }

    private int getXCoord(final int x) {
        return x0 + square_size * (isFlipped ? 7 - x : x);
    }

    private int getYCoord(final int y) {
        return y0 + square_size * (isFlipped ? y : 7 - y);
    }
}


//
class Cell {
    private static final String TAG = Cell.class.getSimpleName();

    private final int col;
    private final int row;

    private final Paint squareColor;
    private Rect tileRect;
    private Context context;
    private Bitmap bitmap;

    Cell(int row, int column,Context context) {
        this.col = column;
        this.row = row;
        this.context=context;
        this.squareColor = new Paint();
        squareColor.setColor(isDark() ? Color.rgb(240,230,140) : Color.rgb(220,235,235));
    }

    public void draw(final Canvas canvas) {
        Drawable drawable=this.context.getResources().getDrawable(R.drawable.pw);
        drawable.setBounds(tileRect);
        drawable.draw(canvas);;
        canvas.drawRect(tileRect, squareColor);
        bitmap=((BitmapDrawable) drawable).getBitmap();
//        canvas.drawBitmap(bitmap,10,10,squareColor);

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
        // To get the actual row, add 1 since 'row' is 0 indexed.
        return String.valueOf(row + 1);
    }

    public void handleTouch() {
        Log.d(TAG, "handleTouch(): col: " + col);
        Log.d(TAG, "handleTouch(): row: " + row);
    }

    private boolean isDark() {
        return (col + row) % 2 == 0;
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
}
