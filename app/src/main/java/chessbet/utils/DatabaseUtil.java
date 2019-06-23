package chessbet.utils;

public class DatabaseUtil {
    // Tables
    public static String matchables = "matchables";
    public static String matches = "matches";

    private DatabaseUtil(){
        throw new RuntimeException("Cannot be instantiated");
    }
}
