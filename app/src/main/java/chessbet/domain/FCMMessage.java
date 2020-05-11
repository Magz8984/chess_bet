package chessbet.domain;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Collins Magondu 19/01/2017
 *  This classe will be used to send message to sever to issue messages to respective clients
 */

public class FCMMessage {
    private String message;
    private List<String> registrationTokens = new ArrayList<>();
    private String from;
    private String fromUID;
    private FCMMessageType messageType;
    private String data;

    public void setFrom(String from) {
        this.from = from;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRegistrationTokens(List<String> registrationTokens) {
        this.registrationTokens = registrationTokens;
    }

    public void setFromUID(String fromUID) {
        this.fromUID = fromUID;
    }

    public void setMessageType(FCMMessageType messageType) {
        this.messageType = messageType;
    }

    public FCMMessageType getMessageType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getRegistrationTokens() {
        return registrationTokens;
    }

    public String getFrom() {
        return from;
    }

    public String getFromUID() {
        return fromUID;
    }

    /**
     * To be used only for setting unique identifiers for message info eg challengeId, uid, puzzleId
     * @param data the uid
     */
    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void addRegistartionToken(String token){
        this.registrationTokens.add(token);
    }

    public static FCMMessage FCMMessageFactory (String data, String from, String fromUID, FCMMessageType messageType,
                                                  ArrayList<String> registrationTokens, String message){
        FCMMessage fcmMessage = new FCMMessage();
        fcmMessage.setData(data);
        fcmMessage.setFrom(from);
        fcmMessage.setFromUID(fromUID);
        fcmMessage.setMessage(message);
        fcmMessage.setRegistrationTokens(registrationTokens);
        fcmMessage.setMessageType(messageType);
        return fcmMessage;
    }

    public enum FCMMessageType{
        CHALLENGE {
            @NonNull
            @Override
            public String toString() {
                return "CHALLENGE";
            }
        },
        INFORMATION {
            @NonNull
            @Override
            public String toString() {
                return "INFORMATION";
            }
        },
        CHAT {
            @NonNull
            @Override
            public String toString() {
                return "CHAT";
            }
        },
        TARGET_CHALLENGE {
            @NonNull
            @Override
            public String toString() {
                return "TARGET_CHALLENGE";
            }
        }
    }
}
