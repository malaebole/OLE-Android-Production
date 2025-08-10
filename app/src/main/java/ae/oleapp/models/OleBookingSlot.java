package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OleBookingSlot {

    @SerializedName("booking_id")
    @Expose
    private String bookingId;
    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("end")
    @Expose
    private String end;
    @SerializedName("slot")
    @Expose
    private String slot;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("shift")
    @Expose
    private String shift;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_phone")
    @Expose
    private String userPhone;
    @SerializedName("booking_status")
    @Expose
    private String bookingStatus;
    @SerializedName("is_schedule")
    @Expose
    private String schedule;
    @SerializedName("is_selected")
    @Expose
    private String isSelected;
    @SerializedName("lady_slot")
    @Expose
    private String ladySlot;
    private String slotId = "";
    @SerializedName("waiting_list")
    @Expose
    private List<BookingWaitingList> waitingList;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(String isSelected) {
        this.isSelected = isSelected;
    }

    public String getLadySlot() {
        return ladySlot;
    }

    public void setLadySlot(String ladySlot) {
        this.ladySlot = ladySlot;
    }

    public List<BookingWaitingList> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(List<BookingWaitingList> waitingList) {
        this.waitingList = waitingList;
    }
}