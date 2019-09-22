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

import chessbet.app.com.R;
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
        Button btnTimeDuration = new Button(context);
        btnTimeDuration.setBackground(context.getResources().getDrawable(R.drawable.rounded_button));
        btnTimeDuration.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        btnTimeDuration.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btnTimeDuration.setText(gameDurations.get(position).toString());
//        btnTimeDuration.setBackgroundColor(context.getResources().getColor(R.color.white));
        btnTimeDuration.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        btnTimeDuration.setOnClickListener(v-> Log.d("CLICKED",gameDurations.get(position).toString()));
        return btnTimeDuration;
    }
}
