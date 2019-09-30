package chessbet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import chessbet.app.com.R;
import chessbet.utils.GameManager;

public class GamesAdapter extends RecyclerView.Adapter<ViewHolder> {
    private List<File> files;

    public GamesAdapter(List<File> files){
        this.files = files;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pgn_game_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String time = files.get(position).getName().replace(GameManager.GAME_FILE_NAME + "_","").replace(".pgn", "");
        holder.getTxtName().setText(GameManager.GAME_FILE_NAME);
        long mills = Long.parseLong(time);
        Date date = new Date(mills);
        DateFormat df = new SimpleDateFormat("dd:MM:yy:HH:mm:ss", Locale.US);
        holder.getTxtTime().setText(df.format(date));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }
}

class ViewHolder extends RecyclerView.ViewHolder{
    private TextView txtName;
    private TextView txtTime;
    ViewHolder(@NonNull View itemView) {
        super(itemView);
        txtName = itemView.findViewById(R.id.txtFileName);
        txtTime = itemView.findViewById(R.id.txtTime);
    }

    TextView getTxtName() {
        return txtName;
    }

    TextView getTxtTime() {
        return txtTime;
    }
}
