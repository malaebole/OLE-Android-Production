package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleUserCard {

    @SerializedName("card_id")
    @Expose
    private String cardId;
    @SerializedName("card_name")
    @Expose
    private String cardName;
    @SerializedName("card_number")
    @Expose
    private String cardNumber;
    @SerializedName("card_token")
    @Expose
    private String cardToken;
    @SerializedName("card_expiry")
    @Expose
    private String cardExpiry;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public String getCardExpiry() {
        return cardExpiry;
    }

    public void setCardExpiry(String cardExpiry) {
        this.cardExpiry = cardExpiry;
    }

}