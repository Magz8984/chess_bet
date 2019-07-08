package chessbet.domain;

/**
 * Author Collins Magondu
 */

import chessbet.app.com.BuildConfig;

public class Constants {
    public static String CLOUD_FUNCTIONS_URL = BuildConfig.CLOUD_FUNCTIONS_URL;
    public static String GET_MATCH_PATH_ON_ELO_RATING = "/getMatchableAccountOnEloRating";
    public static String CREATE_MATCHABLE_ACCOUNT_PATH = "/createUserMatchableAccount";
}
