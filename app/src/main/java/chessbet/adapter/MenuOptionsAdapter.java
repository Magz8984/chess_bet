package chessbet.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import chessbet.app.com.BoardActivity;
import chessbet.app.com.GameAnalysisActivity;
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

        // Watch Game
        listeners.add(view -> Toast.makeText(context, context.getResources().getString(R.string.feature_unavailable), Toast.LENGTH_LONG).show());
        drawables.add(R.drawable.eye);
        strings.add("Watch");

        // Analyze Game
        listeners.add(view -> {
            Intent intent=new Intent(context, GameAnalysisActivity.class);
//            intent.putExtra("match_type", MatchType.SINGLE_PLAYER.toString());
            context.startActivity(intent);
        });
        drawables.add(R.drawable.trend_dark);
        strings.add("Analysis");

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
        View gridMenu;

        if(convertView == null){
            gridMenu = menuInflater.inflate(R.layout.menu_view,null);
            ImageView imgMenu = gridMenu.findViewById(R.id.imgMenu);
            TextView txtMenu = gridMenu.findViewById(R.id.txtMenu);
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
