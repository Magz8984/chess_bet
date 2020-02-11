package chessbet.app.com.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.flask.colorpicker.ColorPickerView;

import chessbet.app.com.R;
import chessengine.BoardPreference;
import chessengine.BoardView;

public class ColorPicker extends DialogFragment implements View.OnClickListener{
private Button black,white;
private ColorPickerView colorPickerView;
private BoardPreference boardPreference;
private BoardView boardView;
private  int trigger = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle){
        View view=inflater.inflate(R.layout.board_color_picker,viewGroup,false);
        black = view.findViewById(R.id.btn_dark);
        white= view.findViewById(R.id.btn_white);
        colorPickerView= view.findViewById(R.id.colorPicker);
        black.setOnClickListener(this);
        white.setOnClickListener(this);
        black.setTextColor(Color.WHITE);
        white.setTextColor(Color.BLACK);
        initColorPicker();
        return  view;
    }

    private void initColorPicker(){
        // Close dialog on orientation to landscape
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            dismiss();
        }

        if(getActivity() != null){
            boardPreference = new BoardPreference(getActivity().getPreferences(Context.MODE_PRIVATE));
            black.setBackgroundColor(boardPreference.getDark());
            white.setBackgroundColor(boardPreference.getWhite());
        }
        colorPickerView.addOnColorChangedListener(i -> {
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
        });
    }

    @Override
    public void onClick(View v) {
        if(v.equals(black)){
            trigger = 1;
            black.setTextColor(Color.WHITE);
            white.setTextColor(Color.BLACK);

        }
        else if(v.equals(white)){
            trigger = 0;
            black.setTextColor(Color.BLACK);
            white.setTextColor(Color.WHITE);
        }
    }
    @Override
    public  void onDestroyView(){
        super.onDestroyView();
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences){
        boardPreference=new BoardPreference(sharedPreferences);
    }

    public void setBoardView(BoardView boardView) {
        this.boardView = boardView;
    }
}
