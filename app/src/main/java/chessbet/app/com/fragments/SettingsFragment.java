package chessbet.app.com.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import chessbet.app.com.LoginActivity;
import chessbet.app.com.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_preferences);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if(preference.getKey().equals("sign_out")){
            if(auth.getCurrentUser() != null){
                auth.signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                // Close current activity
                requireActivity().finish();
            }
        } else if (preference.getKey().equals("reset_pin")){
            if(auth.getCurrentUser() != null){
                auth.sendPasswordResetEmail(Objects.requireNonNull(auth.getCurrentUser().getEmail())).addOnCompleteListener(task -> {
                    Toast.makeText(getContext(), "Password Reset Email Has Been Sent", Toast.LENGTH_LONG).show();
                });
            }
        } else if (preference.getKey().equals("notifications")){
            SwitchPreferenceCompat preferenceCompat = (SwitchPreferenceCompat) preference;
        }
        return true;
    }
}
