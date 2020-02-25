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

import com.chess.engine.Alliance;
import com.chess.engine.Move;
import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.player.MoveTransition;

import java.util.Collection;
import java.util.Collections;

import chessbet.app.com.R;
import chessbet.domain.Player;
import stockfish.EngineUtil;
import stockfish.Query;
import stockfish.QueryType;

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

        else {
            drawable = this.context.getResources().getDrawable(R.drawable.select);
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

    // TODO deconstruct method to atomic methods
    public void handleTouch() {
            if(boardView.destinationTile != null){
                boardView.clearTiles();
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


                    if(boardView.mode != BoardView.Modes.PUZZLE_MODE && !boardView.isEngineLoading) {
                        // Stop the current player timer
                        if(boardView.gameTimer != null){
                            boardView.gameTimer.stopTimer((boardView.chessBoard.currentPlayer().getAlliance() == Alliance.WHITE) ? Player.WHITE :  Player.BLACK);
                        }

                        // Ask stockfish
                        boardView.chessBoard = transition.getTransitionBoard();

                        // Only ask for a move form stockfish if engine is on
//                        if(boardView.isHinting){
//                            boardView.getInternalStockFishHandler().askStockFishMove(FenUtilities.createFEN(boardView.chessBoard), 3000, 10);
//                        }

                        GameUtil.playSound();  // Play sound once move is made

                        boardView.setMoveCheckOrMate(move);

                        if(boardView.isHinting){
                            boardView.askStockFishBestMove();
                        }

                        boardView.moveLog.addMove(move);
                        boardView.updateEcoView();

                        boardView.addMoveToPuzzle(move);
                        boardView.moveCursor = boardView.moveLog.size();
                        boardView.onMoveDoneListener.getMoves(boardView.moveLog);
                        boardView.onMoveDoneListener.onGameResume();
                    }
                    else if (boardView.mode == BoardView.Modes.PUZZLE_MODE){
                        // Check Move Validity
                        puzzleModeAction(move);
                    }

                    if(boardView.mode == BoardView.Modes.PLAY_COMPUTER && boardView.chessBoard.currentPlayer().getAlliance() == Alliance.BLACK){
                        boardView.isEngineLoading = true;
                            Query query = new Query.Builder()
                                    .setQueryType(QueryType.BEST_MOVE)
                                    .setDepth(EngineUtil.getDepthFromSkillLevel())
                                    .setFen(boardView.getFen())
                                    .setThreads(4)
                                    .setTime(3000).build();
                            EngineUtil.submit(query, response -> {
                            Move sMove = boardView.getMoveByPositions(response.get(0));
                            MoveTransition moveTransition = boardView.chessBoard.currentPlayer().makeMove(sMove);
                            if(moveTransition.getMoveStatus().isDone()){
                                boardView.clearTiles();
                                boardView.destinationTile =  boardView.chessBoard.getTile(sMove.getDestinationCoordinate());
                                boardView.sourceTile = boardView.chessBoard.getTile(sMove.getCurrentCoordinate());
                                boardView.chessBoard = moveTransition.getTransitionBoard();
                                GameUtil.playSound();
                                updateGameStatus();
                                boardView.setMoveCheckOrMate(sMove);
                                boardView.moveLog.addMove(sMove);
                                boardView.moveCursor = boardView.moveLog.size();
                                boardView.onMoveDoneListener.getMoves(boardView.moveLog);
                                boardView.isEngineLoading = false;
                                boardView.postInvalidate();
                            }

                        });
                    }
                    updateGameStatus();
                } else {
                    boardView.clearTiles();
                }
                boardView.invalidate();
            }
    }

    private void updateGameStatus(){
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
        } else {
            boardView.onMoveDoneListener.onGameResume();
        }
    }

    private void puzzleModeAction(Move move){
        if(boardView.puzzleMoveCounter < boardView.getPuzzle().getMoves().size() && isCorrectPuzzleMove(move)){
            boardView.puzzleMove.onCorrectMoveMade(true);
            boardView.puzzleMoveCounter++;
            invalidate();

            if(boardView.puzzleMoveCounter < boardView.getPuzzle().getMoves().size()){
                    new Thread(() -> {
                        try {
                            // Wait for a second before the next move is made
                            Thread.sleep(1000);
                            boardView.clearTiles();
                            Move nextMove = Move.MoveFactory.createMove(boardView.chessBoard ,boardView.getPuzzle().getMoves().get(boardView.puzzleMoveCounter).getFromCoordinate(),
                                    boardView.getPuzzle().getMoves().get(boardView.puzzleMoveCounter).getToCoordinate());
                            boardView.destinationTile = boardView.chessBoard.getTile(tileId);
                            boardView.nextPuzzleMove = nextMove;
                            boardView.chessBoard = boardView.chessBoard.currentPlayer().makeMove(nextMove).getTransitionBoard();
                            boardView.destinationTile = boardView.chessBoard.getTile(nextMove.getDestinationCoordinate());
                            boardView.sourceTile = boardView.chessBoard.getTile(nextMove.getCurrentCoordinate());
                            boardView.puzzleMoveCounter++;
                            // Ensure board positions are redone
                            boardView.postInvalidate();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
            }
            else{
                boardView.setMode(BoardView.Modes.LOCAL_PLAY);
                boardView.puzzleMove.onPuzzleFinished();
            }

            GameUtil.playSound();
            boardView.moveCursor = boardView.moveLog.size();
            boardView.onMoveDoneListener.getMoves(boardView.moveLog);
            boardView.onMoveDoneListener.onGameResume();
        } else {
            boardView.puzzleMove.onCorrectMoveMade(false);
        }
    }

    private boolean isCorrectPuzzleMove(Move move){
        Board board;
        if(boardView.getPuzzle().getMoves().get(boardView.puzzleMoveCounter).getToCoordinate() == move.getDestinationCoordinate()){
            if(boardView.getPuzzle().getMoves().get(boardView.puzzleMoveCounter).getFromCoordinate() == move.getCurrentCoordinate()){
                board = boardView.chessBoard.currentPlayer().makeMove(move).getTransitionBoard();
                if (board.currentPlayer().isInCheckMate()) {
                    move.setCheckMateMove(true);
                } else if (board.currentPlayer().isInCheck()) {
                    move.setCheckMove(true);
                }
                boolean correct = move.toString().equals(boardView.getPuzzle().getMoves().get(boardView.puzzleMoveCounter).getMoveCoordinates());
                if(correct){
                    boardView.chessBoard = board;
                    if(boardView.nextPuzzleMove != null){
                        boardView.moveLog.addMove(boardView.nextPuzzleMove);
                        boardView.nextPuzzleMove = null;
                    }
                    boardView.moveLog.addMove(move);
                    return true;
                }
            }
        }
        return false;
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
            this.color = boardView.getDarkCellsColor();
        }
        else{
            this.color = boardView.getWhiteCellsColor();
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
