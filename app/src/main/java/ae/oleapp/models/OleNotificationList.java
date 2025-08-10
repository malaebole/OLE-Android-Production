package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleNotificationList {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("sender")
    @Expose
    private UserInfo sender;
    @SerializedName("booking_id")
    @Expose
    private String bookingId;
    @SerializedName("club")
    @Expose
    private Club club;
    @SerializedName("field_id")
    @Expose
    private String fieldId;
    @SerializedName("booking_type")
    @Expose
    private String bookingType;
    @SerializedName("offer_id")
    @Expose
    private String offerId;
    @SerializedName("is_rated")
    @Expose
    private String isRated;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("is_read")
    @Expose
    private String isRead;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("club_id")
    @Expose
    private String clubId;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("booking_field")
    @Expose
    private String bookingField;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserInfo getSender() {
        return sender;
    }

    public void setSender(UserInfo sender) {
        this.sender = sender;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getIsRated() {
        return isRated;
    }

    public void setIsRated(String isRated) {
        this.isRated = isRated;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBookingField() {
        return bookingField;
    }

    public void setBookingField(String bookingField) {
        this.bookingField = bookingField;
    }
}