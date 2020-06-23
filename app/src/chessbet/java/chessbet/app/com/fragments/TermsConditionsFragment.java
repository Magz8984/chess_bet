package chessbet.app.com.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.app.com.R;

public class TermsConditionsFragment extends Fragment {
    @BindView(R.id.webViewTerms) WebView webView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_terms_conditions, container, false);
        ButterKnife.bind(this, root);
        webView.loadUrl("file:///android_asset/terms_of_service.html");
        return root;
    }
}
