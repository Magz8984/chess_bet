package chessbet.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.chess.engine.Alliance;
import com.chess.engine.Move;

import java.util.List;

import chessbet.app.com.R;
import chessengine.PromotionPiece;
import chessengine.PromotionPieceListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class PawnPromotionAdapter extends BaseAdapter{
    private List<PromotionPiece> promotionPieces;
    private Context context;
    private PromotionPieceListener promotionPieceListener;

    public PawnPromotionAdapter(Alliance alliance, Context context, PromotionPieceListener promotionPieceListener, Move move) {
        this.promotionPieces = PromotionPiece.getPromotionPieces(alliance);
        this.context = context;
        this.promotionPieceListener = promotionPieceListener;
    }

    @Override
    public int getCount() {
        return promotionPieces.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View container, ViewGroup viewGroup) {
        LayoutInflater menuInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View promotionPiece;

        if(container == null){
            assert menuInflater != null;
            promotionPiece = menuInflater.inflate(R.layout.pawn_promotion_image,null);
            CircleImageView circleImageView = promotionPiece.findViewById(R.id.pieceImage);
            Drawable drawable = this.context.getResources().getDrawable(context.getResources()
                    .getIdentifier(this.promotionPieces.get(i).toString(),"drawable", context.getPackageName()));
            circleImageView.setImageDrawable(drawable);
            circleImageView.setOnClickListener(view -> {
                promotionPieceListener.onPromotionPieceTypeSelected(this.promotionPieces.get(i).getPieceType());
            });
        } else {
            promotionPiece = container;
        }
        return promotionPiece;
    }
}
