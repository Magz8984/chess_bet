package chessbet.domain;

/*
  @author Collins Magondu
 */

import chessbet.app.com.BuildConfig;
import okhttp3.MediaType;

public class Constants {
    public static String CLOUD_FUNCTIONS_URL = BuildConfig.CLOUD_FUNCTIONS_URL;
    public static String CREATE_USER_MATCHABLE_ACCOUNT = "/createUserMatchableAccount";
    public static String EVALUATE_AND_STORE_MATCH = "/evaluateMatch";
    public static String FORCE_EVALUATE_MATCH = "/forceEvaluateMatch";
    public static String SEND_FCM_MESSAGE_TO_USERS = "/sendFCMMessage";
    public static String GET_SET_CHALLENGE = "/challenge/randomChallenge";
    public static String SEND_TARGETED_CHALLENGE = "/challenge/sendTargetedChallenge";
    public static String ACCEPT_TARGETED_CHALLENGE = "/challenge/acceptTargetChallenge";
    public static String TERMS_OF_SERVICE_URL = "https://chess-mvp.com/#/home/tos";
    public static String GET_PAYMENT_ACCOUNT = "/payments/serviceAccount";
    public static String SAVE_BY_DARAJA = "/daraja/save";
    public static String WITHDRAW_BY_DARAJA = "/payments/withdraw";
    public static String GET_TRANSACTIONS = "/transactions";
    public static String GAMES_CLOUD_REFERENCE = "games";
    public static String UTILITY_PROFILE = "https://firebasestorage.googleapis.com/v0/b/chessbet-app-com-v1.appspot.com/o/utilities%2Fuser.png" +
            "?alt=media&token=887183ec-e95f-443e-9a12-2ff0ab711b96";
    public static String connectionTesterURL = "https://www.google.com/url?sa=i&source=images&cd=&ved=2ahUKEwipr-" +
            "6TrpTmAhUD1RoKHedPAgUQjRx6BAgBEAQ&url=https%3A%2F%2Fwww.bigstockphoto.com%2F&psig=AOvVaw3ItTsZxypaHe-yhvNLp7-Y&ust=1575286630471989";
    public static long MAX_MATCHING_DURATION = 40000;
    public static long DEFAULT_MATCH_DURATION = 15;
    public static String CHALLENGE_ID = "challengeId";
    public static String IS_CHALLENGER = "isChallenger";
    public static String CHALLENGER = "Challenger";
    public static final int TIME_NO_MOVE_END_MATCH = 240;
    public static final int TIME_NO_MOVE_CAUTION = 230;
    public static int RESPONSE_OKAY_FLAG = 200;
    public static String MESSAGE_TYPE  = "messageType";
    public static String FROM_USER = "fromUser";
    public static String FCM_MESSAGE = "message";
    public static String IS_ON_MATCH = "isOnMatch";
    public static String WHITE = "WHITE";
    public static String BLACK = "BLACK";
    public static String DATA = "data";
    public static String PGN = "pgn";

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * Used to remove characters from strings
     * @param string input string
     * @return output string
     */
    public static String GetDigitsFromString(String string){
        StringBuilder builder = new StringBuilder();
        for (char c: string.toCharArray()) {
            if(Character.isDigit(c)){
               builder.append(c);
            }
        }
        return builder.toString();
    }
}
