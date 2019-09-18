package chessbet.app.com.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;


import chessbet.app.com.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        view.setBackgroundColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_preferences);

//        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        Log.d(this.getClass().getSimpleName(),preference.getKey());
        return true;
    }
}
