package chessengine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;

import chessbet.app.com.R;

public class BoardView extends View {
    private final String place[]={"r","n","b","k","q","b","n","r"};
    private final int row =8;
    private final int column =8;
    private Context context;
    private  int dark;
    private  int white;
    // X and Y coordinates
    private int x0=0;
    private int y0=0;
    private int square_size =0;

    public  boolean isFlipped=false;
    private Cell[][] cells;
    private Component[][] components;
    private Game game;
    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public  BoardView(Context context){
        super(context);
        this.context=context;
        cells=new Cell[row][column]; // Initialize Cell Size
        components=new Component[row][column];
        setFocusable(true);
        game=new Game();
        buildCells(); // Create an instance of the cell;
    }

    private  void buildCells(){
        for(int i=0;i<row;i++){
            for(int j=0;j<column;j++){
                cells[i][j]= new Cell(i,j,this.context);
                components[i][j]=new Component();
                if(i==0){
                    components[i][j].setCol(j);
                    components[i][j].setRow(i);
                    components[i][j].setType(place[j].concat("b"));
                    components[i][j].setResourceId(Component.resID(components[i][j].getType()));
                }
                else if(i==1){
                    components[i][j].setCol(j);
                    components[i][j].setRow(i);
                    components[i][j].setType("pb");
                    components[i][j].setResourceId(Component.resID(components[i][j].getType()));
                }
                else if(i==6){
                    components[i][j].setCol(j);
                    components[i][j].setRow(i);
                    components[i][j].setType("pw");
                    components[i][j].setResourceId(Component.resID(components[i][j].getType()));
                }
                else if(i==7){
                    components[i][j].setCol(i);
                    components[i][j].setRow(j);
                    components[i][j].setType(place[j].concat("w"));
                    components[i][j].setResourceId(Component.resID(components[i][j].getType()));
                }
                else{
                    components[i][j].setCol(j);
                    components[i][j].setRow(i);
                    components[i][j].setType("0");
                }
                cells[i][j].setComponent(components[i][j]); //Sets component
            }
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean moveMade;
        final int x = (int) event.getX();
        final int y = (int) event.getY();

        Cell cell;
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < column; c++) {
                cell = cells[r][c];
                if (cell.isTouched(x, y)){
                    cell.handleTouch();
                    game.assignGame(cell);
                }
            }
        }
        invalidate();
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
                cells[r][c].setTileRect(tileRect);
                if((r+c+2) % 2 == 0){
                    if(dark==0 && white==0){
                        cells[r][c].setColor(Color.CYAN);
                    }
                    else{
                        cells[r][c].setColor(dark);
                    }
                }
                else {
                    if(dark==0 && white==0){
                        cells[r][c].setColor(Color.WHITE);
                    }
                    else{
                        cells[r][c].setColor(white);
                    }
                }
                cells[r][c].draw(canvas);
            }
        }

    }

    private int getCellWidth(int width){
        return  (width /8);
    }

    private int getCellHeight(int height){
        return  (height /8);
    }

    private int getXCoord(final int x) {
        return x0 + square_size * (isFlipped ? 7 - x : x);
    }


    private int getYCoord(final int y) {
        return y0 + square_size * (isFlipped ? y : 7 - y);
    }
    public void setIsFilpped(boolean value){ // FLips;
        this.isFlipped=value;
        requestLayout();
        invalidate();
    }

    public void setWhite(int white) {
        this.white = white;
        invalidate();
    }

    public void setDark(int dark) {
        this.dark = dark;
        invalidate();
    }
}

class Cell extends View{
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
            drawable.setAlpha(10);
        }
        if(this.touched){
            drawable.setColorFilter(Color.YELLOW,PorterDuff.Mode.DST_OVER);
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

    public void setTouchedFalse(){
        this.touched=false;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

class Component{ // Holds Component Data;
    private  String  type;
    private int row;
    private  int col;
    private  int resourceId;

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setResourceId(int reSourceId) {
        this.resourceId = reSourceId;
    }

    public void setType(String  type) {
        this.type = type;
    }

    public int getCol() {
        return col;
    }

    public int getResourceId() {
        return resourceId;
    }

    public String  getType() {
        return type;
    }

    public int getRow() {
        return row;
    }

    public  static int resID(String c){
        switch (c){
            case "rb":
                return R.drawable.rb;
            case "nb":
                return R.drawable.nb;
            case "bb":
                return R.drawable.bb;
            case "kb":
                return R.drawable.kb;
            case "qb":
                return R.drawable.qb;
            case "pb":
                return R.drawable.pb;
            case "rw":
                return R.drawable.rw;
            case "nw":
                return R.drawable.nw;
            case "bw":
                return R.drawable.bw;
            case "kw":
                return R.drawable.kw;
            case "qw":
                return R.drawable.qw;
            case "pw":
                return R.drawable.pw;
            default:
                return 0;
        }
    }
}