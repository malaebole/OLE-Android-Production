package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ae.oleapp.util.expandablerecyclerviewadapter.BaseExpandableRecyclerViewAdapter;

public class OlePlayerBalance implements BaseExpandableRecyclerViewAdapter.BaseGroupBean<OlePlayerBalanceDetail> {

    @SerializedName("id")
    @Expose
    private String bookingId;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("added_by")
    @Expose
    private String addedBy;
    @SerializedName("player_id")
    @Expose
    private String playerId;
    @SerializedName("player_name")
    @Expose
    private String playerName;
    @SerializedName("player_phone")
    @Expose
    private String playerPhone;
    @SerializedName("is_discount")
    @Expose
    private String isDiscount;
    @SerializedName("receipt")
    @Expose
    private String receipt;
    @SerializedName("balance_details")
    @Expose
    private List<OlePlayerBalanceDetail> balanceDetails = null;
    @SerializedName("booking_type")
    @Expose
    private String bookingType;
    @SerializedName("club_name")
    @Expose
    private String clubName;
    @SerializedName("club_type")
    @Expose
    private String clubType;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String id) {
        this.bookingId = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerPhone() {
        return playerPhone;
    }

    public void setPlayerPhone(String playerPhone) {
        this.playerPhone = playerPhone;
    }

    public String getIsDiscount() {
        return isDiscount;
    }

    public void setIsDiscount(String isDiscount) {
        this.isDiscount = isDiscount;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public List<OlePlayerBalanceDetail> getBalanceDetails() {
        return balanceDetails;
    }

    public void setBalanceDetails(List<OlePlayerBalanceDetail> balanceDetails) {
        this.balanceDetails = balanceDetails;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubType() {
        return clubType;
    }

    public void setClubType(String clubType) {
        this.clubType = clubType;
    }

    @Override
    public int getChildCount() {
        return balanceDetails.size();
    }

    @Override
    public OlePlayerBalanceDetail getChildAt(int childIndex) {
        return balanceDetails.size() <= childIndex ? null : balanceDetails.get(childIndex);
    }

    @Override
    public boolean isExpandable() {
        return getChildCount() > 0;
    }
}
