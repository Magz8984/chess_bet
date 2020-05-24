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
import chessbet.app.com.R;
import chessbet.domain.Constants;
import chessbet.domain.MatchType;
import chessbet.domain.TargetedChallenge;
import chessbet.domain.User;

public class SearchFriendAdapter extends RecyclerView.Adapter<SearchFriendAdapter.ViewHolder> {
    private Context context;
    private List<User> users = new ArrayList<>();
    private ChallengeAPI.TargetedChallengeUpdated targetChallengeListener;

    public SearchFriendAdapter(Context context, ChallengeAPI.TargetedChallengeUpdated targetChallengeListener){
        this.context = context;
        this.targetChallengeListener = targetChallengeListener;
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
        User user = users.get(position);
       // Make sure you cannot challenge yourself
        if(user.getUid().equals(AccountAPI.get().getCurrentUser().getUid())){
            holder.getBtnChallenge().setEnabled(false);
        } else {
            holder.getBtnChallenge().setEnabled(true);
        }

       // Challenge creation process
       holder.getBtnChallenge().setOnClickListener(view -> {
           holder.getBtnChallenge().setEnabled(false);
           TargetedChallenge targetedChallenge = TargetedChallenge.targetChallengeFactory(AccountAPI.get().getCurrentUser().getUid(),
               AccountAPI.get().getCurrentUser().getUser_name(), user.getUid(), user.getUser_name(), MatchType.PLAY_ONLINE);
           Toast.makeText(context, "Sending Challenge", Toast.LENGTH_LONG).show();
           ChallengeAPI.get().sendTargetedChallengeImplementation(targetedChallenge,targetChallengeListener);
       });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtUserName;
        private ImageView imageView;
        private Button btnChallenge;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            imageView = itemView.findViewById(R.id.imgProfilePic);
            btnChallenge = itemView.findViewById(R.id.btnChallenge);
        }

        ImageView getImageView() {
            return imageView;
        }

        TextView getTxtUserName() {
            return txtUserName;
        }

        Button getBtnChallenge() {
            return btnChallenge;
        }
    }
}
