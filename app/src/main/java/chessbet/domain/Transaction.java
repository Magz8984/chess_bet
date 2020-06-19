package chessbet.domain;
/**
 * @author Collins Magondu 18/06/2020
 */
public class Transaction {
    private String transactionType;
    private String ref;
    private double amount;
    private boolean complete;
    private String name;
    private String dateCreated;

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public boolean isComplete() {
        return complete;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getRef() {
        return ref;
    }

    public String getTransactionType() {
        return transactionType;
    }
}
