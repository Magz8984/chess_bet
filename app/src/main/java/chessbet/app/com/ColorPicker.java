package chessbet.app.com;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;

import chessengine.BoardPreference;
import chessengine.BoardView;

public class ColorPicker extends DialogFragment implements View.OnClickListener{
private Button black,white;
private LinearLayout linearLayout;
private ColorPickerView colorPickerView;
private SharedPreferences sharedPreferences;
private BoardPreference boardPreference;
private BoardView boardView;
private  int trigger =1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle){
        View view=inflater.inflate(R.layout.board_color_picker,viewGroup,false);
        black=(Button) view.findViewById(R.id.btn_dark);
        white=(Button) view.findViewById(R.id.btn_white);
        linearLayout=(LinearLayout) view.findViewById(R.id.back_ground);
        colorPickerView=(ColorPickerView) view.findViewById(R.id.colorPicker);
        black.setOnClickListener(this);
        white.setOnClickListener(this);
        black.setTextColor(Color.WHITE);
        white.setTextColor(Color.BLACK);
        black.setBackgroundColor(boardPreference.getDark());
        white.setBackgroundColor(boardPreference.getWhite());
        initColorPicker();
        return  view;
    }

    private void initColorPicker(){
        colorPickerView.addOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                if(trigger == 1){
                    black.setBackgroundColor(i);
                    boardPreference.setDark(i);
                    boardView.setDarkCellsColor(boardPreference.getDark());
                }
                else if (trigger == 0){
                    white.setBackgroundColor(i);
                    boardPreference.setWhite(i);
                    boardView.setWhiteCellsColor(boardPreference.getWhite());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.equals(black)){
            trigger =1;
            black.setTextColor(Color.WHITE);
            white.setTextColor(Color.BLACK);

        }
        else if(v.equals(white)){
            trigger =0;
            black.setTextColor(Color.BLACK);
            white.setTextColor(Color.WHITE);
        }
    }
    @Override
    public  void onDestroyView(){
        super.onDestroyView();
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences){
        this.sharedPreferences=sharedPreferences;
        boardPreference=new BoardPreference(sharedPreferences);
    }

    public void setBoardView(BoardView boardView) {
        this.boardView = boardView;
    }
}
