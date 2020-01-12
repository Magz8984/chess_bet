package chessbet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import chessbet.api.AccountAPI;
import chessbet.app.com.R;
import chessbet.domain.DatabaseMatch;
import chessbet.utils.DatabaseUtil;
import chessbet.utils.SQLDatabaseHelper;
import de.hdodenhof.circleimageview.CircleImageView;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder>{
    private List<DatabaseMatch> databaseMatches;
    private Context context;

    public MatchesAdapter(Context context){
        this.context = context;
        this.databaseMatches = DatabaseUtil.getMatchesFromLocalDB(new SQLDatabaseHelper(context).getMatches());
        databaseMatches = AccountAPI.get().assignMatchResults(this.databaseMatches);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try{
            Glide.with(context).asBitmap().load(databaseMatches.get(position).getOpponentPic()).into(holder.getImgOpponentPic());
            holder.getOpponentUserName().setText(databaseMatches.get(position).getOpponentUserName());
            holder.getTxtMatchStatus().setText(databaseMatches.get(position).getMatchStatus().toString());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return databaseMatches.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imgOpponentPic;
        private TextView txtMatchStatus;
        private TextView opponentUserName;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgOpponentPic = itemView.findViewById(R.id.imgOpponentPic);
            txtMatchStatus = itemView.findViewById(R.id.txtResult);
            opponentUserName = itemView.findViewById(R.id.txtOpponentName);
        }
         TextView getTxtMatchStatus() {
            return txtMatchStatus;
        }

        CircleImageView getImgOpponentPic() {
            return imgOpponentPic;
        }

        TextView getOpponentUserName() {
            return opponentUserName;
        }
    }
}
