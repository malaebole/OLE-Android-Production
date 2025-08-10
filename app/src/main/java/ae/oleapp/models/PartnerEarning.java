package ae.oleapp.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class PartnerEarning {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("photo_url")
    @Expose
    private String photoUrl;
    @SerializedName("shares")
    @Expose
    private String shares;
    @SerializedName("shares_amount")
    @Expose
    private String sharesAmount;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public String getSharesAmount() {
        return sharesAmount;
    }

    public void setSharesAmount(String sharesAmount) {
        this.sharesAmount = sharesAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
