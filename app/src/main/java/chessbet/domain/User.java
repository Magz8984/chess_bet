package chessbet.domain;

public class User {
    private String date_created;
    private String date_modified;
    private boolean disabled;
    private String email;
    private String ui;
    private String profile_photo_url;

    public String getDate_created() {
        return date_created;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public String getEmail() {
        return email;
    }

    public String getUi() {
        return ui;
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

    public void setUi(String ui) {
        this.ui = ui;
    }

    public void setProfile_photo_url(String profile_photo_url) {
        this.profile_photo_url = profile_photo_url;
    }

    public String getProfile_photo_url() {
        return profile_photo_url;
    }
}

