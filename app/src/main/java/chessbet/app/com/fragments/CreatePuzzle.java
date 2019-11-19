package chessbet.app.com.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Date;
import java.util.Objects;

import chessbet.api.AccountAPI;
import chessbet.app.com.R;
import chessbet.domain.Puzzle;
import chessbet.services.PuzzleListener;

public class CreatePuzzle extends DialogFragment implements View.OnClickListener , PuzzleListener {
    private Puzzle puzzle;
    private Button btnCancel;
    private Button btnCreate;
    private EditText txtDescription;
    private EditText txtTitle;
    private DeletePuzzle deletePuzzle;
    public CreatePuzzle(Puzzle puzzle, DeletePuzzle deletePuzzle){
        this.deletePuzzle = deletePuzzle;
        this.puzzle = puzzle;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_puzzle_dialog, container);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnCreate = view.findViewById(R.id.btnCreate);
        txtDescription = view.findViewById(R.id.description);
        txtTitle = view.findViewById(R.id.title);
        TextView txtOwnerEmail = view.findViewById(R.id.ownerEmail);
        TextView txtPGN = view.findViewById(R.id.pgn);

        txtOwnerEmail.setText(puzzle.getOwner());
        txtPGN.setText(puzzle.getPgn());

        btnCreate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        AccountAPI.get().setPuzzleListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.equals(btnCancel)){
            this.deletePuzzle.onDelete();
            Toast.makeText(getContext(), "Discarded Puzzle", Toast.LENGTH_SHORT).show();
            Objects.requireNonNull(this.getDialog()).cancel();
        }
        else if(v.equals(btnCreate)){
            if(txtDescription.getText().toString().equals("")){
                Toast.makeText(getContext(), "Add a description", Toast.LENGTH_LONG).show();
            }
            else if (txtTitle.getText().toString().equals("")){
                Toast.makeText(getContext(), "Add a title", Toast.LENGTH_LONG).show();
            }
            else {
                this.puzzle.setTimestamp(new Date().getTime());
                this.puzzle.setDescription(txtDescription.getText().toString());
                this.puzzle.setTitle(txtTitle.getText().toString());
                AccountAPI.get().sendPuzzle(this.puzzle);
            }
        }
    }

    @Override
    public void onPuzzleSent(boolean isSent) {
        if(isSent){
            Toast.makeText(getContext(), "Puzzle Sent", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getContext(), "Puzzle Not Sent", Toast.LENGTH_LONG).show();
        }
    }

    public interface DeletePuzzle{
        void onDelete();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
