package chessbet.app.com.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import chessbet.app.com.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    @BindView(R.id.profile_photo) CircleImageView profile_photo;
    @BindView(R.id.iv_camera) CircleImageView iv_camera;
    @BindView(R.id.nameTv) TextView nameTv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }
}
