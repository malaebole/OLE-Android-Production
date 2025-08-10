package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleSmsData {

    @SerializedName("sms_id")
    @Expose
    private String smsId;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("paid_price")
    @Expose
    private String paidPrice;
    @SerializedName("sms_text")
    @Expose
    private String smsText;
    @SerializedName("sending_date")
    @Expose
    private String sendingDate;
    @SerializedName("sending_time")
    @Expose
    private String sendingTime;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("club_id")
    @Expose
    private String clubId;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("sms_for")
    @Expose
    private String smsFor;
    @SerializedName("total_sms")
    @Expose
    private String totalSms;

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(String paidPrice) {
        this.paidPrice = paidPrice;
    }

    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

    public String getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(String sendingDate) {
        this.sendingDate = sendingDate;
    }

    public String getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(String sendingTime) {
        this.sendingTime = sendingTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSmsFor() {
        return smsFor;
    }

    public void setSmsFor(String smsFor) {
        this.smsFor = smsFor;
    }

    public String getTotalSms() {
        return totalSms;
    }

    public void setTotalSms(String totalSms) {
        this.totalSms = totalSms;
    }
}