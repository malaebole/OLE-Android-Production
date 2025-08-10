package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OlePlayerBookingList {

    @SerializedName("booking_id")
    @Expose
    private String bookingId;
    @SerializedName("booking_date")
    @Expose
    private String bookingDate;
    @SerializedName("booking_time")
    @Expose
    private String bookingTime;
    @SerializedName("booking_price")
    @Expose
    private String bookingPrice;
    @SerializedName("match_fee")
    @Expose
    private String matchFee;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("created_by")
    @Expose
    private UserInfo createdBy;
    @SerializedName("club_name")
    @Expose
    private String clubName;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("club_latitude")
    @Expose
    private String clubLatitude;
    @SerializedName("club_longitude")
    @Expose
    private String clubLongitude;
    @SerializedName("match_allowed")
    @Expose
    private String matchAllowed;
    @SerializedName("field_name")
    @Expose
    private String fieldName;
    @SerializedName("field_size")
    @Expose
    private String fieldSize;
    @SerializedName("field_type")
    @Expose
    private String fieldType;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("joining_fee")
    @Expose
    private String joiningFee;
    @SerializedName("total_players")
    @Expose
    private String totalPlayers;
    @SerializedName("remaining_players")
    @Expose
    private String remainingPlayers;
    @SerializedName("booking_type")
    @Expose
    private String bookingType;
    @SerializedName("my_status")
    @Expose
    private String myStatus;
    @SerializedName("request_status")
    @Expose
    private String requestStatus;
    @SerializedName("joined_players")
    @Expose
    private List<OlePlayerInfo> joinedPlayers = null;
    @SerializedName("requested_players")
    @Expose
    private List<OlePlayerInfo> requestedPlayers = null;
    @SerializedName("booking_status")
    @Expose
    private String bookingStatus;
    @SerializedName("invoice_no")
    @Expose
    private String invoiceNo;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("facilities")
    @Expose
    private List<OleClubFacility> facilities = null;
    @SerializedName("club_facilities")
    @Expose
    private List<OleClubFacility> clubFacilities = null;
    @SerializedName("game_teams")
    @Expose
    private OleGameTeam oleGameTeams;
    @SerializedName("maunal_players")
    @Expose
    private List<OlePlayerInfo> maunalPlayers = null;
    @SerializedName("player_two")
    @Expose
    private UserInfo playerTwo;
    @SerializedName("age_limit")
    @Expose
    private String ageLimit;
    @SerializedName("remaining_amount")
    @Expose
    private String remainingAmount;
    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;
    @SerializedName("club_id")
    @Expose
    private String clubId;
    @SerializedName("paid_amount")
    @Expose
    private String paidAmount;
    @SerializedName("unpaid_amount")
    @Expose
    private String unpaidAmount;
    @SerializedName("unread_chat_count")
    @Expose
    private String unreadChatCount;
    @SerializedName("games_allowed")
    @Expose
    private String gamesAllowed;
    @SerializedName("show_to_favorites")
    @Expose
    private String showToFavorites;
    @SerializedName("field_photo")
    @Expose
    private String fieldPhoto;
    @SerializedName("partner_count")
    @Expose
    private Integer partnerCount;
    @SerializedName("creator_partner")
    @Expose
    private OlePlayerInfo creatorPartner;
    @SerializedName("player_two_partner")
    @Expose
    private OlePlayerInfo playerTwoPartner;
    @SerializedName("requested_players_count")
    @Expose
    private String requestedPlayersCount;
    @SerializedName("club_payment_method")
    @Expose
    private String clubPaymentMethod;
    @SerializedName("skill_level_id")
    @Expose
    private String skillLevelId;
    @SerializedName("skill_level")
    @Expose
    private String skillLevel;
    @SerializedName("group")
    @Expose
    private OlePlayersGroup olePlayersGroup;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
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

    public String getMatchFee() {
        return matchFee;
    }

    public void setMatchFee(String matchFee) {
        this.matchFee = matchFee;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public UserInfo getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserInfo createdBy) {
        this.createdBy = createdBy;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getClubLatitude() {
        return clubLatitude;
    }

    public void setClubLatitude(String clubLatitude) {
        this.clubLatitude = clubLatitude;
    }

    public String getClubLongitude() {
        return clubLongitude;
    }

    public void setClubLongitude(String clubLongitude) {
        this.clubLongitude = clubLongitude;
    }

    public String getMatchAllowed() {
        return matchAllowed;
    }

    public void setMatchAllowed(String matchAllowed) {
        this.matchAllowed = matchAllowed;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getJoiningFee() {
        return joiningFee;
    }

    public void setJoiningFee(String joiningFee) {
        this.joiningFee = joiningFee;
    }

    public String getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(String totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public String getRemainingPlayers() {
        return remainingPlayers;
    }

    public void setRemainingPlayers(String remainingPlayers) {
        this.remainingPlayers = remainingPlayers;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getMyStatus() {
        return myStatus;
    }

    public void setMyStatus(String myStatus) {
        this.myStatus = myStatus;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
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

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<OleClubFacility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<OleClubFacility> facilities) {
        this.facilities = facilities;
    }

    public List<OleClubFacility> getClubFacilities() {
        return clubFacilities;
    }

    public void setClubFacilities(List<OleClubFacility> clubFacilities) {
        this.clubFacilities = clubFacilities;
    }

    public OleGameTeam getGameTeams() {
        return oleGameTeams;
    }

    public void setGameTeams(OleGameTeam oleGameTeams) {
        this.oleGameTeams = oleGameTeams;
    }

    public List<OlePlayerInfo> getMaunalPlayers() {
        return maunalPlayers;
    }

    public void setMaunalPlayers(List<OlePlayerInfo> maunalPlayers) {
        this.maunalPlayers = maunalPlayers;
    }

    public UserInfo getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(UserInfo playerTwo) {
        this.playerTwo = playerTwo;
    }

    public String getAgeLimit() {
        return ageLimit;
    }

    public void setAgeLimit(String ageLimit) {
        this.ageLimit = ageLimit;
    }

    public String getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(String remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
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

    public String getUnreadChatCount() {
        return unreadChatCount;
    }

    public void setUnreadChatCount(String unreadChatCount) {
        this.unreadChatCount = unreadChatCount;
    }

    public String getGamesAllowed() {
        return gamesAllowed;
    }

    public void setGamesAllowed(String gamesAllowed) {
        this.gamesAllowed = gamesAllowed;
    }

    public String getShowToFavorites() {
        return showToFavorites;
    }

    public void setShowToFavorites(String showToFavorites) {
        this.showToFavorites = showToFavorites;
    }

    public String getFieldPhoto() {
        return fieldPhoto;
    }

    public void setFieldPhoto(String fieldPhoto) {
        this.fieldPhoto = fieldPhoto;
    }

    public Integer getPartnerCount() {
        return partnerCount;
    }

    public void setPartnerCount(Integer partnerCount) {
        this.partnerCount = partnerCount;
    }

    public OlePlayerInfo getCreatorPartner() {
        return creatorPartner;
    }

    public void setCreatorPartner(OlePlayerInfo creatorPartner) {
        this.creatorPartner = creatorPartner;
    }

    public OlePlayerInfo getPlayerTwoPartner() {
        return playerTwoPartner;
    }

    public void setPlayerTwoPartner(OlePlayerInfo playerTwoPartner) {
        this.playerTwoPartner = playerTwoPartner;
    }

    public String getRequestedPlayersCount() {
        return requestedPlayersCount;
    }

    public void setRequestedPlayersCount(String requestedPlayersCount) {
        this.requestedPlayersCount = requestedPlayersCount;
    }

    public String getClubPaymentMethod() {
        return clubPaymentMethod;
    }

    public void setClubPaymentMethod(String clubPaymentMethod) {
        this.clubPaymentMethod = clubPaymentMethod;
    }

    public String getSkillLevelId() {
        return skillLevelId;
    }

    public void setSkillLevelId(String skillLevelId) {
        this.skillLevelId = skillLevelId;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public OlePlayersGroup getPlayersGroup() {
        return olePlayersGroup;
    }

    public void setPlayersGroup(OlePlayersGroup olePlayersGroup) {
        this.olePlayersGroup = olePlayersGroup;
    }
}