package chessbet.domain;

/**
 * @author Collins Magondu 19/06/2020
 * Used for STK Pushes to Daraja API through Payments Service
 */
public class MPESASavingDTO {
    private String amount;
    private String phoneNumber;

    public MPESASavingDTO(String phoneNumber, String amount) {
        this.amount = amount;
        this.phoneNumber = phoneNumber;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
