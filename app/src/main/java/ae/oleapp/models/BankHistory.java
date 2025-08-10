package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankHistory {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("bank_id")
    @Expose
    private String bankId;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("earning_amount")
    @Expose
    private String earningAmount;
    @SerializedName("extra_amount")
    @Expose
    private String extraAmount;
    @SerializedName("deposit_type")
    @Expose
    private String depositType;
    @SerializedName("income_type")
    @Expose
    private String incomeType;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("receipt")
    @Expose
    private String receipt;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("added_date")
    @Expose
    private String addedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEarningAmount() {
        return earningAmount;
    }

    public void setEarningAmount(String earningAmount) {
        this.earningAmount = earningAmount;
    }

    public String getExtraAmount() {
        return extraAmount;
    }

    public void setExtraAmount(String extraAmount) {
        this.extraAmount = extraAmount;
    }

    public String getDepositType() {
        return depositType;
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public String getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(String incomeType) {
        this.incomeType = incomeType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

}
