package chessbet.app.com;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ColorPickerActivity extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle){
        View view=inflater.inflate(R.layout.board_color_picker,viewGroup,false);

        return  view;
    }
}
