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

import androidx.annotation.NonNull;

import com.chess.engine.Move;
import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
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
    private  int color;
    private int column =0;
    private int row=0;

    private final Paint squareColor;
    private Rect tileRect;
    private Context context;
    private Bitmap bitmap;
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
        Tile tile = boardView.chessBoard.getTile(this.tileId);
        assignCellColor();
        if(tile.isOccupied()){
            String name = tile.getPiece() +
                    tile.getPiece().getPieceAlliance().toString();
            drawable = this.context.getResources().getDrawable(context.getResources()
                    .getIdentifier( name.toLowerCase(),"drawable", context.getPackageName()));
        }

        else{
            drawable=this.context.getResources().getDrawable(R.drawable.select);
            drawable.setAlpha(0);
        }

        if(boardView.destinationTile != null && boardView.destinationTile.getTileCoordinate() == tile.getTileCoordinate()){
            drawable.setColorFilter(Color.HSVToColor(new float[] {60, 100, 100}), PorterDuff.Mode.DST_OVER);
        }
        else if(boardView.sourceTile != null && boardView.sourceTile.getTileCoordinate() == tile.getTileCoordinate()){
            drawable.setColorFilter(Color.HSVToColor(new float[] {50, 100, 100}), PorterDuff.Mode.DST_OVER);
        }
        else{
            drawable.setColorFilter(color,PorterDuff.Mode.DST_OVER);
        }

        highlightMoves(canvas);

        drawable.setBounds(tileRect);
        drawable.draw(canvas);
        bitmap = ((BitmapDrawable) drawable).getBitmap();

        mSrcRectF.set(0, 0, bitmap.getWidth(), bitmap.getHeight());

        mDestRectF.set(0, 0, getWidth(),getHeight());
        matrix.setRectToRect(mSrcRectF, mDestRectF, Matrix.ScaleToFit.CENTER);
        matrix.preScale(-1.0f,-1.0f);

        canvas.drawBitmap(bitmap, matrix, squareColor);
    }

    public void handleTouch() {
            if(boardView.destinationTile != null){
                boardView.destinationTile = null;
            }

            if(boardView.sourceTile == null){
                boardView.sourceTile = boardView.chessBoard.getTile(tileId);
                boardView.movedPiece = boardView.sourceTile.getPiece();

                if(boardView.movedPiece == null){
                    boardView.sourceTile = null;
                }
                boardView.invalidate(); // Highlights the source tile
            }
            else if(boardView.chessBoard.getTile(tileId).getTileCoordinate() == boardView.sourceTile.getTileCoordinate()){
                boardView.sourceTile = null;
                boardView.movedPiece = null;
                boardView.invalidate(); // Removes the highlight
            }
            else{
                // Second Click
                final Move move = Move.MoveFactory.createMove(boardView.chessBoard,boardView.movedPiece.getPiecePosition(), tileId);

                final MoveTransition transition = boardView.chessBoard.currentPlayer().makeMove(move);

                if(transition.getMoveStatus().isDone()){
                    // Undo a move
                    if(boardView.moveCursor < boardView.moveLog.size()){
                        boardView.moveLog.removeMoves(boardView.moveCursor -1);
                        boardView.onMoveDoneListener.getMoves(boardView.moveLog);
                    }
                    boardView.destinationTile = boardView.chessBoard.getTile(tileId);
                    boardView.setMoveData(boardView.movedPiece.getPiecePosition(), tileId); // Online Play
                    boardView.chessBoard= transition.getTransitionBoard();
                    GameUtil.playSound();  // Play sound once move is made

                    if(boardView.chessBoard.currentPlayer().isInCheckMate()){
                        move.setCheckMateMove(true);
                    }
                    else if(boardView.chessBoard.currentPlayer().isInCheck()){
                        move.setCheckMove(true);
                    }

                    boardView.moveLog.addMove(move);
                    boardView.moveCursor = boardView.moveLog.size();
                    boardView.onMoveDoneListener.getMoves(boardView.moveLog);
                    boardView.onMoveDoneListener.onGameResume();

                    if(boardView.chessBoard.currentPlayer().isInCheckMate()){
                        boardView.onMoveDoneListener.isCheckMate(boardView.chessBoard.currentPlayer());
                    }
                    else if(boardView.chessBoard.currentPlayer().isInCheck()){
                        boardView.onMoveDoneListener.isCheck(boardView.chessBoard.currentPlayer());
                    }
                    else if(boardView.chessBoard.currentPlayer().isInStaleMate()){
                        boardView.onMoveDoneListener.isStaleMate(boardView.chessBoard.currentPlayer());
                    }
                    else if(boardView.chessBoard.isDraw()){
                        boardView.onMoveDoneListener.isDraw();
                    }
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

    @NonNull
    public String toString() {
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

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void highlightMoves(final Canvas canvas){
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
                else{
                    drawable.setColorFilter(Color.RED,PorterDuff.Mode.DST_OVER);
                    drawable.draw(canvas);
                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                    mSrcRectF.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    mDestRectF.set(0, 0, getWidth(),getHeight());
                    matrix.setRectToRect(mSrcRectF, mDestRectF, Matrix.ScaleToFit.CENTER);
                    canvas.drawBitmap(bitmap, matrix, squareColor);
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
