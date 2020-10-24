package chessbet.app.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import chessbet.app.com.R;
import chessbet.domain.Challenge;
import de.hdodenhof.circleimageview.CircleImageView;

public class NewChallengesAdapter  extends FirestoreRecyclerAdapter<Challenge, NewChallengesAdapter.ViewHolder>{
    private Context context;

    public NewChallengesAdapter(@NonNull FirestoreRecyclerOptions<Challenge> options, Context context) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Challenge challenge) {
        viewHolder.txtAmount.setText(challenge.getAmount().toString());
        Glide.with(context).load(challenge.getPhotoUrl()).into(viewHolder.imgProfilePhoto);

        if(challenge.isAccepted()) {
            viewHolder.btnAccept.setEnabled(false);
            viewHolder.txtStatus.setText("Paired");
            viewHolder.txtStatus.setTextColor(context.getResources().getColor(R.color.bootstrap_red));
        } else {
            viewHolder.btnAccept.setEnabled(true);
            viewHolder.txtStatus.setText("Free");
            viewHolder.txtStatus.setTextColor(context.getResources().getColor(R.color.bootstrap_green));
        }

        viewHolder.btnAccept.setOnClickListener(view -> {
            // Accept  challenge and wait for setUp
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_challenge_view, viewGroup, false);
        return new NewChallengesAdapter.ViewHolder(view);
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
