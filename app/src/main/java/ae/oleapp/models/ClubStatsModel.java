package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClubStatsModel {

    @SerializedName("earnings_total")
    @Expose
    private String earningsTotal;
    @SerializedName("facilities_total")
    @Expose
    private String facilitiesTotal;
    @SerializedName("discount_total")
    @Expose
    private String discountTotal;
    @SerializedName("pos_machine_total")
    @Expose
    private String posMachineTotal;
    @SerializedName("online_total")
    @Expose
    private String onlineTotal;
    @SerializedName("match_fee_total")
    @Expose
    private String matchFeeTotal;
    @SerializedName("total_hours")
    @Expose
    private String totalHours;
    @SerializedName("completed_count")
    @Expose
    private String completedCount;
    @SerializedName("canceled_count")
    @Expose
    private String canceledCount;
    @SerializedName("matches_count")
    @Expose
    private String matchesCount;
    @SerializedName("games_count")
    @Expose
    private String gamesCount;
    @SerializedName("new_users")
    @Expose
    private String newUsers;
    @SerializedName("expenses_total")
    @Expose
    private String expensesTotal;
    @SerializedName("profit_total")
    @Expose
    private String profitTotal;
    @SerializedName("currency")
    @Expose
    private String currency;

    public String getEarningsTotal() {
        return earningsTotal;
    }

    public void setEarningsTotal(String earningsTotal) {
        this.earningsTotal = earningsTotal;
    }

    public String getFacilitiesTotal() {
        return facilitiesTotal;
    }

    public void setFacilitiesTotal(String facilitiesTotal) {
        this.facilitiesTotal = facilitiesTotal;
    }

    public String getDiscountTotal() {
        return discountTotal;
    }

    public void setDiscountTotal(String discountTotal) {
        this.discountTotal = discountTotal;
    }

    public String getPosMachineTotal() {
        return posMachineTotal;
    }

    public void setPosMachineTotal(String posMachineTotal) {
        this.posMachineTotal = posMachineTotal;
    }

    public String getOnlineTotal() {
        return onlineTotal;
    }

    public void setOnlineTotal(String onlineTotal) {
        this.onlineTotal = onlineTotal;
    }

    public String getMatchFeeTotal() {
        return matchFeeTotal;
    }

    public void setMatchFeeTotal(String matchFeeTotal) {
        this.matchFeeTotal = matchFeeTotal;
    }

    public String getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(String totalHours) {
        this.totalHours = totalHours;
    }

    public String getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(String completedCount) {
        this.completedCount = completedCount;
    }

    public String getCanceledCount() {
        return canceledCount;
    }

    public void setCanceledCount(String canceledCount) {
        this.canceledCount = canceledCount;
    }

    public String getMatchesCount() {
        return matchesCount;
    }

    public void setMatchesCount(String matchesCount) {
        this.matchesCount = matchesCount;
    }

    public String getGamesCount() {
        return gamesCount;
    }

    public void setGamesCount(String gamesCount) {
        this.gamesCount = gamesCount;
    }

    public String getNewUsers() {
        return newUsers;
    }

    public void setNewUsers(String newUsers) {
        this.newUsers = newUsers;
    }

    public String getExpensesTotal() {
        return expensesTotal;
    }

    public void setExpensesTotal(String expensesTotal) {
        this.expensesTotal = expensesTotal;
    }

    public String getProfitTotal() {
        return profitTotal;
    }

    public void setProfitTotal(String profitTotal) {
        this.profitTotal = profitTotal;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
