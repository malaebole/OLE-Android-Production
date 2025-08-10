package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleFieldPrice {

    @SerializedName("day_id")
    @Expose
    private String dayId;
    @SerializedName("one_hour")
    @Expose
    private String oneHour;
    @SerializedName("one_half_hour")
    @Expose
    private String oneHalfHour;
    @SerializedName("two_hour")
    @Expose
    private String twoHour;
    @SerializedName("currency")
    @Expose
    private String currency;

    public String getDayId() {
        return dayId;
    }

    public void setDayId(String dayId) {
        this.dayId = dayId;
    }

    public String getOneHour() {
        return oneHour;
    }

    public void setOneHour(String oneHour) {
        this.oneHour = oneHour;
    }

    public String getOneHalfHour() {
        return oneHalfHour;
    }

    public void setOneHalfHour(String oneHalfHour) {
        this.oneHalfHour = oneHalfHour;
    }

    public String getTwoHour() {
        return twoHour;
    }

    public void setTwoHour(String twoHour) {
        this.twoHour = twoHour;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

}