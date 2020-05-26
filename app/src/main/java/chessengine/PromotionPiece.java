package chessengine;

import androidx.annotation.NonNull;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class PromotionPiece {
    private Alliance alliance;
    private Piece.PieceType pieceType;

    private PromotionPiece(Alliance alliance, Piece.PieceType pieceType) {
        this.alliance = alliance;
        this.pieceType = pieceType;
    }

    public Piece.PieceType getPieceType() {
        return pieceType;
    }

    @NonNull
    @Override
    public String toString() {
        return (this.pieceType.toString().concat(alliance.toString())).toLowerCase();
    }

    public static List<PromotionPiece> getPromotionPieces(Alliance alliance) {
        final List<PromotionPiece> promotionPieces = new ArrayList<>();
        promotionPieces.add(new PromotionPiece(alliance, Piece.PieceType.QUEEN));
        promotionPieces.add(new PromotionPiece(alliance, Piece.PieceType.KNIGHT));
        promotionPieces.add(new PromotionPiece(alliance, Piece.PieceType.BISHOP));
        promotionPieces.add(new PromotionPiece(alliance, Piece.PieceType.ROOK));
        return promotionPieces;
    }
}
