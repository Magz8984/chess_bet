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

import com.chess.engine.Move;
import com.chess.engine.board.Board;
import com.chess.engine.player.MoveTransition;

import java.util.Collection;
import java.util.Collections;

import chessbet.app.com.R;
public class Cell extends View {
    private final int tileId;
    private BoardView boardView;
    private Matrix matrix;
    private RectF mSrcRectF;
    private RectF mDestRectF;
    private static final String TAG = Cell.class.getSimpleName();
    private  int color;
    private int column =0;
    private int row=0;

    private final Paint squareColor;
    private Rect tileRect;
    private Context context;
    private Bitmap bitmap;
    private boolean touched=false;
    private Drawable drawable;

    Cell(Context context,final BoardView boardView, final int tileId) {
        super(context);
        this.context=context;
        this.tileId=tileId;
        this.boardView = boardView;
        this.squareColor = new Paint();
        this.matrix=new Matrix();
        this.mDestRectF=new RectF();
        this.mSrcRectF=new RectF();
        assignCellColor();
    }

    public void draw(final Canvas canvas) {
        super.draw(canvas);

        assignCellColor();

        if(boardView.chessBoard.getTile(this.tileId).isOccupied()){
            String name = boardView.chessBoard.getTile(tileId).getPiece() +
                    boardView.chessBoard.getTile(tileId).getPiece().getPieceAlliance().toString();
            drawable= this.context.getResources().getDrawable(context.getResources()
                    .getIdentifier( name.toLowerCase(),"drawable", context.getPackageName()));
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

        highlightMoves(boardView.chessBoard, canvas ,color);

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
        switch (column) {
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
            if(boardView.sourceTile == null){
                this.touched = true;
                boardView.sourceTile = boardView.chessBoard.getTile(tileId);
                boardView.movedPiece = boardView.sourceTile.getPiece();

                if(boardView.movedPiece == null){
                    boardView.sourceTile = null;
                }
            }
            else if(boardView.chessBoard.getTile(tileId).getTileCoordinate() ==
                    boardView.sourceTile.getTileCoordinate()){
                boardView.sourceTile = null;
                boardView.movedPiece = null;
                this.touched =false;
            }
            else{
                // Second Click
                final Move move = Move.MoveFactory.createMove(boardView.chessBoard,boardView.movedPiece.getPiecePosition(), tileId);

                final MoveTransition transition = boardView.chessBoard.currentPlayer().makeMove(move);

                if(transition.getMoveStatus().isDone()){
                    boardView.chessBoard= transition.getTransitionBoard();
                    boardView.moveLog.addMove(move);
                    boardView.onMoveDoneListener.getMove(move);

                }

                boardView.sourceTile = null;
                boardView.movedPiece = null;
                boardView.invalidate();
            }
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
        return "<Tile " + tileId + ">";
    }


    private void assignCellColor(){
        if((row + column + 2) % 2 == 0 ){
            this.color=boardView.getDarkCellsColor();
        }
        else{
            this.color=boardView.getWhiteCellsColor();
        }
    }

    public int getTileId() {
        return tileId;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void highlightMoves(final Board board,final Canvas canvas,final int tileColor){
        for(final Move move : pieceLegalMoves(boardView.chessBoard)){
            if(move.getDestinationCoordinate() == this.tileId){
                if(!boardView.chessBoard.getTile(this.tileId).isOccupied()){
                    drawable=this.context.getResources().getDrawable(R.drawable.select_light);
                    drawable.setColorFilter(color,PorterDuff.Mode.DST_OVER);
                    drawable.draw(canvas);
                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                    mSrcRectF.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    mDestRectF.set(0, 0, getWidth(),getHeight());
                    matrix.setRectToRect(mSrcRectF, mDestRectF, Matrix.ScaleToFit.CENTER);
                    canvas.drawBitmap(bitmap, matrix, squareColor);
                    break;
                }
            }
        }
    }

    private Collection<Move> pieceLegalMoves(final Board board){
        if(boardView.movedPiece != null){
            if(boardView.movedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()){
                return  boardView.movedPiece.calculateLegalMoves(board);
            }
        }
       return Collections.emptyList();
    }

}
