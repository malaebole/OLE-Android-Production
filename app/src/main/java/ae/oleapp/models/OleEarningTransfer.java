package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleEarningTransfer {

    @SerializedName("from_to_dates")
    @Expose
    private String fromToDates;
    @SerializedName("total_bookings")
    @Expose
    private String totalBookings;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("charges")
    @Expose
    private String charges;
    @SerializedName("vat_tax")
    @Expose
    private String vatTax;
    @SerializedName("account_name")
    @Expose
    private String accountName;
    @SerializedName("bank_name")
    @Expose
    private String bankName;
    @SerializedName("account_number")
    @Expose
    private String accountNumber;
    @SerializedName("branch_name")
    @Expose
    private String branchName;
    @SerializedName("transaction_date")
    @Expose
    private String transactionDate;
    @SerializedName("invoice_slip")
    @Expose
    private String invoiceSlip;
    @SerializedName("type")
    @Expose
    private String type;

    public String getFromToDates() {
        return fromToDates;
    }

    public void setFromToDates(String fromToDates) {
        this.fromToDates = fromToDates;
    }

    public String getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(String totalBookings) {
        this.totalBookings = totalBookings;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCharges() {
        return charges;
    }

    public void setCharges(String charges) {
        this.charges = charges;
    }

    public String getVatTax() {
        return vatTax;
    }

    public void setVatTax(String vatTax) {
        this.vatTax = vatTax;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getInvoiceSlip() {
        return invoiceSlip;
    }

    public void setInvoiceSlip(String invoiceSlip) {
        this.invoiceSlip = invoiceSlip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
