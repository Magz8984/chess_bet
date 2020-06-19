package chessbet.adapter;


/**
 * @author Elias Baya
 */

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import chessbet.app.com.R;
import chessbet.domain.Players;
import chessbet.domain.Tournament;

public class TournamentsAdapter extends RecyclerView.Adapter<TournamentsAdapter.MyHolder>{
    Context context;
    private List<Tournament> tournamentsList;

    public TournamentsAdapter(Context context, List<Tournament> tournamentsList) {
        this.context = context;
        this.tournamentsList = tournamentsList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate row_tournaments.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_tournament, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myholder, int i) {
        //get data
        Tournament.Amount amount = tournamentsList.get(i).getAmount();
        String amt = amount.getAmount();
        String authorUid = tournamentsList.get(i).getAuthorUid();
        String id = tournamentsList.get(i).getId();
        Boolean isLocked = tournamentsList.get(i).getLocked();
        int matchDuration = tournamentsList.get(i).getMatchDuration();
        String name = tournamentsList.get(i).getName();
        int numbeOfRoundsScheduled = tournamentsList.get(i).getNumbeOfRoundsScheduled();
        String paringAlgorithm = tournamentsList.get(i).getParingAlgorithm();
        ArrayList<Players> playersArrayList = tournamentsList.get(i).getPlayersArrayList();
        int rounds = tournamentsList.get(i).getRounds();
//        Teams teams = tournamentsList.get(position).getTeams();
        long timeStamp = tournamentsList.get(i).getTimeStamp();
        String typeOfTournament = tournamentsList.get(i).getTypeOfTournament();

        //Converting timeStamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(String.valueOf(timeStamp)));
        String date_created = DateFormat.format("dd/MM/yyyy  hh:mm aa", calendar).toString();

        String dateOfStart = tournamentsList.get(i).getDateOfStart();

        //set data to views
        myholder.nameTv.setText(name);
        myholder.start_dateTv.setText(dateOfStart);
        myholder.roundsTv.setText(String.valueOf(rounds));
        myholder.durationTv.setText(String.valueOf(matchDuration));
        myholder.typeTv.setText(typeOfTournament);
        myholder.amountTv.setText(String.valueOf(amt));

    }

    @Override
    public int getItemCount() {
        return tournamentsList.size();
    }

    // view holder class
    static class MyHolder extends RecyclerView.ViewHolder{
        //views from row_tournament.xml
        TextView nameTv, start_dateTv, roundsTv, durationTv, typeTv, amountTv, playersTv;
        Button joinBtn, viewBtn;
        MyHolder(@NonNull View itemView) {
            super(itemView);

            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            start_dateTv = (TextView)itemView.findViewById(R.id.start_dateTv);
            roundsTv = (TextView)itemView.findViewById(R.id.roundsTv);
            durationTv = (TextView)itemView.findViewById(R.id.durationTv);
            typeTv = (TextView)itemView.findViewById(R.id.typeTv);
            amountTv = (TextView)itemView.findViewById(R.id.amountTv);
            playersTv = (TextView)itemView.findViewById(R.id.playersTv);
            joinBtn = (Button) itemView.findViewById(R.id.joinBtn);
            viewBtn = (Button) itemView.findViewById(R.id.viewBtn);
        }
    }
}
