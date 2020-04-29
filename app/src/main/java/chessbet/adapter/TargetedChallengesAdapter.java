package chessbet.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import chessbet.api.ChallengeAPI;
import chessbet.api.MatchAPI;
import chessbet.app.com.BoardActivity;
import chessbet.app.com.R;
import chessbet.domain.MatchableAccount;
import chessbet.domain.TargetedChallenge;
import chessbet.services.MatchListener;
import chessbet.utils.DatabaseUtil;

public class TargetedChallengesAdapter extends FirestoreRecyclerAdapter<TargetedChallenge, TargetedChallengesAdapter.ViewHolder> {
    private Context context;
    private boolean isMyChallenges;
    private ProgressDialog progressDialog;
    private ChallengeAPI.TargetedChallengeUpdated targetedChallengeListener;
    public TargetedChallengesAdapter(@NonNull FirestoreRecyclerOptions<TargetedChallenge> options, Context context, boolean isMyChallenges ,ChallengeAPI.TargetedChallengeUpdated targetedChallengeListener) {
        super(options);
        this.isMyChallenges = isMyChallenges;
        this.context = context;
        progressDialog = new ProgressDialog(context);
        this.targetedChallengeListener = targetedChallengeListener;
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

        if(isMyChallenges) {
            holder.getBtnAccept().setVisibility(View.VISIBLE);
            holder.getBtnPlay().setVisibility(View.GONE);
        } else {
            holder.getBtnAccept().setVisibility(View.GONE);
            holder.getBtnPlay().setVisibility(View.VISIBLE);
        }

        holder.getBtnPlay().setOnClickListener(view -> acceptChallenge(model));
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
