package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OleCashDeposit {

    @SerializedName("cash_in_hand")
    @Expose
    private String cashInHand;
    @SerializedName("card_amount")
    @Expose
    private String cardAmount;
    @SerializedName("pos_amount")
    @Expose
    private String posAmount;
    @SerializedName("discounts")
    @Expose
    private String discounts;
    @SerializedName("match_fees")
    @Expose
    private String matchFees;
    @SerializedName("facilities")
    @Expose
    private String facilities;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("receipt_photo")
    @Expose
    private String receiptPhoto;
    @SerializedName("deposit_note")
    @Expose
    private String depositNote;
    @SerializedName("added_by")
    @Expose
    private String addedBy;
    @SerializedName("added_date")
    @Expose
    private String addedDate;
    @SerializedName("inventory_cash")
    @Expose
    private String inventoryCash;
    @SerializedName("inventory_pos")
    @Expose
    private String inventoryPos;
    @SerializedName("inventory_discount")
    @Expose
    private String inventoryDiscount;
    @SerializedName("deposited_amount")
    @Expose
    private String depositedAmount;
    @SerializedName("expenses_data")
    @Expose
    private List<OleClubExpense> expenseList;
    @SerializedName("report_file_added")
    @Expose
    private Boolean reportFileAdded;

    @SerializedName("can_update")
    @Expose
    private Boolean canUpdate;

    @SerializedName("remainings")
    @Expose
    private String remainings;

    @SerializedName("yesterday_remainings")
    @Expose
    private String yesterdayRemainings;

    @SerializedName("total_expense")
    @Expose
    private String totalExpense;









    public String getCashInHand() {
        return cashInHand;
    }

    public void setCashInHand(String cashInHand) {
        this.cashInHand = cashInHand;
    }

    public String getCardAmount() {
        return cardAmount;
    }

    public void setCardAmount(String cardAmount) {
        this.cardAmount = cardAmount;
    }

    public String getPosAmount() {
        return posAmount;
    }

    public void setPosAmount(String posAmount) {
        this.posAmount = posAmount;
    }

    public String getDiscounts() {
        return discounts;
    }

    public void setDiscounts(String discounts) {
        this.discounts = discounts;
    }

    public String getMatchFees() {
        return matchFees;
    }

    public void setMatchFees(String matchFees) {
        this.matchFees = matchFees;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReceiptPhoto() {
        return receiptPhoto;
    }

    public void setReceiptPhoto(String receiptPhoto) {
        this.receiptPhoto = receiptPhoto;
    }

    public String getDepositNote() {
        return depositNote;
    }

    public void setDepositNote(String depositNote) {
        this.depositNote = depositNote;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getInventoryCash() {
        return inventoryCash;
    }

    public void setInventoryCash(String inventoryCash) {
        this.inventoryCash = inventoryCash;
    }

    public String getInventoryPos() {
        return inventoryPos;
    }

    public void setInventoryPos(String inventoryPos) {
        this.inventoryPos = inventoryPos;
    }

    public String getInventoryDiscount() {
        return inventoryDiscount;
    }

    public void setInventoryDiscount(String inventoryDiscount) {
        this.inventoryDiscount = inventoryDiscount;
    }

    public String getDepositedAmount() {
        return depositedAmount;
    }

    public void setDepositedAmount(String depositedAmount) {
        this.depositedAmount = depositedAmount;
    }

    public List<OleClubExpense> getExpenseList() {
        return expenseList;
    }

    public void setExpenseList(List<OleClubExpense> expenseList) {
        this.expenseList = expenseList;
    }

    public Boolean getReportFileAdded() {
        return reportFileAdded;
    }
    public void setReportFileAdded(Boolean reportFileAdded) {
        this.reportFileAdded = reportFileAdded;
    }
    public Boolean getCanUpdate() {
        return canUpdate;
    }
    public void setCanUpdate(Boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    public String getRemainings() {
        return remainings;
    }
    public void setRemainings(String remainings) {
        this.remainings = remainings;
    }
    public String getYesterdayRemainings() {
        return yesterdayRemainings;
    }
    public void setYesterdayRemainings(String yesterdayRemainings) {
        this.yesterdayRemainings = yesterdayRemainings;
    }

    public String getTotalExpense() {
        return totalExpense;
    }
    public void setTotalExpense(String totalExpense) {
        this.totalExpense = totalExpense;
    }
}
