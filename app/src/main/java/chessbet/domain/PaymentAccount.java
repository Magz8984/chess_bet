package chessbet.domain;

/**
 * @author Collins Magondu 18/06/2020
 */
public class PaymentAccount {
    private long id;
    private String email;
    private String name;
    private String phoneNumber;
    private double balance;
    private boolean termsOfServiceAccepted;
    private Status status;

    public double getBalance() {
        return balance;
    }

    public long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setTermsOfServiceAccepted(boolean termsOfServiceAccepted) {
        this.termsOfServiceAccepted = termsOfServiceAccepted;
    }

    public boolean isTermsOfServiceAccepted() {
        return termsOfServiceAccepted;
    }

    //    privat
    public enum Status {
        ACTIVE,
        SUSPENDED,
        PENDING,
        CLOSED
    }
}
