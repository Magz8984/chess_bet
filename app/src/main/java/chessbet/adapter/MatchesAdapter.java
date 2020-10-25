package chessbet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import chessbet.api.AccountAPI;
import chessbet.app.com.R;
import chessbet.domain.Match;
import chessbet.domain.MatchStatus;
import chessbet.domain.MatchType;
import chessbet.utils.DatabaseUtil;
import chessbet.utils.SQLDatabaseHelper;
import de.hdodenhof.circleimageview.CircleImageView;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder>{
    private List<Match> matches;
    private Context context;

    public MatchesAdapter(Context context){
        this.context = context;
        this.matches = DatabaseUtil.getMatchesFromLocalDB(new SQLDatabaseHelper(context).getMatches());
        matches = AccountAPI.get().assignMatchResults(this.matches);
    }

    public MatchesAdapter(Context context, @NonNull MatchType matchType, List<Match> matches){
        this.context = context;
        this.matches = AccountAPI.get().assignMatchResults(matches);

        // Filter type specific games
        List<Match> filteredMatches = new ArrayList<>();
        for (final Match match: matches) {
            if(match.getMatchType().equals(matchType.toString())){
                filteredMatches.add(match);
            }
        }
        this.matches = filteredMatches;
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
            Match match = matches.get(position);
            Glide.with(context).asBitmap().load(match.getOpponentPic()).into(holder.getImgOpponentPic());
            holder.getOpponentUserName().setText(match.getOpponentUserName());
            // TODO Hack. Change this
            holder.getTxtMatchStatus().setText((match.getMatchStatus() == MatchStatus.GAME_ABORTED ? "ABORT": match.getMatchStatus().toString()));

            if(match.getAmount() != null) {
                holder.getTxtAmount().setText(String.format(Locale.ENGLISH,"%.2f", match.getAmount().getAmount() / 2));
                holder.getTxtCurrency().setText(match.getAmount().getCurrency());
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imgOpponentPic;
        private TextView txtMatchStatus;
        private TextView opponentUserName;
        private TextView txtCurrency;
        private TextView txtAmount;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgOpponentPic = itemView.findViewById(R.id.imgOpponentPic);
            txtMatchStatus = itemView.findViewById(R.id.txtResult);
            opponentUserName = itemView.findViewById(R.id.txtOpponentName);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtCurrency = itemView.findViewById(R.id.txtCurrency);
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

        public TextView getTxtAmount() {
            return txtAmount;
        }

        public TextView getTxtCurrency() {
            return txtCurrency;
        }
    }
}
