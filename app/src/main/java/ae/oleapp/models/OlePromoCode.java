package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OlePromoCode {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("coupon_title")
    @Expose
    private String couponTitle;
    @SerializedName("coupon_code")
    @Expose
    private String couponCode;
    @SerializedName("start_from")
    @Expose
    private String startFrom;
    @SerializedName("expiry")
    @Expose
    private String expiry;
    @SerializedName("discount_type")
    @Expose
    private String discountType;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("usage_limit")
    @Expose
    private String usageLimit;
    @SerializedName("fields")
    @Expose
    private String fields;
    @SerializedName("club_id")
    @Expose
    private String clubId;
    @SerializedName("club_name")
    @Expose
    private String clubName;
    @SerializedName("club_logo")
    @Expose
    private String clubLogo;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("used_times")
    @Expose
    private String usedTimes;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("player_usage_limit")
    @Expose
    private String playerUsageLimit;
    @SerializedName("player_list")
    @Expose
    private List<OlePlayerInfo> playerList;
    @SerializedName("one_hour_price")
    @Expose
    private String oneHourPrice;
    @SerializedName("one_half_hour_price")
    @Expose
    private String oneHalfHourPrice;
    @SerializedName("two_hour_price")
    @Expose
    private String twoHourPrice;
    @SerializedName("one_hour_discount")
    @Expose
    private String oneHourDiscount;
    @SerializedName("one_half_hour_discount")
    @Expose
    private String oneHalfHourDiscount;
    @SerializedName("two_hour_discount")
    @Expose
    private String twoHourDiscount;
    @SerializedName("promo_image")
    @Expose
    private String promoImage;
    @SerializedName("club_type")
    @Expose
    private String clubType;
    @SerializedName("pending_bookings")
    @Expose
    private String pendingBookings;
    @SerializedName("completed_bookings")
    @Expose
    private String completedBookings;
    @SerializedName("player_payment")
    @Expose
    private String playerPayment;
    @SerializedName("players_count")
    @Expose
    private String playersCount;
    @SerializedName("new_players")
    @Expose
    private String newPlayers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCouponTitle() {
        return couponTitle;
    }

    public void setCouponTitle(String couponTitle) {
        this.couponTitle = couponTitle;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(String startFrom) {
        this.startFrom = startFrom;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(String usageLimit) {
        this.usageLimit = usageLimit;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsedTimes() {
        return usedTimes;
    }

    public void setUsedTimes(String usedTimes) {
        this.usedTimes = usedTimes;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPlayerUsageLimit() {
        return playerUsageLimit;
    }

    public void setPlayerUsageLimit(String playerUsageLimit) {
        this.playerUsageLimit = playerUsageLimit;
    }

    public List<OlePlayerInfo> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<OlePlayerInfo> playerList) {
        this.playerList = playerList;
    }

    public String getOneHourPrice() {
        return oneHourPrice;
    }

    public void setOneHourPrice(String oneHourPrice) {
        this.oneHourPrice = oneHourPrice;
    }

    public String getOneHalfHourPrice() {
        return oneHalfHourPrice;
    }

    public void setOneHalfHourPrice(String oneHalfHourPrice) {
        this.oneHalfHourPrice = oneHalfHourPrice;
    }

    public String getTwoHourPrice() {
        return twoHourPrice;
    }

    public void setTwoHourPrice(String twoHourPrice) {
        this.twoHourPrice = twoHourPrice;
    }

    public String getOneHourDiscount() {
        return oneHourDiscount;
    }

    public void setOneHourDiscount(String oneHourDiscount) {
        this.oneHourDiscount = oneHourDiscount;
    }

    public String getOneHalfHourDiscount() {
        return oneHalfHourDiscount;
    }

    public void setOneHalfHourDiscount(String oneHalfHourDiscount) {
        this.oneHalfHourDiscount = oneHalfHourDiscount;
    }

    public String getTwoHourDiscount() {
        return twoHourDiscount;
    }

    public void setTwoHourDiscount(String twoHourDiscount) {
        this.twoHourDiscount = twoHourDiscount;
    }

    public String getPromoImage() {
        return promoImage;
    }

    public void setPromoImage(String promoImage) {
        this.promoImage = promoImage;
    }

    public String getClubType() {
        return clubType;
    }

    public void setClubType(String clubType) {
        this.clubType = clubType;
    }

    public String getPendingBookings() {
        return pendingBookings;
    }

    public void setPendingBookings(String pendingBookings) {
        this.pendingBookings = pendingBookings;
    }

    public String getCompletedBookings() {
        return completedBookings;
    }

    public void setCompletedBookings(String completedBookings) {
        this.completedBookings = completedBookings;
    }

    public String getPlayerPayment() {
        return playerPayment;
    }

    public void setPlayerPayment(String playerPayment) {
        this.playerPayment = playerPayment;
    }

    public String getPlayersCount() {
        return playersCount;
    }

    public void setPlayersCount(String playersCount) {
        this.playersCount = playersCount;
    }

    public String getNewPlayers() {
        return newPlayers;
    }

    public void setNewPlayers(String newPlayers) {
        this.newPlayers = newPlayers;
    }
}
