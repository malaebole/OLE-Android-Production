package ae.oleapp.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OlePlayerMatch {

    @SerializedName("match_id")
    @Expose
    private String matchId;
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
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("field_name")
    @Expose
    private String fieldName;
    @SerializedName("field_size")
    @Expose
    private String fieldSize;
    @SerializedName("club_name")
    @Expose
    private String clubName;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("booking_type")
    @Expose
    private String bookingType;
    @SerializedName("joining_fee")
    @Expose
    private String joiningFee;
    @SerializedName("total_players")
    @Expose
    private String totalPlayers;
    @SerializedName("remaining_players")
    @Expose
    private String remainingPlayers;
    @SerializedName("joined_players")
    @Expose
    private List<OlePlayerInfo> joinedPlayers = null;
    @SerializedName("requested_players")
    @Expose
    private List<OlePlayerInfo> requestedPlayers = null;
    @SerializedName("created_by")
    @Expose
    private UserInfo createdBy;
    @SerializedName("player_two")
    @Expose
    private UserInfo playerTwo;
    @SerializedName("my_status")
    @Expose
    private String myStatus;
    @SerializedName("request_status")
    @Expose
    private String requestStatus;
    @SerializedName("match_type")
    @Expose
    private String matchType;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("match_fee")
    @Expose
    private String matchFee;
    @SerializedName("facilities")
    @Expose
    private List<OleClubFacility> facilities = null;
    @SerializedName("club_latitude")
    @Expose
    private String clubLatitude;
    @SerializedName("club_longitude")
    @Expose
    private String clubLongitude;
    @SerializedName("game_teams")
    @Expose
    private OleGameTeam oleGameTeam;
    @SerializedName("maunal_players")
    @Expose
    private List<OlePlayerInfo> manualPlayers = null;
    @SerializedName("booking_status")
    @Expose
    private String bookingStatus;
    @SerializedName("age_limit")
    @Expose
    private String ageLimit;
    @SerializedName("club_id")
    @Expose
    private String clubId;
    @SerializedName("unread_chat_count")
    @Expose
    private String unreadChatCount;
    @SerializedName("creator_partner")
    @Expose
    private OlePlayerInfo creatorPartner;
    @SerializedName("player_two_partner")
    @Expose
    private OlePlayerInfo playerTwoPartner;
    @SerializedName("skill_level_id")
    @Expose
    private String skillLevelId;
    @SerializedName("skill_level")
    @Expose
    private String skillLevel;
    @SerializedName("club_payment_method")
    @Expose
    private String clubPaymentMethod;

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
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

    public UserInfo getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserInfo createdBy) {
        this.createdBy = createdBy;
    }

    public UserInfo getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(UserInfo playerTwo) {
        this.playerTwo = playerTwo;
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

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getMatchFee() {
        return matchFee;
    }

    public void setMatchFee(String matchFee) {
        this.matchFee = matchFee;
    }

    public List<OleClubFacility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<OleClubFacility> facilities) {
        this.facilities = facilities;
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

    public OleGameTeam getGameTeam() {
        return oleGameTeam;
    }

    public void setGameTeam(OleGameTeam oleGameTeam) {
        this.oleGameTeam = oleGameTeam;
    }

    public List<OlePlayerInfo> getManualPlayers() {
        return manualPlayers;
    }

    public void setManualPlayers(List<OlePlayerInfo> manualPlayers) {
        this.manualPlayers = manualPlayers;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getAgeLimit() {
        return ageLimit;
    }

    public void setAgeLimit(String ageLimit) {
        this.ageLimit = ageLimit;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getUnreadChatCount() {
        return unreadChatCount;
    }

    public void setUnreadChatCount(String unreadChatCount) {
        this.unreadChatCount = unreadChatCount;
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

    public String getClubPaymentMethod() {
        return clubPaymentMethod;
    }

    public void setClubPaymentMethod(String clubPaymentMethod) {
        this.clubPaymentMethod = clubPaymentMethod;
    }
}
