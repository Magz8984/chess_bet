package chessbet.app.com.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private CircleImageView imgProfilePicture;
    private User user;
    private FirebaseUser firebaseUser;

    private ImageView edit_emailIv, edit_username_Iv, edit_password_Iv;
    private TextView usernameTv, emailTv;
    private ProgressDialog loading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        EventBroadcast.get().addUserLoadedObserver(this);
        imgProfilePicture = view.findViewById(R.id.profile_photo);
        edit_emailIv = view.findViewById(R.id.edit_emailIv);
        edit_username_Iv = view.findViewById(R.id.edit_username_Iv);
        edit_password_Iv = view.findViewById(R.id.edit_password_Iv);
        usernameTv = view.findViewById(R.id.usernameTv);
        emailTv = view.findViewById(R.id.emailTv);

        edit_emailIv.setOnClickListener(this);
        edit_username_Iv.setOnClickListener(this);
        edit_password_Iv.setOnClickListener(this);
        loading = new ProgressDialog(getActivity());
        loading.setMessage("Wait a moment...");
        AccountAPI.get().setUserListener(this);


        edit_emailIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmailDialog();
            }
        });

        edit_username_Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUsernameDialog();
            }
        });

        edit_password_Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOldPasswordDialog();
            }
        });

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
                    emailTv.setText(user.getEmail());
                }
            }catch (Exception ex){
                Log.d(getClass().getSimpleName(), ex.getMessage());
            }

        }
    }

    @Override
    public void onStart() {
        // Make sure soft keyboard does not adjust layout
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onStart();
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_emailIv:
                showEmailDialog();
                break;
            case R.id.edit_username_Iv:
                showUsernameDialog();
                break;
            case R.id.edit_password_Iv:
                showOldPasswordDialog();
                break;
        }
    }

    private void showOldPasswordDialog() {
        //AlertDialog
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("Verify current password");

        //set Layout Linear Layout
        LinearLayout linearLayout = new LinearLayout(getActivity());
        // Views to set in dialog
        final EditText passwordEt = new EditText(getActivity());
        passwordEt.setHint("Enter current password");
        passwordEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        /*sets the main width of EditView to fit a text of n 'M' letters regardless of the actual
        text extension and text size*/
        passwordEt.setMinEms(16);
        linearLayout.addView(passwordEt);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               if (!TextUtils.isEmpty(passwordEt.getText().toString())){
                    reloginUser(passwordEt.getText().toString());
               }else {
                   return;
               }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //create and show dialog
        builder.create().show();
    }

    private void reloginUser(String password) {
        loading.show();
        firebaseUser.reauthenticate(EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()),
                password)).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                // user is the authentic user
                showNewPasswordDialog();
            } else {
                Toast.makeText(getContext(), "Wrong old password provided", Toast.LENGTH_LONG).show();
            }
           loading.dismiss();
        });
    }

    private void showNewPasswordDialog() {
        //AlertDialog
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("Update password");

        //set Layout Linear Layout
        LinearLayout linearLayout = new LinearLayout(getActivity());
        // Views to set in dialog
        final EditText passwordEt = new EditText(getActivity());
        passwordEt.setHint("Enter password");
        passwordEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        /*sets the main width of EditView to fit a text of n 'M' letters regardless of the actual
        text extension and text size*/
        passwordEt.setMinEms(16);
        linearLayout.addView(passwordEt);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(passwordEt.getText().toString())){
                    updatePassword(passwordEt.getText().toString());
                }
                else {
                    return;
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //create and show dialog
        builder.create().show();
    }

    private void updatePassword(String password) {
        loading.show();
        firebaseUser.updatePassword(password).addOnCompleteListener(task1 -> {
            if(task1.isSuccessful()){
                Toast.makeText(getContext(), "Successfully updated password", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Error on password update", Toast.LENGTH_LONG).show();
            }
        });
        loading.dismiss();
    }

    private void showUsernameDialog() {
        //AlertDialog
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("Update username");

        //set Layout Linear Layout
        LinearLayout linearLayout = new LinearLayout(getActivity());
        // Views to set in dialog
        final EditText usernameEt = new EditText(getActivity());
        usernameEt.setHint("Enter username");
        /*sets the main width of EditView to fit a text of n 'M' letters regardless of the actual
        text extension and text size*/
        usernameEt.setMinEms(16);
        linearLayout.addView(usernameEt);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(usernameEt.getText().toString())){
                    updateUsername(usernameEt.getText().toString());
                }
                else {
                    return;
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
            }
        });

        //create and show dialog
        builder.create().show();
    }

    private void updateUsername(String username) {
        loading.show();
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        builder.setDisplayName(username);
        AccountAPI.get().getCurrentUser().setUser_name(username);
        firebaseUser.updateProfile(builder.build()).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                AccountAPI.get().updateUser();
                EventBroadcast.get().broadcastUserUpdate();
                Toast.makeText(getContext(),"Username successfully changed", Toast.LENGTH_SHORT).show();
            }
            loading.dismiss();
        });
    }

    private void showEmailDialog() {
        //AlertDialog
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("Update email");

        //set Layout Linear Layout
        LinearLayout linearLayout = new LinearLayout(getActivity());
        // Views to set in dialog
        final EditText emailEt = new EditText(getActivity());
        emailEt.setHint("Enter email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        /*sets the main width of EditView to fit a text of n 'M' letters regardless of the actual
        text extension and text size*/
        emailEt.setMinEms(16);
        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(emailEt.getText().toString())){
                    updateEmail(emailEt.getText().toString());
                }else {
                    return;
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //create and show dialog
        builder.create().show();
    }

    private void updateEmail(String email) {
        loading.show();
        firebaseUser.updateEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                AccountAPI.get().getCurrentUser().setEmail(email);
                AccountAPI.get().updateUser();
                emailTv.setText(email);
                EventBroadcast.get().broadcastUserUpdate();
                Toast.makeText(getContext(),"Email successfully changed", Toast.LENGTH_SHORT).show();
                // Send email verification once new email has been set
                sendEmailVerification();
            } else {
                Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
            loading.dismiss();
        });
    }
    private void sendEmailVerification(){
        loading.show();
        firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {
            loading.dismiss();
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
