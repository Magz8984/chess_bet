package chessbet.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import chessbet.app.com.BoardActivity;
import chessbet.app.com.R;
import chessbet.domain.Constants;
import chessbet.domain.Puzzle;
import de.hdodenhof.circleimageview.CircleImageView;

public class PuzzlesAdapter extends FirestoreRecyclerAdapter<Puzzle, PuzzlesAdapter.ViewHolder> {
    private Context context;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PuzzlesAdapter(@NonNull FirestoreRecyclerOptions<Puzzle> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Puzzle model) {
        holder.getTxtTitle().setText(model.getTitle());
        holder.getTxtDescription().setText(model.getDescription());
        holder.getTxtOwner().setText(model.getOwner());
        Glide.with(this.context).asBitmap()
                .load(((model.getOwnerPhotoUrl() == null || model.getOwnerPhotoUrl().equals("")) ? Constants.UTILITY_PROFILE : model.getOwnerPhotoUrl()))
                .into(holder.getImgOwner());
        holder.getTxtView().setOnClickListener(v -> {
            Intent intent = new Intent(context, BoardActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Puzzle", model);
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.puzzel_view, parent, false);
        return new ViewHolder(view);
    }

    /**
     * View Holder For Puzzles
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTitle;
        private TextView txtDescription;
        private CircleImageView imgOwner;
        private TextView txtOwner;
        private TextView txtView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtPuzzleTitle);
            txtDescription = itemView.findViewById(R.id.txtPuzzleDescription);
            imgOwner = itemView.findViewById(R.id.ownerPic);
            txtOwner = itemView.findViewById(R.id.txtOwner);
            txtView = itemView.findViewById(R.id.txtView);
        }

        TextView getTxtDescription() {
            return txtDescription;
        }

        TextView getTxtTitle() {
            return txtTitle;
        }

        CircleImageView getImgOwner() {
            return imgOwner;
        }

        TextView getTxtOwner() {
            return txtOwner;
        }

        public TextView getTxtView() {
            return txtView;
        }
    }

}
