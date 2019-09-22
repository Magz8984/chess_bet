package chessbet.domain;

import java.util.ArrayList;

public class Account {
    private double amount;
    private String currency;
    private boolean terms_and_condition_accepted;
    private String date_created;
    private ArrayList<AccountEvent> events;
    private String last_date_modified;
    private AccountStatus status;
    private String owner;
    private int elo_rating;
    private ArrayList<MatchDetails> matches;

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
}
