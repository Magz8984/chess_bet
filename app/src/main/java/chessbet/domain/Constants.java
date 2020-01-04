package chessbet.domain;

/**
 * Author Collins Magondu
 */

import chessbet.app.com.BuildConfig;

public class Constants {
    public static String CLOUD_FUNCTIONS_URL = BuildConfig.CLOUD_FUNCTIONS_URL;
    public static String GET_MATCH_PATH_ON_ELO_RATING = "/getMatchableAccountOnEloRating";
    public static String CREATE_USER_MATCHABLE_ACCOUNT = "/createUserMatchableAccount";
    public static String EVALUATE_AND_STORE_MATCH = "/evaluateMatch";
    public static String TERMS_OF_SERVICE_URL = "https://chess-mvp.com/#/home/tos";
    public static String GAMES_CLOUD_REFERENCE = "Games";
    public static String UTILITY_PROFILE = "https://firebasestorage.googleapis.com/v0/b/chessbet-app-com-v1.appspot.com/o/utilities%2Fuser.png" +
            "?alt=media&token=887183ec-e95f-443e-9a12-2ff0ab711b96";
    public static String connectionTesterURL = "https://www.google.com/url?sa=i&source=images&cd=&ved=2ahUKEwipr-" +
            "6TrpTmAhUD1RoKHedPAgUQjRx6BAgBEAQ&url=https%3A%2F%2Fwww.bigstockphoto.com%2F&psig=AOvVaw3ItTsZxypaHe-yhvNLp7-Y&ust=1575286630471989";
    public static long MAX_MATCHING_DURATION = 40000;
}
