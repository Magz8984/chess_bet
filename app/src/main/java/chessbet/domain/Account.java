package chessbet.domain;


import java.util.ArrayList;

public class Account {
    private String id;
    private double amount;
    private String currency;
    private boolean terms_and_condition_accepted;
    private String date_created;
    private ArrayList<AccountEvent> events;
    private String last_date_modified;
    private long last_matchable_time;
    private MatchType last_match_type;
    private long last_match_duration = 0;
    private Amount last_match_amount;
    private AccountStatus status;
    private String owner;
    private int elo_rating;
    private boolean matched;
    private ArrayList<MatchDetails> matches;
    private String current_challenge_id;
    private long current_challenge_timestamp;

    public void setElo_rating(int elo_rating) {
        this.elo_rating = elo_rating;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setEvents(ArrayList<AccountEvent> events) {
        this.events = events;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setLast_date_modified(String last_date_modified) {
        this.last_date_modified = last_date_modified;
    }
    public void setMatches(ArrayList<MatchDetails> matches) {
        this.matches = matches;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public boolean isTerms_and_condition_accepted() {
        return terms_and_condition_accepted;
    }

    public void setTerms_and_condition_accepted(boolean terms_and_condition_accepted) {
        this.terms_and_condition_accepted = terms_and_condition_accepted;
    }

    public void setLast_match_amount(Amount last_match_amount) {
        this.last_match_amount = last_match_amount;
    }

    public void setLast_match_duration(long last_match_duration) {
        this.last_match_duration = last_match_duration;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setLast_match_type(MatchType last_match_type) {
        this.last_match_type = last_match_type;
    }

    public void setLast_matchable_time(long last_matchable_time) {
        this.last_matchable_time = last_matchable_time;
    }

    public Amount getLast_match_amount() {
        return last_match_amount;
    }

    public long getLast_match_duration() {
        return last_match_duration;
    }

    public long getLast_matchable_time() {
        return last_matchable_time;
    }

    public MatchType getLast_match_type() {
        return last_match_type;
    }

    public int getElo_rating() {
        return elo_rating;
    }

    public String getOwner() {
        return owner;
    }

    public String  getDate_created() {
        return date_created;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public ArrayList<AccountEvent> getEvents() {
        return events;
    }

    public void addEvent(AccountEvent accountEvent){
        if(events == null){
            events = new ArrayList<>();
        }
        events.add(accountEvent);
    }
    public ArrayList<MatchDetails> getMatches() {
        return matches;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getLast_date_modified() {
        return last_date_modified;
    }

    public void setCurrent_challenge_id(String current_challenge_id) {
        this.current_challenge_id = current_challenge_id;
    }

    public void setCurrent_challenge_timestamp(long current_challenge_timestamp) {
        this.current_challenge_timestamp = current_challenge_timestamp;
    }

    public long getCurrent_challenge_timestamp() {
        return current_challenge_timestamp;
    }

    public String getCurrent_challenge_id() {
        return current_challenge_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
