package chessbet.app.com.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.api.AccountAPI;
import chessbet.app.com.R;
import chessbet.app.com.activities.MainActivity;
import chessbet.domain.Account;
import chessbet.domain.AccountEvent;
import chessbet.domain.User;
import chessbet.services.AccountListener;
import chessbet.utils.Util;
import es.dmoral.toasty.Toasty;

public class TermsConditionsFragment extends Fragment implements View.OnClickListener, AccountListener {
    @BindView(R.id.webViewTerms) WebView webView;
    @BindView(R.id.btnAcceptTerms) Button btnAcceptTerms;
    private ProgressDialog loader;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_terms_conditions, container, false);
        ButterKnife.bind(this, root);
        webView.loadUrl("file:///android_asset/terms_of_service.html");
        btnAcceptTerms.setOnClickListener(this);
        loader = new ProgressDialog(requireContext());
        loader.setMessage("Accepting terms and conditions");

        // Handle Back press
        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK) {

                    Util.switchContent(R.id.frag_container,
                            Util.GAMES_FRAGMENT,
                            ((MainActivity) (getContext())),
                            Util.AnimationType.SLIDE_UP);
                }
                return true;
            }
        });
        return root;
    }

    @Override
    public void onClick(View view) {
        if(view.equals(btnAcceptTerms)){
             Account account = AccountAPI.get().getCurrentAccount();
            if(account != null) {
                if(!account.isTerms_and_condition_accepted()) {
                    loader.show();
                    AccountEvent accountEvent = new AccountEvent();
                    accountEvent.setName(AccountEvent.Event.TERMS_OF_SERVICE_ACCEPTED);
                    accountEvent.setDone(true);
                    accountEvent.setDate_created(Util.now());
                    AccountAPI.get().getCurrentAccount().addEvent(accountEvent);
                    AccountAPI.get().getCurrentAccount().setTerms_and_condition_accepted(true);
                    AccountAPI.get().updateAccount(this);
                } else  {
                    Toasty.info(requireContext(), "Terms and conditions is already accepted", Toasty.LENGTH_LONG).show();
                }
            } else {
                Toasty.info(requireContext(), "Account Not Fetched Yet", Toasty.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onAccountReceived(Account account) {

    }

    @Override
    public void onUserReceived(User user) {

    }

    @Override
    public void onAccountUpdated(boolean status) {
        AccountAPI.get().getCurrentAccount().setTerms_and_condition_accepted(status);
        loader.dismiss();
        if(status) {
            Toasty.success(requireContext(), "Terms and conditions is accepted", Toasty.LENGTH_LONG).show();
        } else {
            Toasty.error(requireContext(), "Terms and conditions is not accepted", Toasty.LENGTH_LONG).show();
        }
    }
}
