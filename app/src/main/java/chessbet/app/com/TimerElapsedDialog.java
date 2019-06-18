package chessbet.app.com;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import chessbet.domain.TimerEvent;

public class TimerElapsedDialog extends DialogFragment {
    private TimerEvent timerEvent;
    private String result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle){
        View view = inflater.inflate(R.layout.board_timer_elapsed,viewGroup,false);
        TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtMessage = (TextView) view.findViewById(R.id.txtMessage);
        TextView txtResult = (TextView) view.findViewById(R.id.txtResult);

        txtTitle.setText(timerEvent.toString());
        txtMessage.setText(timerEvent.getMessage());
        txtResult.setText(result);

        return view;
    }

    public void setTimerEvent(TimerEvent timerEvent) {
        this.timerEvent = timerEvent;
    }

    public void setResult(String result){
        this.result=result;
    }
}
