package ae.oleapp.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClubBankLists {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("account_title")
    @Expose
    private String accountTitle;
    @SerializedName("account_number")
    @Expose
    private String accountNumber;
    @SerializedName("iban_number")
    @Expose
    private String ibanNumber;
    @SerializedName("branch_name")
    @Expose
    private String branchName;
    @SerializedName("bank_city")
    @Expose
    private String bankCity;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("account_type")
    @Expose
    private String accountType;
    @SerializedName("added_date")
    @Expose
    private String addedDate;
    @SerializedName("total_expenses")
    @Expose
    private String totalExpenses;
    @SerializedName("total_earnings")
    @Expose
    private String totalEarnings;
    @SerializedName("currency")
    @Expose
    private String currency;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountTitle() {
        return accountTitle;
    }

    public void setAccountTitle(String accountTitle) {
        this.accountTitle = accountTitle;
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

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBankCity() {
        return bankCity;
    }

    public void setBankCity(String bankCity) {
        this.bankCity = bankCity;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(String totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public String getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(String totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
