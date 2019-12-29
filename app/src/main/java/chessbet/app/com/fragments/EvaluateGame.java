package chessbet.app.com.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.Objects;

import chessbet.app.com.BuildConfig;
import chessbet.app.com.R;

public class EvaluateGame extends DialogFragment {
    private UnifiedNativeAdView nativeAdView;
    private UnifiedNativeAd nativeAd = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.evaluate_game_dialog, container, false);
        nativeAdView = view.findViewById(R.id.addView);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        viewAd();
    }

    private void populateAdView(UnifiedNativeAd nativeAd){
        MediaView mediaView = nativeAdView.findViewById(R.id.ad_media);
        nativeAdView.setMediaView(mediaView);

        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.ad_headline));
        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.ad_body));
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.ad_call_to_action));
        nativeAdView.setIconView(nativeAdView.findViewById(R.id.ad_app_icon));
        nativeAdView.setPriceView(nativeAdView.findViewById(R.id.ad_price));
        nativeAdView.setStarRatingView(nativeAdView.findViewById(R.id.ad_stars));
        nativeAdView.setStoreView(nativeAdView.findViewById(R.id.ad_store));
        nativeAdView.setAdvertiserView(nativeAdView.findViewById(R.id.ad_advertiser));

        ((TextView) nativeAdView.getHeadlineView()).setText(nativeAd.getHeadline());

        if(nativeAd.getBody() == null){
            nativeAdView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            nativeAdView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) nativeAdView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            nativeAdView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            nativeAdView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) nativeAdView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            nativeAdView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) nativeAdView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            nativeAdView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
           nativeAdView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
           nativeAdView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView)nativeAdView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
           nativeAdView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
           nativeAdView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView)nativeAdView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
           nativeAdView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar)nativeAdView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
           nativeAdView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
           nativeAdView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView)nativeAdView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
           nativeAdView.getAdvertiserView().setVisibility(View.VISIBLE);
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
        AdLoader.Builder builder = new AdLoader.Builder(Objects.requireNonNull(getContext()), BuildConfig.ADD_MOB_UNIT_ID);
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
                        Toast.makeText(getContext(), "Failed to load due to an network error", Toast.LENGTH_LONG).show();
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
    public void onDestroy() {
        if(nativeAd != null){
            nativeAd.destroy();
        }
        super.onDestroy();
    }
}
