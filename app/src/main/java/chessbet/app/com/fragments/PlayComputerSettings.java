package chessbet.app.com.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import chessbet.adapter.ComputerSkillLevelAdapter;
import chessbet.app.com.BoardActivity;
import chessbet.app.com.R;
import chessbet.domain.MatchType;

public class PlayComputerSettings extends DialogFragment implements View.OnClickListener, ComputerSkillLevelAdapter.SkillLevel {
    private Button btnCancel;
    private Button btnPlay;
    private int skillLevel = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_computer_dialog, container, false);
        GridView gridView = view.findViewById(R.id.levels);
        gridView.setAdapter(new ComputerSkillLevelAdapter(getContext(), this));
        btnCancel = view.findViewById(R.id.btnCancel);
        btnPlay = view.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(Objects.requireNonNull(this.getDialog()).getWindow())
                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View view) {
        if(view.equals(btnCancel)){
            this.dismiss();
        } else if (view.equals(btnPlay)) {
            if(skillLevel != 0) {
                Intent intent=new Intent(getContext(), BoardActivity.class);
                intent.putExtra("match_type", MatchType.SINGLE_PLAYER.toString());
                intent.putExtra("skill_level", skillLevel);
                Objects.requireNonNull(getContext()).startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Set Skill Level", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }
}
