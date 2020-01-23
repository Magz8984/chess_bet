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
import chessbet.api.MatchAPI;
import chessbet.app.com.R;
import chessbet.domain.Constants;
import chessbet.domain.User;

public class OnlineUsersAdapter extends FirebaseRecyclerAdapter<User, OnlineUsersAdapter.ViewHolder> {
    private Context context;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options Adapter options
     */
    public OnlineUsersAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User user) {
        Glide.with(this.context).load(user.getProfile_photo_url() == null ||
                user.getProfile_photo_url().equals("") ?
                Constants.UTILITY_PROFILE : user.getProfile_photo_url()).into(holder.getImageView());
        holder.getTxtEmail().setText(user.getEmail());
        holder.getTxtUserName().setText(user.getUser_name());
        // Make sure you cannot challenge yourself
        if(user.getUid().equals(AccountAPI.get().getCurrentUser().getUid())){
            holder.getBtnChallenge().setEnabled(false);
        } else {
            holder.getBtnChallenge().setEnabled(true);
        }

        holder.getBtnChallenge().setOnClickListener(view -> {
            holder.getBtnChallenge().setEnabled(false); // Disable button
            Toast.makeText(context, "Sending a challenge to " + user.getUser_name(), Toast.LENGTH_LONG).show();
            if(!ChallengeAPI.get().isCurrentChallengeValid()){
                ChallengeAPI.get().challengeAccount(user.getUid(), new ChallengeAPI.ChallengeSent() {
                    @Override
                    public void onChallengeSent() {
                        holder.getBtnChallenge().setEnabled(true);
                        MatchAPI.get().setMatchCreated(true);
                        MatchAPI.get().getAccount(); // Listener for challenge acceptance
                        ChallengeAPI.get().setLastChallengedUser(user);
                        Toast.makeText(context, "Challenge sent to " + user.getUser_name(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onChallengeNotSent() {
                        holder.getBtnChallenge().setEnabled(true);
                        Toast.makeText(context, "Challenge not sent", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                // If the current challenge is valid no need for another challenge
                holder.getBtnChallenge().setEnabled(true);
                Toast.makeText(context, "Your current challenge is still valid", Toast.LENGTH_LONG).show();
            }
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
        private TextView txtEmail;
        private Button btnChallenge;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            imageView = itemView.findViewById(R.id.imgProfilePic);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            btnChallenge = itemView.findViewById(R.id.btnChallenge);
        }

        Button getBtnChallenge() {
            return btnChallenge;
        }

        ImageView getImageView() {
            return imageView;
        }

        TextView getTxtEmail() {
            return txtEmail;
        }

        TextView getTxtUserName() {
            return txtUserName;
        }
    }

}
