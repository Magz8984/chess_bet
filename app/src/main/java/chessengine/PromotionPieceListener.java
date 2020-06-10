package chessengine;

import com.chess.engine.Move;
import com.chess.engine.pieces.Piece;

/**
 * Implemented by promotion views
 * @see chessbet.app.com.BoardActivity
 */
public interface PromotionPieceListener {
    void onPromotionPieceTypeSelected(Piece.PieceType pieceType);
    void onPromotionMoveMade(Move move);
}
