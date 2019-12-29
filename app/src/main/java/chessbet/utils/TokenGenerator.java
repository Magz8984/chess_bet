package chessbet.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * @author Collins
 * Used For Token Generation For Cloud Functions
 */
public class TokenGenerator {
    /**
     * Used for all api calls to cloud functions
     * @param onTokenGeneratedListener
     */
    public static void genetrateToken(OnTokenGeneratedListener onTokenGeneratedListener){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        firebaseUser.getIdToken(true).addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null && task.getResult().getToken() != null){
                onTokenGeneratedListener.onTokenGenerated(task.getResult().getToken());
            }
        });
    }

    public interface OnTokenGeneratedListener{
        void onTokenGenerated(String token);
    }
}
