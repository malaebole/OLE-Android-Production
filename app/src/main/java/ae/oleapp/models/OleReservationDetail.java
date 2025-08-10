package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleReservationDetail {

    @SerializedName("booking_by")
    @Expose
    private String bookingBy;
    @SerializedName("booking_added")
    @Expose
    private String bookingAdded;
    @SerializedName("player_confirmed")
    @Expose
    private String playerConfirmed;
    @SerializedName("player_canceled")
    @Expose
    private String playerCanceled;
    @SerializedName("owner_confirmed")
    @Expose
    private String ownerConfirmed;
    @SerializedName("owner_canceled")
    @Expose
    private String ownerCanceled;
    @SerializedName("employee_confirmed_at")
    @Expose
    private String employeeConfirmedAt;
    @SerializedName("confirmed_employee_name")
    @Expose
    private String confirmedEmployeeName;
    @SerializedName("employee_canceled_at")
    @Expose
    private String employeeCanceledAt;
    @SerializedName("canceled_employee_name")
    @Expose
    private String canceledEmployeeName;
    @SerializedName("completed_at")
    @Expose
    private String completedAt;
    @SerializedName("completed_by")
    @Expose
    private String completedBy;

    public String getBookingBy() {
        return bookingBy;
    }

    public void setBookingBy(String bookingBy) {
        this.bookingBy = bookingBy;
    }

    public String getBookingAdded() {
        return bookingAdded;
    }

    public void setBookingAdded(String bookingAdded) {
        this.bookingAdded = bookingAdded;
    }

    public String getPlayerConfirmed() {
        return playerConfirmed;
    }

    public void setPlayerConfirmed(String playerConfirmed) {
        this.playerConfirmed = playerConfirmed;
    }

    public String getPlayerCanceled() {
        return playerCanceled;
    }

    public void setPlayerCanceled(String playerCanceled) {
        this.playerCanceled = playerCanceled;
    }

    public String getOwnerConfirmed() {
        return ownerConfirmed;
    }

    public void setOwnerConfirmed(String ownerConfirmed) {
        this.ownerConfirmed = ownerConfirmed;
    }

    public String getOwnerCanceled() {
        return ownerCanceled;
    }

    public void setOwnerCanceled(String ownerCanceled) {
        this.ownerCanceled = ownerCanceled;
    }

    public String getEmployeeConfirmedAt() {
        return employeeConfirmedAt;
    }

    public void setEmployeeConfirmedAt(String employeeConfirmedAt) {
        this.employeeConfirmedAt = employeeConfirmedAt;
    }

    public String getConfirmedEmployeeName() {
        return confirmedEmployeeName;
    }

    public void setConfirmedEmployeeName(String confirmedEmployeeName) {
        this.confirmedEmployeeName = confirmedEmployeeName;
    }

    public String getEmployeeCanceledAt() {
        return employeeCanceledAt;
    }

    public void setEmployeeCanceledAt(String employeeCanceledAt) {
        this.employeeCanceledAt = employeeCanceledAt;
    }

    public String getCanceledEmployeeName() {
        return canceledEmployeeName;
    }

    public void setCanceledEmployeeName(String canceledEmployeeName) {
        this.canceledEmployeeName = canceledEmployeeName;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

}
