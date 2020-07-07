package chessbet.domain;

/**
 * @author Collins Magondu 19/06/2020
 * Allows withdrawal by MPESA
 */
public class MPESAPayoutDTO {
    public Amount amount;
    public String accountHolder;
    public String recipient;

    public MPESAPayoutDTO(Amount amount, String accountHolder, String recipient) {
        this.amount = amount;
        this.accountHolder = accountHolder;
        this.recipient = recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }


    public Amount getAmount() {
        return amount;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public String getAccountHolder() {
        return accountHolder;
    }
}
