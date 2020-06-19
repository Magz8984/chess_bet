package chessbet.domain;

/**
 * @author Collins Magondu 19/06/2020
 * Allows withdrawal by MPESA
 */
public class MPESAPayoutDTO {
    public Amount amount;
    public String phoneNumber;

    public MPESAPayoutDTO(Amount amount, String phoneNumber) {
        this.amount = amount;
        this.phoneNumber = phoneNumber;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Amount getAmount() {
        return amount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
