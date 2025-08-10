package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OlePlayerInfo {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("nick_name")
    @Expose
    private String nickName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("birth_date")
    @Expose
    private String birthDate;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("photo_url")
    @Expose
    private String photoUrl;
    @SerializedName("goals")
    @Expose
    private String goals;
    @SerializedName("points")
    @Expose
    private String points;
    @SerializedName("match_won")
    @Expose
    private String matchWon;
    @SerializedName("match_loss")
    @Expose
    private String matchLoss;
    @SerializedName("match_played")
    @Expose
    private String matchPlayed;
    @SerializedName("match_drawn")
    @Expose
    private String matchDrawn;
    @SerializedName("violations")
    @Expose
    private String violations;
    @SerializedName("awards")
    @Expose
    private String awards;
    @SerializedName("level")
    @Expose
    private OlePlayerLevel level;
    @SerializedName("win_percentage")
    @Expose
    private String winPercentage;
    @SerializedName("games_ranking")
    @Expose
    private String gamesRanking;
    @SerializedName("reviews")
    @Expose
    private String reviews;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("total_bookings")
    @Expose
    private String totalBookings;
    @SerializedName("win_rank")
    @Expose
    private String winRank;
    @SerializedName("favorite")
    @Expose
    private String favorite;
    @SerializedName("friendly_games")
    @Expose
    private String friendlyGames;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("nationality")
    @Expose
    private String nationality;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("player_position")
    @Expose
    private OlePlayerPosition olePlayerPosition;
    @SerializedName("playing_level")
    @Expose
    private String playingLevel;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("is_rated")
    @Expose
    private String isRated;
    @SerializedName("match_status")
    @Expose
    private String matchStatus;
    @SerializedName("is_captain")
    @Expose
    private String isCaptain;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("x_coordinate")
    @Expose
    private String xCoordinate;
    @SerializedName("y_coordinate")
    @Expose
    private String yCoordinate;
    @SerializedName("player_confirmed")
    @Expose
    private String playerConfirmed;
    @SerializedName("total_hours")
    @Expose
    private String totalHours;
    @SerializedName("paid_amount")
    @Expose
    private String paidAmount;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;
    @SerializedName("my_partner")
    @Expose
    private OlePlayerInfo myPartner;
    @SerializedName("days")
    @Expose
    private String days;
    @SerializedName("app_module")
    @Expose
    private String appModule;
    @SerializedName("cancel_percentage")
    @Expose
    private String cancelPercentage;
    @SerializedName("cancel_count")
    @Expose
    private String cancelCount;
    @SerializedName("is_rewarded")
    @Expose
    private String isRewarded;
    @SerializedName("most_played")
    @Expose
    private List<OlePlayerInfo> mostPlayed;
    @SerializedName("football_matches")
    @Expose
    private List<OleMatchResults> footballMatches;
    @SerializedName("achievements")
    @Expose
    private List<OleProfileAchievement> achievements;
    @SerializedName("padel_matches")
    @Expose
    private List<OlePadelMatchResults> padelMatches;
    @SerializedName("skill_level_id")
    @Expose
    private String skillLevelId;
    @SerializedName("skill_level")
    @Expose
    private String skillLevel;
    @SerializedName("player_levels")
    @Expose
    private List<OlePlayerLevel> olePlayerLevels;
    @SerializedName("blocked_date")
    @Expose
    private String blockedDate;
    @SerializedName("card_discount_value")
    @Expose
    private String cardDiscountValue;
    @SerializedName("discount_booking_remaining")
    @Expose
    private String discountBookingRemaining;
    @SerializedName("discount_booking_target")
    @Expose
    private String discountBookingTarget;
    @SerializedName("opponent_goals")
    @Expose
    private String opponentGoals;
    @SerializedName("pending_balance")
    @Expose
    private String pendingBalance;
    @SerializedName("last_booking_date")
    @Expose
    private String lastBookingDate;
    @SerializedName("remaining_bookings")
    @Expose
    private String remainingBookings;
    @SerializedName("club_id") // for player stat list use
    @Expose
    private String clubId;
    @SerializedName("club_name") // for player stat list use
    @Expose
    private String clubName;
    @SerializedName("emoji_url")
    @Expose
    private String emojiUrl;
    @SerializedName("bib_id")
    @Expose
    private String bibId;
    @SerializedName("bib_url")
    @Expose
    private String bibUrl;
    public String getEmojiUrl() {return emojiUrl;}

    public void setEmojiUrl(String emojiUrl) {
        this.emojiUrl = emojiUrl;
    }

    public String getBibId() {
        return bibId;
    }

    public void setBibId(String bibId) {
        this.bibId = bibId;
    }

    public String getBibUrl() {
        return bibUrl;
    }

    public void setBibUrl(String bibUrl) {
        this.bibUrl = bibUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getMatchWon() {
        return matchWon;
    }

    public void setMatchWon(String matchWon) {
        this.matchWon = matchWon;
    }

    public String getMatchLoss() {
        return matchLoss;
    }

    public void setMatchLoss(String matchLoss) {
        this.matchLoss = matchLoss;
    }

    public String getMatchPlayed() {
        return matchPlayed;
    }

    public void setMatchPlayed(String matchPlayed) {
        this.matchPlayed = matchPlayed;
    }

    public String getMatchDrawn() {
        return matchDrawn;
    }

    public void setMatchDrawn(String matchDrawn) {
        this.matchDrawn = matchDrawn;
    }

    public String getViolations() {
        return violations;
    }

    public void setViolations(String violations) {
        this.violations = violations;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public OlePlayerLevel getLevel() {
        return level;
    }

    public void setLevel(OlePlayerLevel level) {
        this.level = level;
    }

    public String getWinPercentage() {
        return winPercentage;
    }

    public void setWinPercentage(String winPercentage) {
        this.winPercentage = winPercentage;
    }

    public String getGamesRanking() {
        return gamesRanking;
    }

    public void setGamesRanking(String gamesRanking) {
        this.gamesRanking = gamesRanking;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(String totalBookings) {
        this.totalBookings = totalBookings;
    }

    public String getWinRank() {
        return winRank;
    }

    public void setWinRank(String winRank) {
        this.winRank = winRank;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getFriendlyGames() {
        return friendlyGames;
    }

    public void setFriendlyGames(String friendlyGames) {
        this.friendlyGames = friendlyGames;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OlePlayerPosition getPlayerPosition() {
        return olePlayerPosition;
    }

    public void setPlayerPosition(OlePlayerPosition olePlayerPosition) {
        this.olePlayerPosition = olePlayerPosition;
    }

    public String getPlayingLevel() {
        return playingLevel;
    }

    public void setPlayingLevel(String playingLevel) {
        this.playingLevel = playingLevel;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getIsRated() {
        return isRated;
    }

    public void setIsRated(String isRated) {
        this.isRated = isRated;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getIsCaptain() {
        return isCaptain;
    }

    public void setIsCaptain(String isCaptain) {
        this.isCaptain = isCaptain;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(String xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public String getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(String yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public String getPlayerConfirmed() {
        return playerConfirmed;
    }

    public void setPlayerConfirmed(String playerConfirmed) {
        this.playerConfirmed = playerConfirmed;
    }

    public String getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(String totalHours) {
        this.totalHours = totalHours;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public OlePlayerInfo getMyPartner() {
        return myPartner;
    }

    public void setMyPartner(OlePlayerInfo myPartner) {
        this.myPartner = myPartner;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getAppModule() {
        return appModule;
    }

    public void setAppModule(String appModule) {
        this.appModule = appModule;
    }

    public String getCancelPercentage() {
        return cancelPercentage;
    }

    public void setCancelPercentage(String cancelPercentage) {
        this.cancelPercentage = cancelPercentage;
    }

    public String getCancelCount() {
        return cancelCount;
    }

    public void setCancelCount(String cancelCount) {
        this.cancelCount = cancelCount;
    }

    public String getIsRewarded() {
        return isRewarded;
    }

    public void setIsRewarded(String isRewarded) {
        this.isRewarded = isRewarded;
    }

    public List<OlePlayerInfo> getMostPlayed() {
        return mostPlayed;
    }

    public void setMostPlayed(List<OlePlayerInfo> mostPlayed) {
        this.mostPlayed = mostPlayed;
    }

    public List<OleMatchResults> getFootballMatches() {
        return footballMatches;
    }

    public void setFootballMatches(List<OleMatchResults> footballMatches) {
        this.footballMatches = footballMatches;
    }

    public List<OleProfileAchievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<OleProfileAchievement> achievements) {
        this.achievements = achievements;
    }

    public List<OlePadelMatchResults> getPadelMatches() {
        return padelMatches;
    }

    public void setPadelMatches(List<OlePadelMatchResults> padelMatches) {
        this.padelMatches = padelMatches;
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

    public List<OlePlayerLevel> getPlayerLevels() {
        return olePlayerLevels;
    }

    public void setPlayerLevels(List<OlePlayerLevel> olePlayerLevels) {
        this.olePlayerLevels = olePlayerLevels;
    }

    public String getBlockedDate() {
        return blockedDate;
    }

    public void setBlockedDate(String blockedDate) {
        this.blockedDate = blockedDate;
    }

    public String getCardDiscountValue() {
        return cardDiscountValue;
    }

    public void setCardDiscountValue(String cardDiscountValue) {
        this.cardDiscountValue = cardDiscountValue;
    }

    public String getDiscountBookingRemaining() {
        return discountBookingRemaining;
    }

    public void setDiscountBookingRemaining(String discountBookingRemaining) {
        this.discountBookingRemaining = discountBookingRemaining;
    }

    public String getDiscountBookingTarget() {
        return discountBookingTarget;
    }

    public void setDiscountBookingTarget(String discountBookingTarget) {
        this.discountBookingTarget = discountBookingTarget;
    }

    public String getOpponentGoals() {
        return opponentGoals;
    }

    public void setOpponentGoals(String opponentGoals) {
        this.opponentGoals = opponentGoals;
    }

    public String getPendingBalance() {
        return pendingBalance;
    }

    public void setPendingBalance(String pendingBalance) {
        this.pendingBalance = pendingBalance;
    }

    public String getLastBookingDate() {
        return lastBookingDate;
    }

    public void setLastBookingDate(String lastBookingDate) {
        this.lastBookingDate = lastBookingDate;
    }

    public String getRemainingBookings() {
        return remainingBookings;
    }

    public void setRemainingBookings(String remainingBookings) {
        this.remainingBookings = remainingBookings;
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

    public boolean isEmpty() {
        return id == null;
    }
}