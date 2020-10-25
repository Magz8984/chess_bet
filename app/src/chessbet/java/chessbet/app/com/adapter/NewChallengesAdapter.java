package chessbet.app.com.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Locale;

import chessbet.api.AccountAPI;
import chessbet.api.ChallengeAPI;
import chessbet.api.MatchAPI;
import chessbet.app.com.BoardActivity;
import chessbet.app.com.R;
import chessbet.domain.AcceptChallengeDTO;
import chessbet.domain.Challenge;
import chessbet.domain.MatchableAccount;
import chessbet.services.MatchListener;
import chessbet.services.MatchService;
import chessbet.utils.DatabaseUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class NewChallengesAdapter  extends FirestoreRecyclerAdapter<Challenge, NewChallengesAdapter.ViewHolder> implements ChallengeAPI.ChallengeHandler, MatchListener {
    private Context context;
    private ProgressDialog progressDialog;

    public NewChallengesAdapter(@NonNull FirestoreRecyclerOptions<Challenge> options, Context context) {
        super(options);
        this.context = context;
        ChallengeAPI.get().setChallengeHandler(this);
        MatchAPI.get().setMatchListener(this);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Accepting Challenge");
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Challenge challenge) {
        Log.d(NewChallengesAdapter.class.getSimpleName(), challenge.getFcmToken());
        viewHolder.txtUserName.setText(challenge.getUserName());
        viewHolder.txtAmount.setText(String.format(Locale.ENGLISH, "%s %.2f", challenge.getCurrency(), challenge.getAmount()));
        Glide.with(context).load(challenge.getPhotoUrl()).into(viewHolder.imgProfilePhoto);

        if(challenge.getOwner().equals(AccountAPI.get().getFirebaseUser().getUid())) {
            viewHolder.btnAccept.setEnabled(false);
            viewHolder.txtStatus.setText("Self Challenge");
            viewHolder.txtStatus.setTextColor(context.getResources().getColor(R.color.bootstrap_red));
        } else {
            if(challenge.isAccepted()) {
                viewHolder.btnAccept.setEnabled(false);
                viewHolder.txtStatus.setText("Paired");
                viewHolder.txtStatus.setTextColor(context.getResources().getColor(R.color.bootstrap_red));
            } else {
                viewHolder.btnAccept.setEnabled(true);
                viewHolder.txtStatus.setText("Free");
                viewHolder.txtStatus.setTextColor(context.getResources().getColor(R.color.bootstrap_green));
            }
        }
        viewHolder.btnAccept.setOnClickListener(view -> {
            // Accept  challenge and wait for setUp
            progressDialog.show();
            AcceptChallengeDTO acceptChallengeDTO = new AcceptChallengeDTO(AccountAPI.get().getFirebaseUser().getUid(), challenge.getOwner());
            ChallengeAPI.get().acceptBetChallengeImplementation(acceptChallengeDTO);
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_challenge_view, viewGroup, false);
        return new NewChallengesAdapter.ViewHolder(view);
    }

    @Override
    public void challengeSent(String id) {
    }

    @Override
    public void challengeFound(String response) {
        progressDialog.dismiss();
        MatchAPI.get().getAccount();
    }

    @Override
    public void challengeNotFound() {
        progressDialog.dismiss();
        Toasty.error(context, "An error occurred while accepting challenge").show();
    }

    @Override
    public void onMatchMade(MatchableAccount matchableAccount) {
        try {
            ChallengeAPI.get().setOnChallenge(true);
            context.startService(new Intent(context, MatchService.class));
            new Handler().postDelayed(() -> {
                Intent target= new Intent(context, BoardActivity.class);
                target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle bundle = new Bundle();
                bundle.putParcelable(DatabaseUtil.matchables,matchableAccount);
                target.putExtras(bundle);
                context.startActivity(target);
            },3000);
        } catch (Exception ex ){
            Crashlytics.logException(ex);
        }
    }

    @Override
    public void onMatchableCreatedNotification() {

    }

    @Override
    public void onMatchError() {
        Toasty.info(context, "Still setting up match").show();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgProfilePhoto;
        private TextView txtUserName;
        private TextView txtAmount;
        private TextView txtStatus;
        private Button btnAccept;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfilePhoto = itemView.findViewById(R.id.imgProfilePhoto);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
        }
    }
}
