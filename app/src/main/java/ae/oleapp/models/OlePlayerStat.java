package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OlePlayerStat {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("nick_name")
    @Expose
    private String nickName;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("nationality")
    @Expose
    private String nationality;
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
    @SerializedName("favorite")
    @Expose
    private String favorite;
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
    @SerializedName("win_rank")
    @Expose
    private String winRank;
    @SerializedName("friendly_games")
    @Expose
    private String friendlyGames;
    @SerializedName("total_bookings")
    @Expose
    private String totalBookings;
    @SerializedName("total_canceled")
    @Expose
    private String totalCanceled;
    @SerializedName("canceled_twelve")
    @Expose
    private String canceledTwelve;
    @SerializedName("total_hours")
    @Expose
    private String totalHours;
    @SerializedName("call_bookings")
    @Expose
    private String callBookings;
    @SerializedName("app_bookings")
    @Expose
    private String appBookings;
    @SerializedName("future_bookings")
    @Expose
    private String futureBookings;
    @SerializedName("total_cash_paid")
    @Expose
    private String totalCashPaid;
    @SerializedName("total_online_paid")
    @Expose
    private String totalOnlinePaid;
    @SerializedName("total_pos_paid")
    @Expose
    private String totalPosPaid;
    @SerializedName("card_discount_target")
    @Expose
    private String cardDiscountTarget;
    @SerializedName("card_discount_remaining")
    @Expose
    private String cardDiscountRemaining;
    @SerializedName("card_discount_value")
    @Expose
    private String cardDiscountValue;
    @SerializedName("card_discount_expiry")
    @Expose
    private String cardDiscountExpiry;
    @SerializedName("restricted_days")
    @Expose
    private String restrictedDays;
    @SerializedName("restricted_payment")
    @Expose
    private String restrictedPayment;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("cancellation_hours")
    @Expose
    private String cancellationHours;
    @SerializedName("is_blocked")
    @Expose
    private String isBlocked;
    @SerializedName("blocked_date")
    @Expose
    private String blockedDate;
    @SerializedName("blocked_reason")
    @Expose
    private String blockedReason;
    @SerializedName("pending_balance")
    @Expose
    private List<OlePlayerBalance> pendingBalance;
    @SerializedName("last_booking_date")
    @Expose
    private String lastBookingDate;
    @SerializedName("bookings_restriction")
    @Expose
    private String bookingsRestriction;
    @SerializedName("continuous_allowed")
    @Expose
    private String continuousAllowed;
    @SerializedName("blocked_by")
    @Expose
    private String blockedBy;

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

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
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

    public String getWinRank() {
        return winRank;
    }

    public void setWinRank(String winRank) {
        this.winRank = winRank;
    }

    public String getFriendlyGames() {
        return friendlyGames;
    }

    public void setFriendlyGames(String friendlyGames) {
        this.friendlyGames = friendlyGames;
    }

    public String getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(String totalBookings) {
        this.totalBookings = totalBookings;
    }

    public String getTotalCanceled() {
        return totalCanceled;
    }

    public void setTotalCanceled(String totalCanceled) {
        this.totalCanceled = totalCanceled;
    }

    public String getCanceledTwelve() {
        return canceledTwelve;
    }

    public void setCanceledTwelve(String canceledTwelve) {
        this.canceledTwelve = canceledTwelve;
    }

    public String getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(String totalHours) {
        this.totalHours = totalHours;
    }

    public String getCallBookings() {
        return callBookings;
    }

    public void setCallBookings(String callBookings) {
        this.callBookings = callBookings;
    }

    public String getAppBookings() {
        return appBookings;
    }

    public void setAppBookings(String appBookings) {
        this.appBookings = appBookings;
    }

    public String getFutureBookings() {
        return futureBookings;
    }

    public void setFutureBookings(String futureBookings) {
        this.futureBookings = futureBookings;
    }

    public String getTotalCashPaid() {
        return totalCashPaid;
    }

    public void setTotalCashPaid(String totalCashPaid) {
        this.totalCashPaid = totalCashPaid;
    }

    public String getTotalOnlinePaid() {
        return totalOnlinePaid;
    }

    public void setTotalOnlinePaid(String totalOnlinePaid) {
        this.totalOnlinePaid = totalOnlinePaid;
    }

    public String getTotalPosPaid() {
        return totalPosPaid;
    }

    public void setTotalPosPaid(String totalPosPaid) {
        this.totalPosPaid = totalPosPaid;
    }

    public String getCardDiscountTarget() {
        return cardDiscountTarget;
    }

    public void setCardDiscountTarget(String cardDiscountTarget) {
        this.cardDiscountTarget = cardDiscountTarget;
    }

    public String getCardDiscountRemaining() {
        return cardDiscountRemaining;
    }

    public void setCardDiscountRemaining(String cardDiscountRemaining) {
        this.cardDiscountRemaining = cardDiscountRemaining;
    }

    public String getCardDiscountValue() {
        return cardDiscountValue;
    }

    public void setCardDiscountValue(String cardDiscountValue) {
        this.cardDiscountValue = cardDiscountValue;
    }

    public String getCardDiscountExpiry() {
        return cardDiscountExpiry;
    }

    public void setCardDiscountExpiry(String cardDiscountExpiry) {
        this.cardDiscountExpiry = cardDiscountExpiry;
    }

    public String getRestrictedDays() {
        return restrictedDays;
    }

    public void setRestrictedDays(String restrictedDays) {
        this.restrictedDays = restrictedDays;
    }

    public String getRestrictedPayment() {
        return restrictedPayment;
    }

    public void setRestrictedPayment(String restrictedPayment) {
        this.restrictedPayment = restrictedPayment;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCancellationHours() {
        return cancellationHours;
    }

    public void setCancellationHours(String cancellationHours) {
        this.cancellationHours = cancellationHours;
    }

    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
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

    public List<OlePlayerBalance> getPendingBalance() {
        return pendingBalance;
    }

    public void setPendingBalance(List<OlePlayerBalance> pendingBalance) {
        this.pendingBalance = pendingBalance;
    }

    public String getLastBookingDate() {
        return lastBookingDate;
    }

    public void setLastBookingDate(String lastBookingDate) {
        this.lastBookingDate = lastBookingDate;
    }

    public String getBookingsRestriction() {
        return bookingsRestriction;
    }

    public void setBookingsRestriction(String bookingsRestriction) {
        this.bookingsRestriction = bookingsRestriction;
    }

    public String getContinuousAllowed() {
        return continuousAllowed;
    }

    public void setContinuousAllowed(String continuousAllowed) {
        this.continuousAllowed = continuousAllowed;
    }

    public String getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(String blockedBy) {
        this.blockedBy = blockedBy;
    }
}