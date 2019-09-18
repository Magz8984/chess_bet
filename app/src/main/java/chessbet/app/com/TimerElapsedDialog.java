package chessbet.app.com;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import chessbet.domain.TimerEvent;

public class TimerElapsedDialog extends DialogFragment {
    private TimerEvent timerEvent;
    private String result;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle){
        View view = inflater.inflate(R.layout.board_timer_elapsed,viewGroup,false);
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtMessage = view.findViewById(R.id.txtMessage);
        TextView txtResult = view.findViewById(R.id.txtResult);

        txtTitle.setText(timerEvent.toString());
        txtMessage.setText(timerEvent.getMessage());
        txtResult.setText(result);

        return view;
    }

    void setTimerEvent(TimerEvent timerEvent) {
        this.timerEvent = timerEvent;
    }

    void setResult(String result){
        this.result=result;
    }
}
