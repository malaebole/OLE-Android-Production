package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OlePlayerBalanceDetail {

    @SerializedName("is_discount")
    @Expose
    private String isDiscount;
    @SerializedName("paid_amount")
    @Expose
    private String paidAmount;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("received_by")
    @Expose
    private String receivedBy;
    @SerializedName("received_date")
    @Expose
    private String receivedDate;
    @SerializedName("receipt")
    @Expose
    private String receipt;

    public String getIsDiscount() {
        return isDiscount;
    }

    public void setIsDiscount(String isDiscount) {
        this.isDiscount = isDiscount;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }
}
