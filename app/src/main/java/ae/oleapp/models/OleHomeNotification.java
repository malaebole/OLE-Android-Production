package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleHomeNotification {

    @SerializedName("actual_price")
    @Expose
    private String actualPrice;
    @SerializedName("booking_date")
    @Expose
    private String bookingDate;
    @SerializedName("booking_id")
    @Expose
    private String bookingId;
    @SerializedName("booking_time")
    @Expose
    private String bookingTime;
    @SerializedName("booking_type")
    @Expose
    private String bookingType;
    @SerializedName("club_id")
    @Expose
    private String clubId;
    @SerializedName("club_name")
    @Expose
    private String clubName;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("discount_type")
    @Expose
    private String discountType;
    @SerializedName("discount_value")
    @Expose
    private String discountValue;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("offer_type")
    @Expose
    private String offerType;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("product_photo")
    @Expose
    private String productPhoto;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("club_logo")
    @Expose
    private String clubLogo;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("by")
    @Expose
    private OlePlayerInfo by;
    @SerializedName("request_status")
    @Expose
    private String requestStatus;
    @SerializedName("team_name")
    @Expose
    private String teamName;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("day")
    @Expose
    private String day;

    public String getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(String actualPrice) {
        this.actualPrice = actualPrice;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPhoto() {
        return productPhoto;
    }

    public void setProductPhoto(String productPhoto) {
        this.productPhoto = productPhoto;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClubLogo() {
        return clubLogo;
    }

    public void setClubLogo(String clubLogo) {
        this.clubLogo = clubLogo;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public OlePlayerInfo getBy() {
        return by;
    }

    public void setBy(OlePlayerInfo by) {
        this.by = by;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}