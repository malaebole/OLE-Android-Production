package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OleEarningData {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("earning_data")
    @Expose
    private List<Earning> earningData = null;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Earning> getEarnings() {
        return earningData;
    }

    public void setEarnings(List<Earning> earningData) {
        this.earningData = earningData;
    }
}
