package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OlePaymentSetting {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("club_id")
    @Expose
    private String clubId;
    @SerializedName("cash_payment")
    @Expose
    private String cashPayment;
    @SerializedName("card_payment")
    @Expose
    private String cardPayment;
    @SerializedName("bank_name")
    @Expose
    private String bankName;
    @SerializedName("account_name")
    @Expose
    private String accountName;
    @SerializedName("branch_name")
    @Expose
    private String branchName;
    @SerializedName("account_number")
    @Expose
    private String accountNumber;
    @SerializedName("iban_number")
    @Expose
    private String ibanNumber;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getCashPayment() {
        return cashPayment;
    }

    public void setCashPayment(String cashPayment) {
        this.cashPayment = cashPayment;
    }

    public String getCardPayment() {
        return cardPayment;
    }

    public void setCardPayment(String cardPayment) {
        this.cardPayment = cardPayment;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIbanNumber() {
        return ibanNumber;
    }

    public void setIbanNumber(String ibanNumber) {
        this.ibanNumber = ibanNumber;
    }

}
