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

import java.util.ArrayList;
import java.util.List;

import chessbet.api.AccountAPI;
import chessbet.api.ChallengeAPI;
import chessbet.api.MatchAPI;
import chessbet.app.com.R;
import chessbet.domain.Constants;
import chessbet.domain.User;

public class SearchFriendAdapter extends RecyclerView.Adapter<SearchFriendAdapter.ViewHolder> {
    private Context context;
    private List<User> users = new ArrayList<>();

    public SearchFriendAdapter(Context context){
        this.context = context;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_view ,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       Glide.with(this.context).load((users.get(position).getProfile_photo_url() == null ||
               users.get(position).getProfile_photo_url().equals("")) ?
               Constants.UTILITY_PROFILE : users.get(position).getProfile_photo_url())
               .into(holder.getImageView());

       holder.getTxtUserName().setText(users.get(position).getUser_name());
       holder.getTxtEmail().setText(users.get(position).getEmail());

       // Make sure you cannot challenge yourself
        if(users.get(position).getUid().equals(AccountAPI.get().getCurrentUser().getUid())){
            holder.getBtnChallenge().setEnabled(false);
        } else {
            holder.getBtnChallenge().setEnabled(true);
        }

       // Challenge creation process
       holder.getBtnChallenge().setOnClickListener(view -> {
           holder.getBtnChallenge().setEnabled(false);
               if(!ChallengeAPI.get().isCurrentChallengeValid()){
                   Toast.makeText(context, "Sending a challenge to " + users.get(position).getUser_name(), Toast.LENGTH_LONG).show();
                   ChallengeAPI.get().challengeAccount(users.get(position).getUid(), new ChallengeAPI.ChallengeSent() {
                       @Override
                       public void onChallengeSent() {
                           MatchAPI.get().setMatchCreated(true);
                           holder.getBtnChallenge().setEnabled(true);
                           MatchAPI.get().getAccount(); // Listener for challenge acceptance
                           ChallengeAPI.get().setLastChallengedUser(users.get(position));
                           Toast.makeText(context, "Challenge sent to " + users.get(position).getUser_name(), Toast.LENGTH_LONG).show();
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

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtUserName;
        private ImageView imageView;
        private TextView txtEmail;
        private Button btnChallenge;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            imageView = itemView.findViewById(R.id.imgProfilePic);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            btnChallenge = itemView.findViewById(R.id.btnChallenge);
        }

        ImageView getImageView() {
            return imageView;
        }

        TextView getTxtUserName() {
            return txtUserName;
        }

        TextView getTxtEmail() {
            return txtEmail;
        }

        Button getBtnChallenge() {
            return btnChallenge;
        }
    }
}
