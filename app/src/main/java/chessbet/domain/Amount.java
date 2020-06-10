package chessbet.domain;

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

    @Override
    public String toString() {
        return "Amount{" +
                "amount='" + amount + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}
