package chessbet.app.com.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chess.engine.Alliance;
import com.chess.engine.Move;

import chessbet.adapter.PawnPromotionAdapter;
import chessbet.app.com.R;
import chessengine.PromotionPieceListener;

public class PawnPromotionDialog extends DialogFragment {
    private Alliance alliance;
    private PromotionPieceListener promotionPieceListener;

    public PawnPromotionDialog(Alliance alliance, PromotionPieceListener promotionPieceListener) {
        this.alliance = alliance;
        this.promotionPieceListener = promotionPieceListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PawnPromotionAdapter adapter = new PawnPromotionAdapter(alliance, requireContext(), promotionPieceListener);
        View view = inflater.inflate(R.layout.pawn_promotion_dialog, container, false);
        GridView gridView = view.findViewById(R.id.pieces);
        gridView.setAdapter(adapter);
        return view;
    }
}
