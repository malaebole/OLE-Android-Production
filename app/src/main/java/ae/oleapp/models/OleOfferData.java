package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleOfferData {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("offer_name")
    @Expose
    private String offerName;
    @SerializedName("details")
    @Expose
    private String details;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("offer_start")
    @Expose
    private String offerStart;
    @SerializedName("offer_expiry")
    @Expose
    private String offerExpiry;
    @SerializedName("timimg_start")
    @Expose
    private String timimgStart;
    @SerializedName("timing_end")
    @Expose
    private String timingEnd;
    @SerializedName("day_id")
    @Expose
    private String dayId;
    @SerializedName("club_name")
    @Expose
    private String clubName;
    @SerializedName("club_logo")
    @Expose
    private String clubLogo;
    @SerializedName("field_name")
    @Expose
    private String fieldName;
    @SerializedName("field_size")
    @Expose
    private String fieldSize;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("discount_type")
    @Expose
    private String discountType;
    @SerializedName("club_id")
    @Expose
    private String clubId;
    @SerializedName("field_id")
    @Expose
    private String fieldId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOfferStart() {
        return offerStart;
    }

    public void setOfferStart(String offerStart) {
        this.offerStart = offerStart;
    }

    public String getOfferExpiry() {
        return offerExpiry;
    }

    public void setOfferExpiry(String offerExpiry) {
        this.offerExpiry = offerExpiry;
    }

    public String getTimimgStart() {
        return timimgStart;
    }

    public void setTimimgStart(String timimgStart) {
        this.timimgStart = timimgStart;
    }

    public String getTimingEnd() {
        return timingEnd;
    }

    public void setTimingEnd(String timingEnd) {
        this.timingEnd = timingEnd;
    }

    public String getDayId() {
        return dayId;
    }

    public void setDayId(String dayId) {
        this.dayId = dayId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubLogo() {
        return clubLogo;
    }

    public void setClubLogo(String clubLogo) {
        this.clubLogo = clubLogo;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldSize() {
        return fieldSize;
    }

    public void setFieldSize(String fieldSize) {
        this.fieldSize = fieldSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }
}