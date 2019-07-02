package chessbet.adapter;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import chessbet.utils.GameTimer;

public class GameDurationAdapter extends BaseAdapter {
    private List<GameTimer.GameDuration> gameDurations;
    private Context context;
    public GameDurationAdapter(Context context){
        this.context = context;
        gameDurations = new ArrayList<>();
        gameDurations.add(GameTimer.GameDuration.TEN_MINUTES);
        gameDurations.add(GameTimer.GameDuration.FIFTEEN_MINUTES);
        gameDurations.add(GameTimer.GameDuration.TWENTY_MINUTES);
        gameDurations.add(GameTimer.GameDuration.THIRTY_MINUTES);
    }
    @Override
    public int getCount() {
        return gameDurations.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Button timeDuration = new Button(context);
        timeDuration.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        timeDuration.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        timeDuration.setText(gameDurations.get(position).toString());
        timeDuration.setOnClickListener(v->{
            Log.d("CLICKED",gameDurations.get(position).toString());
        });
        return timeDuration;
    }
}
