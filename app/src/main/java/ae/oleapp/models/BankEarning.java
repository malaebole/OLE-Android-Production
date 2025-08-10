package ae.oleapp.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class BankEarning {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("bank_name")
    @Expose
    private String bankName;
    @SerializedName("photo_url")
    @Expose
    private String photoUrl;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("currency")
    @Expose
    private String currency;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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
}
