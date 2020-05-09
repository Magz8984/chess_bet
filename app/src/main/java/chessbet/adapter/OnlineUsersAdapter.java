package chessbet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import chessbet.api.AccountAPI;
import chessbet.api.ChallengeAPI;
import chessbet.app.com.R;
import chessbet.domain.Constants;
import chessbet.domain.MatchType;
import chessbet.domain.TargetedChallenge;
import chessbet.domain.User;

public class OnlineUsersAdapter extends FirebaseRecyclerAdapter<User, OnlineUsersAdapter.ViewHolder> {
    private Context context;
    private ChallengeAPI.TargetedChallengeUpdated targetedChallengeListener;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options Adapter options
     */
    public OnlineUsersAdapter(@NonNull FirebaseRecyclerOptions<User> options, ChallengeAPI.TargetedChallengeUpdated targetedChallengeListener) {
        super(options);
        this.targetedChallengeListener = targetedChallengeListener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User user) {
        Glide.with(this.context).load(user.getProfile_photo_url() == null ||
                user.getProfile_photo_url().equals("") ?
                Constants.UTILITY_PROFILE : user.getProfile_photo_url()).into(holder.getImageView());
        holder.getTxtUserName().setText(user.getUser_name());
        // Make sure you cannot challenge yourself
        if(user.getUid().equals(AccountAPI.get().getCurrentUser().getUid())){
            holder.getBtnChallenge().setEnabled(false);
        } else {
            holder.getBtnChallenge().setEnabled(true);
        }

        holder.getBtnChallenge().setOnClickListener(view -> {
            holder.getBtnChallenge().setEnabled(false);
            TargetedChallenge targetedChallenge = TargetedChallenge.targetChallengeFactory(AccountAPI.get().getCurrentUser().getUid(),
                    AccountAPI.get().getCurrentUser().getUser_name(), user.getUid(), user.getUser_name(), MatchType.PLAY_ONLINE);
            Toast.makeText(context, "Sending Challenge", Toast.LENGTH_LONG).show();
            ChallengeAPI.get().sendTargetedChallengeImplementation(targetedChallenge, targetedChallengeListener);
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_view, parent, false);
        return new ViewHolder(view);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtUserName;
        private ImageView imageView;
        private Button btnChallenge;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            imageView = itemView.findViewById(R.id.imgProfilePic);
            btnChallenge = itemView.findViewById(R.id.btnChallenge);
        }

        Button getBtnChallenge() {
            return btnChallenge;
        }

        ImageView getImageView() {
            return imageView;
        }

        TextView getTxtUserName() {
            return txtUserName;
        }
    }

}
