package chessbet.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import chessbet.app.com.BoardActivity;
import chessbet.app.com.SettingsActivity;
import chessbet.app.com.R;
import chessbet.domain.MatchType;


public class MenuOptionsAdapter extends BaseAdapter {
    private List<View.OnClickListener> listeners;
    private Context context;
    private List<Integer> drawables;
    private List<String> strings;

    public MenuOptionsAdapter(Context context){
        listeners=new ArrayList<>();
        drawables=new ArrayList<>();
        strings=new ArrayList<>();
        this.context=context;

        // Two Player
        listeners.add(v -> {
            Intent intent=new Intent(context, BoardActivity.class);
            intent.putExtra("match_type", MatchType.TWO_PLAYER.toString());
            context.startActivity(intent);
        });
        drawables.add(R.drawable.two_player);
        strings.add("Two Player");

        // Single Player
        listeners.add(v -> {
            Intent intent=new Intent(context, BoardActivity.class);
            intent.putExtra("match_type", MatchType.SINGLE_PLAYER.toString());
            context.startActivity(intent);
        });
        drawables.add(R.drawable.desktop);
        strings.add("Single Player");

        // Play Online
        listeners.add(v -> {
            Intent intent=new Intent(context, BoardActivity.class);
            intent.putExtra("match_type", MatchType.PLAY_ONLINE.toString());
            context.startActivity(intent);
        });
        drawables.add(R.drawable.play_online);
        strings.add("Play Online");

        // Bet Online
        listeners.add(v->{
            Intent intent=new Intent(context, BoardActivity.class);
            intent.putExtra("match_type", MatchType.BET_ONLINE.toString());
            context.startActivity(intent);
        });
        drawables.add(R.drawable.bet_online);
        strings.add("Bet Online");


        // Settings
        listeners.add(v->{
            Intent intent=new Intent(context, SettingsActivity.class);
            context.startActivity(intent);
        });
        drawables.add(R.drawable.settings);
        strings.add("Settings");

    }
    @Override
    public int getCount() {
        return listeners.size();
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
        LayoutInflater menuInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridMenu=null;

        if(convertView == null){
            gridMenu = menuInflater.inflate(R.layout.menu_view,null);
            ImageView imgMenu =  (ImageView) gridMenu.findViewById(R.id.imgMenu);
            TextView txtMenu = (TextView) gridMenu.findViewById(R.id.txtMenu);
            imgMenu.setImageResource(drawables.get(position));
            txtMenu.setText(strings.get(position));
            gridMenu.setOnClickListener(listeners.get(position));
        }
        else {
            gridMenu = convertView;
        }
        return gridMenu;
    }
}
