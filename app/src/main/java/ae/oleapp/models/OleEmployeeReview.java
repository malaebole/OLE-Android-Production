package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleEmployeeReview {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("booking_id")
    @Expose
    private String bookingId;
    @SerializedName("booking_field")
    @Expose
    private String bookingField;
    @SerializedName("player_id")
    @Expose
    private String playerId;
    @SerializedName("player_name")
    @Expose
    private String playerName;
    @SerializedName("player_phone")
    @Expose
    private String playerPhone;
    @SerializedName("player_photo")
    @Expose
    private String playerPhoto;
    @SerializedName("tip_amount")
    @Expose
    private String tipAmount;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("stars")
    @Expose
    private String stars;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("times_ago")
    @Expose
    private String timesAgo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingField() {
        return bookingField;
    }

    public void setBookingField(String bookingField) {
        this.bookingField = bookingField;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerPhone() {
        return playerPhone;
    }

    public void setPlayerPhone(String playerPhone) {
        this.playerPhone = playerPhone;
    }

    public String getPlayerPhoto() {
        return playerPhoto;
    }

    public void setPlayerPhoto(String playerPhoto) {
        this.playerPhoto = playerPhoto;
    }

    public String getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(String tipAmount) {
        this.tipAmount = tipAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimesAgo() {
        return timesAgo;
    }

    public void setTimesAgo(String timesAgo) {
        this.timesAgo = timesAgo;
    }

}
