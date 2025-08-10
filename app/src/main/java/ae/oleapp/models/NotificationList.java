package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationList {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("sender")
    @Expose
    private PlayerInfo sender;
    @SerializedName("booking_id")
    @Expose
    private String bookingId;
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
    @SerializedName("is_lineup")
    @Expose
    private String isLineup;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("game_id")
    @Expose
    private String gameId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PlayerInfo getSender() {
        return sender;
    }

    public void setSender(PlayerInfo sender) {
        this.sender = sender;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
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

    public String getIsLineup() {
        return isLineup;
    }

    public void setIsLineup(String isLineup) {
        this.isLineup = isLineup;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
