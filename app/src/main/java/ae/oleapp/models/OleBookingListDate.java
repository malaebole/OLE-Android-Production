package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleBookingListDate {

    @SerializedName("booking_available")
    @Expose
    private String bookingAvailable;
    @SerializedName("hidden_slots")
    @Expose
    private String hiddenSlots;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("lady_slot")
    @Expose
    private String ladySlot;

    public String getBookingAvailable() {
        return bookingAvailable;
    }

    public void setBookingAvailable(String bookingAvailable) {
        this.bookingAvailable = bookingAvailable;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHiddenSlots() {
        return hiddenSlots;
    }

    public void setHiddenSlots(String hiddenSlots) {
        this.hiddenSlots = hiddenSlots;
    }

    public String getLadySlot() {
        return ladySlot;
    }

    public void setLadySlot(String ladySlot) {
        this.ladySlot = ladySlot;
    }
}