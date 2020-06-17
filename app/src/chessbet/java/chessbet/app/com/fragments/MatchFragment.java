package chessbet.app.com.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

import chessbet.api.AccountAPI;
import chessbet.api.ChallengeAPI;
import chessbet.api.MatchAPI;
import chessbet.app.com.BoardActivity;
import chessbet.app.com.BuildConfig;
import chessbet.app.com.R;
import chessbet.domain.Challenge;
import chessbet.domain.ChallengeDTO;
import chessbet.domain.MatchableAccount;
import chessbet.services.MatchListener;
import chessbet.services.MatchService;
import chessbet.utils.DatabaseUtil;
import es.dmoral.toasty.Toasty;

public class MatchFragment extends Fragment implements View.OnClickListener, MatchListener, FABProgressListener, ChallengeAPI.ChallengeHandler{
    private FloatingActionButton btnFindMatch;
    private FABProgressCircle progressCircle;
    private Button btnRatingLess;
    private Button btnRatingMore;
    private Button btnRandom;
    private Button btnKes1000;
    private Button btnKes500;
    private Button btnKes250;
    private Button btnKes100;
    private Button btnKes50;
    private UnifiedNativeAdView nativeAdView;
    private UnifiedNativeAd nativeAd = null;

    private List<View> rangeButtons;
    private List<View> amountButtons;
    private int amount = 50;
    private int range = 1000;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_match, container, false);
        btnFindMatch = root.findViewById(R.id.btnFindMatch);
        progressCircle = root.findViewById(R.id.fabProgressCircle);
        btnRatingLess = root.findViewById(R.id.btnRatingLess);
        btnRatingMore = root.findViewById(R.id.btnRatingMore);
        btnRandom = root.findViewById(R.id.btnRandomChallenge);
        btnKes1000 = root.findViewById(R.id.btnKes1000);
        btnKes500 = root.findViewById(R.id.btnKes500);
        btnKes250 = root.findViewById(R.id.btnKes250);
        btnKes100 = root.findViewById(R.id.btnKes100);
        btnKes50 = root.findViewById(R.id.btnKes50);
        nativeAdView = root.findViewById(R.id.addView);

        btnFindMatch.setOnClickListener(this);
        btnRatingLess.setOnClickListener(this);
        btnRatingMore.setOnClickListener(this);
        btnRandom.setOnClickListener(this);
        btnKes1000.setOnClickListener(this);
        btnKes500.setOnClickListener(this);
        btnKes250.setOnClickListener(this);
        btnKes100.setOnClickListener(this);
        btnKes50.setOnClickListener(this);
        MatchAPI.get().setMatchListener(this);
        amountButtons = Arrays.asList(btnKes50, btnKes100, btnKes250, btnKes1000, btnKes500);
        rangeButtons = Arrays.asList(btnRandom, btnRatingMore, btnRatingLess);
        ChallengeAPI.get().setChallengeHandler(this);
        return root;
    }

    @Override
    public void onClick(View view) {
        if(view.equals(btnFindMatch)) {
            MatchAPI.get().getAccount();
            if(AccountAPI.get().getCurrentAccount() == null) {
                Toasty.info(requireContext(), "Account Is Not Yet Loaded").show();
                return;
            } if (amount == 0 || range == 0) {
                Toasty.info(requireContext(), "Select amount and range").show();
                return;
            }
            progressCircle.show();
            ChallengeDTO challengeDTO = new ChallengeDTO.Builder()
                    .setType(Challenge.Type.BET_CHALLENGE)
                    .setMinEloRating(AccountAPI.get().getCurrentAccount().getElo_rating() - range)
                    .setMaxEloRating(AccountAPI.get().getCurrentAccount().getElo_rating() + range)
                    .setEloRating(AccountAPI.get().getCurrentAccount().getElo_rating())
                    .setOwner(AccountAPI.get().getCurrentAccount().getOwner())
                    .setAmount(amount)
                    .setDuration(5)
                    .build();
            ChallengeAPI.get().getSetChallengeImplementation(challengeDTO);
            btnFindMatch.setEnabled(false);
        } else if(view.equals(btnKes1000)) {
            this.amount = 1000;
            this.selectButton(btnKes1000, amountButtons);
        } else if (view.equals(btnKes500)){
            this.amount = 500;
            this.selectButton(btnKes500, amountButtons);
        } else if(view.equals(btnKes50)) {
            this.amount = 50;
            this.selectButton(btnKes50, amountButtons);
        } else if (view.equals(btnKes250)){
            this.amount = 250;
            this.selectButton(btnKes250, amountButtons);
        } else if (view.equals(btnKes100)) {
            this.amount = 100;
            this.selectButton(btnKes100, amountButtons);
        } else if(view.equals(btnRandom)) {
            this.range = 1000;
            this.selectButton(btnRandom, rangeButtons);
        } else if (view.equals(btnRatingLess)){
            this.range = 200;
            this.selectButton(btnRatingLess, rangeButtons);
        } else if(view.equals(btnRatingMore)) {
            this.range = 500;
            this.selectButton(btnRatingMore, rangeButtons);
        }
    }


    private void selectButton(View selectedButton, List<View> buttons) {
        selectedButton.setBackground(requireContext().getResources().getDrawable(R.drawable.rounded_selected_button));
        for(View button : buttons) {
            if(!button.equals(selectedButton)){
                button.setBackground(requireContext().getResources().getDrawable(R.drawable.rounded_button));
            }
        }
    }

    @Override
    public void onMatchMade(MatchableAccount matchableAccount) {
        try {
            progressCircle.beginFinalAnimation();
            ChallengeAPI.get().setOnChallenge(true);
            AccountAPI.get().getCurrentAccount().setLast_match_duration(0);
            progressCircle.beginFinalAnimation();
            requireContext().startService(new Intent(getContext(), MatchService.class));
            new Handler().postDelayed(() -> {
                Intent target= new Intent(getContext(), BoardActivity.class);
                target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle bundle = new Bundle();
                bundle.putParcelable(DatabaseUtil.matchables,matchableAccount);
                target.putExtras(bundle);
                startActivity(target);
            },3000);
        } catch (Exception ex){
            Crashlytics.logException(ex);
        }
    }

    @Override
    public void onMatchableCreatedNotification() {

    }

    @Override
    public void onMatchError() {
        try {
            requireActivity().runOnUiThread(() -> new Handler().postDelayed(() -> progressCircle.hide(),40000)); // Waits for 40 seconds before hiding the progress bar
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void challengeSent(String id) {
        Toasty.success(requireContext(), "Challenge created Wait a moment For a match").show();
    }

    @Override
    public void challengeFound(String response) {
        FragmentActivity activity = getActivity();
        if(activity != null) {
            activity.runOnUiThread(() -> {
                Toasty.success(requireContext(), response).show();
            });
        }
    }

    @Override
    public void challengeNotFound() {
        FragmentActivity activity = getActivity();
        if(activity != null) {
            activity.runOnUiThread(() -> {
                Toasty.info(requireContext(), "Challenge Not Found").show();
                btnFindMatch.setEnabled(true);
            });
        }
    }

    @Override
    public void onFABProgressAnimationEnd() {
        Snackbar.make(progressCircle, "Challenge Found", Snackbar.LENGTH_LONG)
                .setAction("Action",null)
                .show();
    }

    private void populateAdView(UnifiedNativeAd nativeAd){
        MediaView mediaView = nativeAdView.findViewById(R.id.ad_media);
        nativeAdView.setMediaView(mediaView);

        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.ad_call_to_action));

        if (nativeAd.getCallToAction() == null) {
            nativeAdView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            nativeAdView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) nativeAdView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        nativeAdView.setNativeAd(nativeAd);

        VideoController videoController = nativeAd.getVideoController();

        if(videoController.hasVideoContent()){
            videoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                    Toast.makeText(getContext(), "Thanks For Watching This Ad", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void viewAd(){
        AdLoader.Builder builder = new AdLoader.Builder(requireContext(), BuildConfig.ADD_MOB_UNIT_ID);
        builder.forUnifiedNativeAd(unifiedNativeAd -> {
            if(nativeAd != null){
                nativeAd.destroy();
            }
            nativeAd = unifiedNativeAd;
            populateAdView(nativeAd);
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();
        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                switch(i){
                    case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                        Toast.makeText(getContext(), "Failed to load due to an internal error", Toast.LENGTH_LONG).show();
                        break;
                    case AdRequest.ERROR_CODE_INVALID_REQUEST:
                        Toast.makeText(getContext(), "Failed to load due to an invalid request", Toast.LENGTH_LONG).show();
                        break;
                    case AdRequest.ERROR_CODE_NETWORK_ERROR:
                        Toast.makeText(getContext(), "Failed to load due to a network error", Toast.LENGTH_LONG).show();
                        break;
                    case AdRequest.ERROR_CODE_NO_FILL:
                        Toast.makeText(getContext(), "Failed to load due to code no fill", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(getContext(), "Failed to load with error code " + i, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }


    @Override
    public void onStart() {
        super.onStart();
        this.viewAd();
    }
}
