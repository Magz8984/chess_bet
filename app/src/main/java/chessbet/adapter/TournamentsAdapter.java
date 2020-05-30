package chessbet.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import chessbet.app.com.R;
import chessbet.models.Amount;
import chessbet.models.Players;
import chessbet.models.Teams;
import chessbet.models.Tournaments;

public class TournamentsAdapter extends RecyclerView.Adapter<TournamentsAdapter.MyHolder>{
    Context context;
    List<Tournaments> tournamentsList;

    public TournamentsAdapter(Context context, List<Tournaments> tournamentsList) {
        this.context = context;
        this.tournamentsList = tournamentsList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate row_tournaments.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_tournament, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        Amount amount = tournamentsList.get(position).getAmount();
        String amt = amount.getAmount();
        String authorUid = tournamentsList.get(position).getAuthorUid();
        String id = tournamentsList.get(position).getId();
        Boolean isLocked = tournamentsList.get(position).getLocked();
        int matchDuration = tournamentsList.get(position).getMatchDuration();
        String name = tournamentsList.get(position).getName();
        int numbeOfRoundsScheduled = tournamentsList.get(position).getNumbeOfRoundsScheduled();
        String paringAlgorithm = tournamentsList.get(position).getParingAlgorithm();
        Players players = tournamentsList.get(position).getPlayers();
        String rounds = tournamentsList.get(position).getRounds();
        Teams teams = tournamentsList.get(position).getTeams();
        int timeStamp = tournamentsList.get(position).getTimeStamp();
        String typeOfTournament = tournamentsList.get(position).getTypeOfTournament();

        //Converting timeStamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(String.valueOf(timeStamp)));
        String date_created = DateFormat.format("dd/MM/yyyy  hh:mm aa", calendar).toString();

        //set data to views
        holder.nameTv.setText(name);
        holder.dateTv.setText(date_created);
        holder.roundsTv.setText(rounds);
        holder.durationTv.setText(matchDuration);
        holder.typeTv.setText(typeOfTournament);
        holder.amountTv.setText(amt);
       // holder.playersTv.setText();


    }

    @Override
    public int getItemCount() {
        return tournamentsList.size();
    }

    // view holder class
    class MyHolder extends RecyclerView.ViewHolder{
        //views from row_tournament.xml
        TextView nameTv;
        TextView dateTv;
        TextView roundsTv;
        TextView durationTv;
        TextView typeTv;
        TextView amountTv;
        TextView playersTv;
        Button joinBtn;
        Button viewBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            roundsTv = itemView.findViewById(R.id.roundsTv);
            durationTv = itemView.findViewById(R.id.durationTv);
            typeTv = itemView.findViewById(R.id.typeTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            playersTv = itemView.findViewById(R.id.playersTv);
            joinBtn = itemView.findViewById(R.id.joinBtn);
            viewBtn = itemView.findViewById(R.id.viewBtn);
        }
    }
}
