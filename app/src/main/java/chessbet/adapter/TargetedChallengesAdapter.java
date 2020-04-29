package chessbet.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import chessbet.api.AccountAPI;
import chessbet.api.ChallengeAPI;
import chessbet.app.com.R;
import chessbet.domain.TargetedChallenge;
import chessbet.domain.User;

public class TargetedChallengesAdapter extends FirestoreRecyclerAdapter<TargetedChallenge, TargetedChallengesAdapter.ViewHolder> {
    private Context context;
    private ChallengeAPI.TargetedChallengeUpdated targetedChallengeListener;
    private User user;
    public TargetedChallengesAdapter(@NonNull FirestoreRecyclerOptions<TargetedChallenge> options, Context context, ChallengeAPI.TargetedChallengeUpdated targetedChallengeListener) {
        super(options);
        this.context = context;
        this.targetedChallengeListener = targetedChallengeListener;
        this.user = AccountAPI.get().getCurrentUser();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull TargetedChallenge model) {
        Log.d(TargetedChallengesAdapter.class.getSimpleName(), "Here");
        holder.getTxtOwner().setText(model.getOwnerName());
        holder.getTxtDateCrated().setText(model.getDateCreated());

        holder.getImgBin().setOnClickListener(view -> ChallengeAPI.get().deleteTargetedChallenge(model.getId(), new ChallengeAPI.TargetChallengeUpdated() {
            @Override
            public void onUpdate() {
                Toast.makeText(context, "Challenge Deleted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onUpdateError() {
                Toast.makeText(context, "Challenge Deletion Error", Toast.LENGTH_LONG).show();
            }
        }));

        if(model.getTarget().equals(this.user.getUid())) {
            holder.getBtnAccept().setVisibility(View.VISIBLE);
            holder.getBtnPlay().setVisibility(View.GONE);
        } else {
            holder.getBtnAccept().setVisibility(View.GONE);
            holder.getBtnPlay().setVisibility(View.VISIBLE);
        }

        if(model.isAccepted()) {
            holder.getBtnAccept().setVisibility(View.GONE);
        } else {
            holder.getBtnPlay().setVisibility(View.GONE);
        }

        holder.getBtnPlay().setOnClickListener(view -> {
            ChallengeAPI.get().deleteTargetedChallenge(model.getId(), new ChallengeAPI.TargetChallengeUpdated() {
                @Override
                public void onUpdate() {
                    acceptChallenge(model);
                }

                @Override
                public void onUpdateError() {
                    Toast.makeText(context, "Challenge Deletion Error", Toast.LENGTH_LONG).show();
                }
            });
            acceptChallenge(model);
        });
        holder.getBtnAccept().setOnClickListener(view -> acceptChallenge(model));
    }


    private void acceptChallenge(TargetedChallenge challenge) {
        ChallengeAPI.get().acceptTargetedChallengeImplementation(challenge, targetedChallengeListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.target_challenge_view, parent, false);
        return new TargetedChallengesAdapter.ViewHolder(view);
    }

    /**
     * View Holder For Targeted Challenges
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtOwner;
        private TextView txtDateCrated;
        private ImageView imgBin;
        private Button btnAccept;
        private Button btnPlay;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDateCrated = itemView.findViewById(R.id.txtDateCreated);
            txtOwner = itemView.findViewById(R.id.txtOwner);
            imgBin = itemView.findViewById(R.id.imgBin);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnPlay = itemView.findViewById(R.id.btnPlay);
        }

        TextView getTxtDateCrated() {
            return txtDateCrated;
        }

        public TextView getTxtOwner() {
            return txtOwner;
        }

        ImageView getImgBin() {
            return imgBin;
        }

        Button getBtnAccept() {
            return btnAccept;
        }

        Button getBtnPlay() {
            return btnPlay;
        }
    }
}
