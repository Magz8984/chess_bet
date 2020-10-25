package chessbet.utils;

import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Date;

import chessbet.app.com.R;
import chessbet.app.com.fragments.GamesFragment;
import chessbet.app.com.fragments.MatchFragment;
import chessbet.app.com.fragments.NewChallengeFragment;
import chessbet.app.com.fragments.ProfileFragment;
import chessbet.app.com.fragments.TermsConditionsFragment;

/**
 * @author Collins
 */

public class Util {
    public static final String GAMES_FRAGMENT = "GamesFragment";
    public final static String PROFILE_FRAGMENT = "ProfileFragment";
    public final static String PLAY_ONLINE_FRAGMENT = "PlayOnlineFragment";
    public static final String TERMS_FRAGMENT = "TermsFragment";
    public static final String NEW_CHALLENGES_FRAGMENT = "NewChallengesFragment";
    private static String CURRENT_TAG = null;

    /** Get current time*/
    public static String now(){
        Date date = new Date(System.currentTimeMillis());
        return (String) DateFormat.format("yyyy-MM-dd hh:mm:ss", date);
    }

    public static boolean textViewHasText(TextView textView){
        Log.d("UTILDFATA", textView.getText().toString().trim().length() + " " + textView.getText());
        return textView.getText().toString().trim().length() >= 1;
    }

    public static void switchFragmentWithAnimation(int id, Fragment fragment,
                                                   FragmentActivity activity, String TAG, AnimationType transitionStyle) {

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        if (transitionStyle != null) {
            switch (transitionStyle) {
                case SLIDE_DOWN:

                    // Exit from down
                    fragmentTransaction.setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down);

                    break;

                case SLIDE_UP:

                    // Enter from Up
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_up,
                            R.anim.slide_out_up);

                    break;

                case SLIDE_LEFT:

                case SLIDE_IN_SLIDE_OUT:

                    // Enter from left
                    fragmentTransaction.setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_out_left);

                    break;

                // Enter from right
                case SLIDE_RIGHT:
                    fragmentTransaction.setCustomAnimations(R.anim.slide_right,
                            R.anim.slide_out_right);

                    break;

                case FADE_IN:
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                            R.anim.fade_out);

                case FADE_OUT:
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                            R.anim.donot_move);

                    break;

                default:
                    break;
            }
        }

        CURRENT_TAG = TAG;

        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }

    public static void switchContent(int id, String TAG,
                                     FragmentActivity baseActivity, AnimationType transitionStyle) {

        Fragment fragmentToReplace = null;

        FragmentManager fragmentManager = baseActivity
                .getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // If our current fragment is null, or the new fragment is different, we
        // need to change our current fragment
        if (CURRENT_TAG == null || !TAG.equals(CURRENT_TAG)) {

            if (transitionStyle != null) {
                switch (transitionStyle) {
                    case SLIDE_DOWN:
                        // Exit from down
                        transaction.setCustomAnimations(R.anim.slide_up,
                                R.anim.slide_down);

                        break;
                    case SLIDE_UP:
                        // Enter from Up
                        transaction.setCustomAnimations(R.anim.slide_in_up,
                                R.anim.slide_out_up);
                        break;
                    case SLIDE_LEFT:
                    case SLIDE_IN_SLIDE_OUT:
                        // Enter from left
                        transaction.setCustomAnimations(R.anim.slide_left,
                                R.anim.slide_out_left);
                        break;
                    // Enter from right
                    case SLIDE_RIGHT:
                        transaction.setCustomAnimations(R.anim.slide_right,
                                R.anim.slide_out_right);
                        break;
                    case FADE_IN:
                        transaction.setCustomAnimations(R.anim.fade_in,
                                R.anim.fade_out);
                    case FADE_OUT:
                        transaction.setCustomAnimations(R.anim.fade_in,
                                R.anim.donot_move);
                        break;
                    default:
                        break;
                }
            }

            // Try to find the fragment we are switching to
            Fragment fragment = fragmentManager.findFragmentByTag(TAG);

            // If the new fragment can't be found in the manager, create a new
            // one
            if (fragment == null) {
                if (TAG.equals(GAMES_FRAGMENT)) {
                    fragmentToReplace = new GamesFragment();
                } else if (TAG.equals(PROFILE_FRAGMENT)) {
                    fragmentToReplace = new ProfileFragment();
                }
                else if (TAG.equals(PLAY_ONLINE_FRAGMENT)){
                    fragmentToReplace = new MatchFragment();
                }
                else if (TAG.equals(TERMS_FRAGMENT)) {
                    fragmentToReplace = new TermsConditionsFragment();
                } else if (TAG.equals(NEW_CHALLENGES_FRAGMENT)) {
                    fragmentToReplace = new NewChallengeFragment();
                }
            } else {
                if (TAG.equals(GAMES_FRAGMENT)) {
                    fragmentToReplace = fragment;
                } else if (TAG.equals(PROFILE_FRAGMENT)) {
                    fragmentToReplace = fragment;
                }
                else if (TAG.equals(PLAY_ONLINE_FRAGMENT)){
                    fragmentToReplace = fragment;
                }else if (TAG.equals(TERMS_FRAGMENT)) {
                    fragmentToReplace = fragment;
                }else if(TAG.equals(NEW_CHALLENGES_FRAGMENT)){
                    fragmentToReplace = fragment;
                }
            }
            CURRENT_TAG = TAG;

            // Replace our current fragment with the one we are changing to
            transaction.replace(id, fragmentToReplace, TAG);
            transaction.commit();

        }
    }

    public enum AnimationType {
        SLIDE_LEFT, SLIDE_RIGHT, SLIDE_UP, SLIDE_DOWN, FADE_IN, SLIDE_IN_SLIDE_OUT, FADE_OUT
    }
}
