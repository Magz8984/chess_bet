package chessengine;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;

import com.chess.engine.Alliance;
import com.chess.engine.Move;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.Player;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import chessbet.api.MatchAPI;
import chessbet.domain.MatchableAccount;
import chessbet.domain.RemoteMove;
import chessbet.services.RemoteMoveListener;

public class BoardView extends View implements RemoteMoveListener {
    protected Board chessBoard;
    protected Tile sourceTile;
    protected Piece movedPiece;
    private final List<Cell> boardCells;
    private BoardDirection boardDirection;
    private int squareSize = 0;
    private boolean isFlipped = false;
    private int whiteCellsColor;
    private int darkCellsColor;
    protected MoveLog moveLog;
    protected OnMoveDoneListener onMoveDoneListener;
    private MatchableAccount matchableAccount;
    private MatchAPI matchAPI;
    private Alliance localAlliance;

    public  BoardView(Context context){
        super(context);
        moveLog= new MoveLog();
        chessBoard= Board.createStandardBoard();
        boardCells=new ArrayList<>();
        boardDirection = BoardDirection.REVERSE;
        for (int i=0 ; i < BoardUtils.NUMBER_OF_TILES; i++){
            final  Cell cell = new Cell(context,this,i);
            this.boardCells.add(cell);
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            for (int i = 0; i < BoardUtils.NUMBER_OF_TILES; i++) {
                if(boardCells.get(i).isTouched(x,y)){
                        if(matchableAccount != null  && chessBoard.getTile(i).isOccupied()){
                            if(chessBoard.getTile(i).getPiece().getPieceAlliance() == localAlliance){
                                boardCells.get(i).handleTouch();
                            }
                        }
                        else {
                            boardCells.get(i).handleTouch();
                        }
                }
            }
            invalidate();
        }

        return true;
    }

    @Override
    protected  void onDraw(Canvas canvas){
        int i = 0;
        final int width=getWidth();
        final int height=getHeight();

        squareSize=Math.min(getCellWidth(width),getCellHeight(height));

        for (int c = 0; c < BoardUtils.NUMBER_OF_TILES_PER_ROW; c++) {
            for (int r = 0; r < BoardUtils.NUMBER_OF_TILES_PER_ROW; r++) {
                final int xCoord = getXCoord(r);
                final int yCoord = getYCoord(c);

                final Rect tileRect = new Rect(
                        xCoord,
                        yCoord,
                        xCoord + squareSize,
                        yCoord + squareSize
                );
                boardDirection.traverse(boardCells).get(i).setTileRect(tileRect);
                boardDirection.traverse(boardCells).get(i).setColumn(c);
                boardDirection.traverse(boardCells).get(i).setRow(r);
                boardDirection.traverse(boardCells).get(i).draw(canvas);
                i++;
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
        int x0 = 0;
        return x0 + squareSize * (isFlipped ? x : 7 - x);
    }

    private int getYCoord(final int y) {
        int y0 = 0;
        return y0 + squareSize * (isFlipped ? y : 7 - y);
    }

    public void moveBack(){

    }

    public void moveForward(){

    }

    public void setDarkCellsColor(int dark) {
        this.darkCellsColor= dark;
        invalidate();
    }

    public void setWhiteCellsColor(int white) {
        this.whiteCellsColor= white;
        invalidate();
    }

    public int getWhiteCellsColor() {
        return whiteCellsColor;
    }

    public int getDarkCellsColor() {
        return darkCellsColor;
    }

    public void setMoveData(int from, int to) {
        if(matchAPI != null){
            matchAPI.sendMoveData(matchableAccount,from,to);
        }
    }

    @Override
    public void onRemoteMoveMade(RemoteMove remoteMove) {
      this.translateRemoteMoveOnBoard(remoteMove);
    }

    private enum BoardDirection {
        REVERSE{
            @Override
            List<Cell> traverse(final List<Cell> boardCells){
                return Lists.reverse(boardCells);
            }

            @Override
            BoardDirection opposite(){
                return NORMAL;
            }

        },NORMAL{
            @Override
            List<Cell> traverse(final List<Cell> boardCells){
                return boardCells;
            }

            @Override
            BoardDirection opposite(){
                return REVERSE;
            }
        };

        abstract BoardDirection opposite();
        abstract List<Cell> traverse(final List<Cell> boardCells);
    }

    public void flipBoardDirection() {
        this.boardDirection = boardDirection.opposite();
        invalidate();
    }

    public void  setOnMoveDoneListener(final OnMoveDoneListener onMoveDoneListener){
        this.onMoveDoneListener = onMoveDoneListener;
    }

    public void setMatchableAccount(MatchableAccount matchableAccount) {
        this.matchableAccount = matchableAccount;
        matchAPI = new MatchAPI();
        matchAPI.setRemoteMoveListener(this);
        matchAPI.getRemoteMoveData(matchableAccount);
        if(matchableAccount.getOpponent().equals("WHITE")){
            this.localAlliance = Alliance.BLACK;
        }
        else if(matchableAccount.getOpponent().equals("BLACK")){
            this.localAlliance = Alliance.WHITE;
        }
    }

    public Player getCurrentPlayer(){
        return chessBoard.currentPlayer();
    }
    private void translateRemoteMoveOnBoard(RemoteMove remoteMove){
        if(remoteMove!=null){
            final Move move = Move.MoveFactory.createMove(chessBoard,remoteMove.from,remoteMove.to);
            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
            if(transition.getMoveStatus().isDone()){
                chessBoard = transition.getTransitionBoard();
                moveLog.addMove(move);
                onMoveDoneListener.getMove(move);


                if(chessBoard.currentPlayer().isInCheck()){
                    onMoveDoneListener.isCheck(chessBoard.currentPlayer());
                }

                if(chessBoard.currentPlayer().isInStaleMate()){
                    onMoveDoneListener.isStaleMate(chessBoard.currentPlayer());
                }

                if(chessBoard.currentPlayer().isInCheckMate()){
                    onMoveDoneListener.isCheckMate(chessBoard.currentPlayer());
                }
                invalidate();
            }
        }
    }
}


