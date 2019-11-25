package chessbet.domain;

/**
 * Author Collins Magondu
 */

import chessbet.app.com.BuildConfig;

public class Constants {
    public static String CLOUD_FUNCTIONS_URL = BuildConfig.CLOUD_FUNCTIONS_URL;
    public static String GET_MATCH_PATH_ON_ELO_RATING = "/getMatchableAccountOnEloRating";
    public static String CREATE_USER_MATCHABLE_ACCOUNT = "/createUserMatchableAccount";
    public static String UTILITY_PROFILE = "https://firebasestorage.googleapis.com/v0/b/chessbet-app-com-v1.appspot.com" +
            "/o/utilities%2FProfile-Icon-PNG-715x715.png?alt=media&token=0fe059f8-3423-4f8f-b14f-6402edb20cb8";
}
