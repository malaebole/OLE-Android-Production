package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OleBookingList {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("booking_date")
    @Expose
    private String bookingDate;
    @SerializedName("booking_time")
    @Expose
    private String bookingTime;
    @SerializedName("booking_price")
    @Expose
    private String bookingPrice;
    @SerializedName("balance_amount")
    @Expose
    private String balanceAmount;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("user")
    @Expose
    private UserInfo user;
    @SerializedName("field_id")
    @Expose
    private String fieldId;
    @SerializedName("field_name")
    @Expose
    private String fieldName;
    @SerializedName("field_size")
    @Expose
    private String fieldSize;
    @SerializedName("field_color")
    @Expose
    private String fieldColor;
    @SerializedName("club_name")
    @Expose
    private String clubName;
    @SerializedName("club_logo")
    @Expose
    private String clubLogo;
    @SerializedName("sort")
    @Expose
    private String sort;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("call_booking")
    @Expose
    private String callBooking;
    @SerializedName("booking_type")
    @Expose
    private String bookingType;
    @SerializedName("is_match")
    @Expose
    private String isMatch;
    @SerializedName("invoice_no")
    @Expose
    private String invoiceNo;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("match_fee")
    @Expose
    private String matchFee;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;
    @SerializedName("joined_players")
    @Expose
    private List<OlePlayerInfo> joinedPlayers = null;
    @SerializedName("requested_players")
    @Expose
    private List<OlePlayerInfo> requestedPlayers = null;
    @SerializedName("game_teams")
    @Expose
    private OleGameTeam oleGameTeam;
    @SerializedName("club_facilities")
    @Expose
    private List<OleClubFacility> clubFacilities = null;
    @SerializedName("facilities")
    @Expose
    private List<OleClubFacility> facilities = null;
    @SerializedName("player_two")
    @Expose
    private OlePlayerInfo playerTwo;
    @SerializedName("discount_type")
    @Expose
    private String discountType;
    @SerializedName("club_id")
    @Expose
    private String clubId;
    @SerializedName("reservation_details")
    @Expose
    private OleReservationDetail oleReservationDetails;
    @SerializedName("canceled_hours")
    @Expose
    private String canceledHours;
    @SerializedName("paid_amount")
    @Expose
    private String paidAmount;
    @SerializedName("unpaid_amount")
    @Expose
    private String unpaidAmount;
    @SerializedName("booking_field_type")
    @Expose
    private String bookingFieldType;
    @SerializedName("field_photo")
    @Expose
    private String fieldPhoto;
    @SerializedName("promo_code")
    @Expose
    private String promoCode;
    @SerializedName("cash_paid")
    @Expose
    private String cashPaid;
    @SerializedName("pos_paid")
    @Expose
    private String posPaid;
    @SerializedName("card_paid")
    @Expose
    private String cardPaid;
    @SerializedName("wallet_paid")
    @Expose
    private String walletPaid;
    @SerializedName("total_paid")
    @Expose
    private String totalPaid;
    @SerializedName("match_score")
    @Expose
    private OleMatchScore oleMatchScore;
    @SerializedName("user_partner")
    @Expose
    private OlePlayerInfo userPartner;
    @SerializedName("player_two_partner")
    @Expose
    private OlePlayerInfo playerTwoPartner;
    @SerializedName("lady_slot")
    @Expose
    private String ladySlot;
    @SerializedName("total_players")
    @Expose
    private String totalPlayers;
    @SerializedName("time_ago")
    @Expose
    private String timeAgo;
    @SerializedName("pos_receipt")
    @Expose
    private String posReceipt;
    @SerializedName("pending_balance")
    @Expose
    private String pendingBalance;
    @SerializedName("paid_balance")
    @Expose
    private String paidBalance;
    @SerializedName("is_blocked")
    @Expose
    private String isBlocked;
    @SerializedName("blocked_by")
    @Expose
    private String blockedBy;
    @SerializedName("blocked_date")
    @Expose
    private String blockedDate;
    @SerializedName("blocked_reason")
    @Expose
    private String blockedReason;
    @SerializedName("slots_60")
    @Expose
    private String slots60;
    @SerializedName("slots_90")
    @Expose
    private String slots90;
    @SerializedName("slots_120")
    @Expose
    private String slots120;
    @SerializedName("invoice_url")
    @Expose
    private String invoiceUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getBookingPrice() {
        return bookingPrice;
    }

    public void setBookingPrice(String bookingPrice) {
        this.bookingPrice = bookingPrice;
    }
    public String getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(String balanceAmount) {
        this.balanceAmount = balanceAmount;
    }


    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
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

    public String getFieldColor() {
        return fieldColor;
    }

    public void setFieldColor(String fieldColor) {
        this.fieldColor = fieldColor;
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

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCallBooking() {
        return callBooking;
    }

    public void setCallBooking(String callBooking) {
        this.callBooking = callBooking;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getIsMatch() {
        return isMatch;
    }

    public void setIsMatch(String isMatch) {
        this.isMatch = isMatch;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getMatchFee() {
        return matchFee;
    }

    public void setMatchFee(String matchFee) {
        this.matchFee = matchFee;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public List<OlePlayerInfo> getJoinedPlayers() {
        return joinedPlayers;
    }

    public void setJoinedPlayers(List<OlePlayerInfo> joinedPlayers) {
        this.joinedPlayers = joinedPlayers;
    }

    public List<OlePlayerInfo> getRequestedPlayers() {
        return requestedPlayers;
    }

    public void setRequestedPlayers(List<OlePlayerInfo> requestedPlayers) {
        this.requestedPlayers = requestedPlayers;
    }

    public OleGameTeam getGameTeam() {
        return oleGameTeam;
    }

    public void setGameTeam(OleGameTeam oleGameTeams) {
        this.oleGameTeam = oleGameTeams;
    }

    public List<OleClubFacility> getClubFacilities() {
        return clubFacilities;
    }

    public void setClubFacilities(List<OleClubFacility> clubFacilities) {
        this.clubFacilities = clubFacilities;
    }

    public List<OleClubFacility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<OleClubFacility> facilities) {
        this.facilities = facilities;
    }

    public OlePlayerInfo getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(OlePlayerInfo playerTwo) {
        this.playerTwo = playerTwo;
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

    public OleReservationDetail getReservationDetails() {
        return oleReservationDetails;
    }

    public void setReservationDetails(OleReservationDetail oleReservationDetails) {
        this.oleReservationDetails = oleReservationDetails;
    }

    public String getCanceledHours() {
        return canceledHours;
    }

    public void setCanceledHours(String canceledHours) {
        this.canceledHours = canceledHours;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getUnpaidAmount() {
        return unpaidAmount;
    }

    public void setUnpaidAmount(String unpaidAmount) {
        this.unpaidAmount = unpaidAmount;
    }

    public String getBookingFieldType() {
        return bookingFieldType;
    }

    public void setBookingFieldType(String bookingFieldType) {
        this.bookingFieldType = bookingFieldType;
    }

    public String getFieldPhoto() {
        return fieldPhoto;
    }

    public void setFieldPhoto(String fieldPhoto) {
        this.fieldPhoto = fieldPhoto;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getCashPaid() {
        return cashPaid;
    }

    public void setCashPaid(String cashPaid) {
        this.cashPaid = cashPaid;
    }

    public String getPosPaid() {
        return posPaid;
    }

    public void setPosPaid(String posPaid) {
        this.posPaid = posPaid;
    }

    public String getCardPaid() {
        return cardPaid;
    }

    public void setCardPaid(String cardPaid) {
        this.cardPaid = cardPaid;
    }

    public String getWalletPaid() {
        return walletPaid;
    }

    public void setWalletPaid(String walletPaid) {
        this.walletPaid = walletPaid;
    }

    public String getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(String totalPaid) {
        this.totalPaid = totalPaid;
    }

    public OleMatchScore getMatchScore() {
        return oleMatchScore;
    }

    public void setMatchScore(OleMatchScore oleMatchScore) {
        this.oleMatchScore = oleMatchScore;
    }

    public OlePlayerInfo getUserPartner() {
        return userPartner;
    }

    public void setUserPartner(OlePlayerInfo userPartner) {
        this.userPartner = userPartner;
    }

    public OlePlayerInfo getPlayerTwoPartner() {
        return playerTwoPartner;
    }

    public void setPlayerTwoPartner(OlePlayerInfo playerTwoPartner) {
        this.playerTwoPartner = playerTwoPartner;
    }

    public String getLadySlot() {
        return ladySlot;
    }

    public void setLadySlot(String ladySlot) {
        this.ladySlot = ladySlot;
    }

    public String getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(String totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getPosReceipt() {
        return posReceipt;
    }

    public void setPosReceipt(String posReceipt) {
        this.posReceipt = posReceipt;
    }

    public String getPendingBalance() {
        return pendingBalance;
    }

    public void setPendingBalance(String pendingBalance) {
        this.pendingBalance = pendingBalance;
    }

    public String getPaidBalance() {
        return paidBalance;
    }

    public void setPaidBalance(String paidBalance) {
        this.paidBalance = paidBalance;
    }

    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(String blockedBy) {
        this.blockedBy = blockedBy;
    }

    public String getBlockedDate() {
        return blockedDate;
    }

    public void setBlockedDate(String blockedDate) {
        this.blockedDate = blockedDate;
    }

    public String getBlockedReason() {
        return blockedReason;
    }

    public void setBlockedReason(String blockedReason) {
        this.blockedReason = blockedReason;
    }

    public String getSlots60() {
        return slots60;
    }

    public void setSlots60(String slots60) {
        this.slots60 = slots60;
    }

    public String getSlots90() {
        return slots90;
    }

    public void setSlots90(String slots90) {
        this.slots90 = slots90;
    }

    public String getSlots120() {
        return slots120;
    }

    public void setSlots120(String slots120) {
        this.slots120 = slots120;
    }

    public String getInvoiceUrl() {
        return invoiceUrl;
    }

    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }
}