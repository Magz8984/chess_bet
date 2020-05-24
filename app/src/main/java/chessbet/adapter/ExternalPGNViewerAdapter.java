package chessbet.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.ArrayList;
import java.util.List;

import chessbet.app.com.BoardActivity;
import chessbet.app.com.R;
import chessbet.domain.Constants;
import chessengine.PGNHolder;

/**
 * @author Collins Magondu
 */

public class ExternalPGNViewerAdapter extends Adapter<ExternalPGNViewerAdapter.ViewHolder> {
    private List<PGNHolder> pgnHolders = new ArrayList<>();
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setPgnHolders(List<PGNHolder> pgnHolders) {
        this.pgnHolders = pgnHolders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.external_pgn_game_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PGNHolder pgnHolder = pgnHolders.get(position);
        holder.getTxtDate().setText(pgnHolder.getDate());
        holder.getTxtEvent().setText(pgnHolder.getEvent());
        holder.getTxtView().setOnClickListener(view -> {
            Intent intent = new Intent(context, BoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Constants.WHITE, pgnHolder.getWhite());
            intent.putExtra(Constants.BLACK, pgnHolder.getBlack());
            intent.putExtra(Constants.PGN,pgnHolder.getPgn().trim());
            context.startActivity(intent);

        });
        holder.getTxtPlayers().setText(pgnHolder.getPlayers());
    }

    @Override
    public int getItemCount() {
        return pgnHolders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtPlayers;
        private TextView txtEvent;
        private TextView txtView;
        private TextView txtDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPlayers  = itemView.findViewById(R.id.txtPlayers);
            txtEvent = itemView.findViewById(R.id.txtEvent);
            txtView = itemView.findViewById(R.id.txtView);
            txtDate = itemView.findViewById(R.id.txtDate);
        }

        TextView getTxtDate() {
            return txtDate;
        }

        TextView getTxtEvent() {
            return txtEvent;
        }

        TextView getTxtPlayers() {
            return txtPlayers;
        }

        TextView getTxtView() {
            return txtView;
        }
    }
}
