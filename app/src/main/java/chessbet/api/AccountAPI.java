package chessbet.api;

/**
 * @author Collins Magondu
 */
public class AccountAPI {
    private static AccountAPI INSTANCE = new AccountAPI();

    private AccountAPI() {

    }

    public static AccountAPI get(){
        return INSTANCE;
    }


}
