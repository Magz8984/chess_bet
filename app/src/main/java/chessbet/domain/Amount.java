package chessbet.domain;

import androidx.annotation.NonNull;

import java.util.Locale;

public class Amount {
    private double amount;
    private String currency;

    public Amount() {
    }

    public Amount(double amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.ENGLISH,"%s %.2f", currency, amount);
    }
}
