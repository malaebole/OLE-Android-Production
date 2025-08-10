package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleHideField {

    @SerializedName("hidden_ids")
    @Expose
    private String hiddenIds;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("time_slots")
    @Expose
    private String timeSlots;
    @SerializedName("hidden_type")
    @Expose
    private String hiddenType;
    @SerializedName("reason")
    @Expose
    private String reason;

    public String getHiddenIds() {
        return hiddenIds;
    }

    public void setHiddenIds(String hiddenIds) {
        this.hiddenIds = hiddenIds;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(String timeSlots) {
        this.timeSlots = timeSlots;
    }

    public String getHiddenType() {
        return hiddenType;
    }

    public void setHiddenType(String hiddenType) {
        this.hiddenType = hiddenType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
