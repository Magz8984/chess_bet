package chessbet.app.com.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import chessbet.api.AccountAPI;
import chessbet.app.com.R;
import chessbet.domain.User;
import chessbet.utils.EventBroadcast;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener, EventBroadcast.UserLoaded {
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
        init();
        return view;
    }

    private void init(){
        user = AccountAPI.get().getCurrentUser();
        if(user != null) {
            if(user.getProfile_photo_url() != null){
                Glide.with(this).asBitmap().load(user.getProfile_photo_url()).into(imgProfilePicture);
            }

            if(user.getEmail() != null){
                txtEmail.setText(user.getEmail());
            }
        }
    }

    @Override
    public void onClick(View v) {
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        if(v.equals(btnSave)){
            // Add profile matcher
            if(txtNewUsername.getText().toString().length() > 0){
                progressBar.setVisibility(View.VISIBLE);
                builder.setDisplayName(txtNewUsername.getText().toString());
                firebaseUser.updateProfile(builder.build()).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        EventBroadcast.get().broadcastUserUpdate();
                        Toast.makeText(getContext(),"Profile update successful", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
            }
        }
    }

    @Override
    public void onUserLoaded() {
        init();
    }
}
