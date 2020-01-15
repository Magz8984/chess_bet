package chessbet.domain;

public class User {
    private String date_created;
    private String date_modified;
    private boolean disabled;
    private String email;
    private String uid;
    private String user_name;
    private String profile_photo_url;
    private long lastSeen;
    private String fcmToken;
    private boolean online;

    public String getDate_created() {
        return date_created;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public void setUser_name(String userName) {
        this.user_name = userName;
    }

    public String getUser_name() {
        return user_name;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUid(String ui) {
        this.uid = ui;
    }


    public void setProfile_photo_url(String profile_photo_url) {
        this.profile_photo_url = profile_photo_url;
    }

    public String getProfile_photo_url() {
        return (profile_photo_url == null) ? Constants.UTILITY_PROFILE : profile_photo_url;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }
}

