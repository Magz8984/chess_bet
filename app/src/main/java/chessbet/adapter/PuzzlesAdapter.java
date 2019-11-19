package chessbet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import chessbet.app.com.R;
import chessbet.domain.Puzzle;

public class PuzzlesAdapter extends FirestoreRecyclerAdapter<Puzzle, PuzzlesAdapter.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PuzzlesAdapter(@NonNull FirestoreRecyclerOptions<Puzzle> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Puzzle model) {
        holder.getTxtTitle().setText(model.getTitle());
        holder.getTxtDescription().setText(model.getDescription());
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtPuzzleTitle);
            txtDescription = itemView.findViewById(R.id.txtPuzzleDescription);
        }

        public TextView getTxtDescription() {
            return txtDescription;
        }

        public TextView getTxtTitle() {
            return txtTitle;
        }
    }

}
