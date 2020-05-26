package chessengine;

import com.chess.engine.Move;
import com.chess.engine.pieces.Piece;

public interface PromotionPieceListener {
    void onPromotionPieceTypeSelected(Piece.PieceType pieceType);
    void onPromotionMoveMade(Move move);
}
