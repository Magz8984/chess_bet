package chessengine;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;

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

    public void moveBack(){

    }
    public void moveForward(){

    }
}


