package chessbet.app.com.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

import chessbet.api.AccountAPI;
import chessbet.app.com.R;
import chessbet.domain.User;
import chessbet.services.UserListener;
import chessbet.utils.EventBroadcast;
import chessbet.utils.Util;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener, EventBroadcast.UserLoaded, UserListener {
    private TextView txtEmail;
    private EditText txtNewEmail;
    private EditText txtNewPassword;
    private EditText txtNewUsername;
    private CircleImageView imgProfilePicture;
    private User user;
    private FirebaseUser firebaseUser;
    private Button btnSave;
    private TextView txtOldPassword;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        EventBroadcast.get().addUserLoadedObserver(this);
        imgProfilePicture = view.findViewById(R.id.profile_photo);
        txtEmail = view.findViewById(R.id.existingEmail);
        txtNewEmail = view.findViewById(R.id.txtEmailEdit);
        txtNewPassword = view.findViewById(R.id.txtPasswordEdit);
        txtNewUsername = view.findViewById(R.id.txtUserNameEdit);
        txtOldPassword = view.findViewById(R.id.txtOldPasswordEdit);
        progressBar = view.findViewById(R.id.progress_bar);
        btnSave = view.findViewById(R.id.btnSaveProfile);
        btnSave.setOnClickListener(this);
        AccountAPI.get().setUserListener(this);
        return view;
    }

    private void init(){
        user = AccountAPI.get().getCurrentUser();
        if(user != null) {
            try{
                if(user.getProfile_photo_url() != null){
                    Glide.with(this).asBitmap().load(user.getProfile_photo_url()).into(imgProfilePicture);
                }

                if(user.getEmail() != null){
                    txtEmail.setText(user.getEmail());
                }
            }catch (Exception ex){
                Log.d(getClass().getSimpleName(), ex.getMessage());
            }

        }
    }

    @Override
    public void onStart() {
        // Make sure soft keyboard does not adjust layout
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onStart();
        init();
    }

    @Override
    public void onClick(View v) {
        if(v.equals(btnSave)){
            updateUserName();
            updatePassword();
            updateEmailAddress();
        }
    }

    private void updatePassword(){
        if(Util.textViewHasText(txtNewPassword) && Util.textViewHasText(txtOldPassword)){
            progressBar.setVisibility(View.VISIBLE);
            firebaseUser.reauthenticate(EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()),
                    txtOldPassword.getText().toString())).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    // Update password only after task is successful
                    firebaseUser.updatePassword(txtNewPassword.getText().toString()).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            Toast.makeText(getContext(), "Successfully updated password", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Error on password update", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Wrong old password provided", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            });
        }
    }

    private void updateUserName(){
        if(Util.textViewHasText(txtNewUsername)){
            progressBar.setVisibility(View.VISIBLE);
            UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
            builder.setDisplayName(txtNewUsername.getText().toString());
            AccountAPI.get().getCurrentUser().setUser_name(txtNewUsername.getText().toString());
            firebaseUser.updateProfile(builder.build()).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    AccountAPI.get().updateUser();
                    EventBroadcast.get().broadcastUserUpdate();
                    Toast.makeText(getContext(),"Username successfully changed", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            });
        }
    }

    private void updateEmailAddress(){
        if(Util.textViewHasText(txtNewEmail)){
            progressBar.setVisibility(View.VISIBLE);
            firebaseUser.updateEmail(txtNewEmail.getText().toString()).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    AccountAPI.get().getCurrentUser().setEmail(txtNewEmail.getText().toString());
                    AccountAPI.get().updateUser();
                    txtEmail.setText(txtNewEmail.getText().toString());
                    EventBroadcast.get().broadcastUserUpdate();
                    Toast.makeText(getContext(),"Email successfully changed", Toast.LENGTH_SHORT).show();
                    // Send email verification once new email has been set
                    sendEmailVerification();
                } else {
                    Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            });
        }
    }

    private void sendEmailVerification(){
        progressBar.setVisibility(View.VISIBLE);
        firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if(task.isSuccessful()){
                Toast.makeText(getContext(),"Email Verification Sent", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUserLoaded() {
        init();
    }

    @Override
    public void onUserUpdated(boolean status) {
        if(getContext() != null){
            if(status){
                Toast.makeText(getContext(), "User Updated", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "User Not Updated", Toast.LENGTH_LONG).show();
            }
        }
    }
}
