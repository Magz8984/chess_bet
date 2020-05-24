package chessbet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chessbet.app.com.R;

public class ComputerSkillLevelAdapter extends BaseAdapter{
    private Context context;
    private List<Integer> levels = new ArrayList<>();
    private View selectedView;
    private SkillLevel skillLevel;
    public ComputerSkillLevelAdapter(Context context, SkillLevel skillLevel){
        this.skillLevel = skillLevel;
        this.context = context;
        for( int i = 1; i <= 20; i++) {
            levels.add(i);
        }
        Collections.sort(levels);
    }


    @Override
    public int getCount() {
        return levels.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    // Re initialize view to avoid te cached position from being displayed
    private void initializeView(int position, View view) {
        TextView txtLevel = view.findViewById(R.id.txtLevel);
        String data = Integer.toString(levels.get(position));
        txtLevel.setText(data);
        view.setOnClickListener(view1 -> {
            if(selectedView != null) {
                selectedView.setBackgroundColor(this.context.getResources().getColor(R.color.white));
            }
            selectedView = view;
            view.setBackgroundColor(this.context.getResources().getColor(R.color.colorPrimaryYellow));
            skillLevel.setSkillLevel(levels.get(position));
        });
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View skillView;

        if(view == null ) {
            skillView = layoutInflater.inflate(R.layout.computer_skill_level_view, null);
        } else {
            skillView = view;
        }
        initializeView(i, skillView);
        return skillView;
    }

    public interface SkillLevel{
        void setSkillLevel(int skillLevel);
    }
}
