package chessbet.app.com.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Date;

import chessbet.api.AccountAPI;
import chessbet.app.com.R;
import chessbet.domain.AccountEvent;
import chessbet.domain.Constants;

public class TermsOfService extends DialogFragment implements View.OnClickListener{
    private CheckBox chkAccept;
    private WebView webView;
    private Button btnDone;
    private Button btnCancel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.terms_of_service_fragment, container, false);
        webView = view.findViewById(R.id.tocbrowser);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnDone = view.findViewById(R.id.btnDone);
        chkAccept = view.findViewById(R.id.tocaccept);
        btnDone.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        //Only for page load
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(Constants.TERMS_OF_SERVICE_URL);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Disable after page load
                webView.getSettings().setJavaScriptEnabled(false);
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.equals(btnDone)){
            if(chkAccept.isChecked()){
                AccountEvent accountEvent = new AccountEvent();
                accountEvent.setName(AccountEvent.Event.TERMS_OF_SERVICE_ACCEPTED);
                accountEvent.setDone(true);
                // Reference
                accountEvent.setDate_created(new Date().toString());
                AccountAPI.get().getCurrentAccount().addEvent(accountEvent);
                AccountAPI.get().getCurrentAccount().setTerms_and_condition_accepted(true);
                AccountAPI.get().updateAccount();
                dismiss();
            } else {
                Toast.makeText(getContext(), "Terms of service not accepted", Toast.LENGTH_LONG).show();
            }

        } else if (view.equals(btnCancel)){
            dismiss();
        }
    }
}
