package ae.oleapp.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleWalletTransaction {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("history")
    @Expose
    private List<OleTransactionHistory> history = null;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<OleTransactionHistory> getHistory() {
        return history;
    }

    public void setHistory(List<OleTransactionHistory> history) {
        this.history = history;
    }

}
