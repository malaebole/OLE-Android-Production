package ae.oleapp.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Club {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("owner_phone")
    @Expose
    private String ownerPhone;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("name_ar")
    @Expose
    private String nameAr;
    @SerializedName("country")
    @Expose
    private OleCountry oleCountry;
    @SerializedName("city")
    @Expose
    private OleCountry city;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("slots_60")
    @Expose
    private String slots60;
    @SerializedName("slots_90")
    @Expose
    private String slots90;
    @SerializedName("slots_120")
    @Expose
    private String slots120;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("club_logo")
    @Expose
    private String clubLogo;
    @SerializedName("logo_path")
    @Expose
    private String logoPath;
    @SerializedName("club_cover")
    @Expose
    private String clubCover;
    @SerializedName("cover_path")
    @Expose
    private String coverPath;
    @SerializedName("is_featured")
    @Expose
    private String isFeatured;
    @SerializedName("price_per_day")
    @Expose
    private String pricePerDay;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("is_offer")
    @Expose
    private String isOffer;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("start_price")
    @Expose
    private String startPrice;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("booking_count")
    @Expose
    private Integer bookingCount;
    @SerializedName("facilities")
    @Expose
    private List<OleClubFacility> facilities = null;
    @SerializedName("timings")
    @Expose
    private List<Day> timings = null;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("favorite")
    @Expose
    private String favorite;
    @SerializedName("match_allowed")
    @Expose
    private String matchAllowed;
    @SerializedName("favorite_count")
    @Expose
    private String favoriteCount;
    @SerializedName("fields_count")
    @Expose
    private Integer fieldsCount;
    @SerializedName("today_earning")
    @Expose
    private String todayEarning;
    @SerializedName("total_confirmed")
    @Expose
    private String totalConfirmed;
    @SerializedName("total_completed")
    @Expose
    private String totalCompleted;
    @SerializedName("waiting_user_count")
    @Expose
    private String waitingUserCount;
    @SerializedName("total_hours")
    @Expose
    private String totalHours;
    @SerializedName("gap_allowed")
    @Expose
    private String gapAllowed;
    @SerializedName("games_allowed")
    @Expose
    private String gamesAllowed;
    @SerializedName("has_padel_fields")
    @Expose
    private String hasPadelFields;
    @SerializedName("club_payment_method")
    @Expose
    private String clubPaymentMethod;
    @SerializedName("available_fields")
    @Expose
    private String availableFields;
    @SerializedName("club_expiry_date")
    @Expose
    private String clubExpiryDate;
    @SerializedName("is_expired")
    @Expose
    private String isExpired;
    @SerializedName("offer")
    @Expose
    private String offer;
    @SerializedName("new_players_count")
    @Expose
    private String newPlayersCount;
    @SerializedName("club_type")
    @Expose
    private String clubType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public OleCountry getCountry() {
        return oleCountry;
    }

    public void setCountry(OleCountry oleCountry) {
        this.oleCountry = oleCountry;
    }

    public OleCountry getCity() {
        return city;
    }

    public void setCity(OleCountry city) {
        this.city = city;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getClubLogo() {
        return clubLogo;
    }

    public void setClubLogo(String clubLogo) {
        this.clubLogo = clubLogo;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getClubCover() {
        return clubCover;
    }

    public void setClubCover(String clubCover) {
        this.clubCover = clubCover;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(String isFeatured) {
        this.isFeatured = isFeatured;
    }

    public String getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(String pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIsOffer() {
        return isOffer;
    }

    public void setIsOffer(String isOffer) {
        this.isOffer = isOffer;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(String startPrice) {
        this.startPrice = startPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(Integer bookingCount) {
        this.bookingCount = bookingCount;
    }

    public List<OleClubFacility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<OleClubFacility> facilities) {
        this.facilities = facilities;
    }

    public List<Day> getTimings() {
        return timings;
    }

    public void setTimings(List<Day> timings) {
        this.timings = timings;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getMatchAllowed() {
        return matchAllowed;
    }

    public void setMatchAllowed(String matchAllowed) {
        this.matchAllowed = matchAllowed;
    }

    public String getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(String favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Integer getFieldsCount() {
        return fieldsCount;
    }

    public void setFieldsCount(Integer fieldsCount) {
        this.fieldsCount = fieldsCount;
    }

    public String getTodayEarning() {
        return todayEarning;
    }

    public void setTodayEarning(String todayEarning) {
        this.todayEarning = todayEarning;
    }

    public String getTotalConfirmed() {
        return totalConfirmed;
    }

    public void setTotalConfirmed(String totalConfirmed) {
        this.totalConfirmed = totalConfirmed;
    }

    public String getTotalCompleted() {
        return totalCompleted;
    }

    public void setTotalCompleted(String totalCompleted) {
        this.totalCompleted = totalCompleted;
    }

    public String getWaitingUserCount() {
        return waitingUserCount;
    }

    public void setWaitingUserCount(String waitingUserCount) {
        this.waitingUserCount = waitingUserCount;
    }

    public String getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(String totalHours) {
        this.totalHours = totalHours;
    }

    public String getGapAllowed() {
        return gapAllowed;
    }

    public void setGapAllowed(String gapAllowed) {
        this.gapAllowed = gapAllowed;
    }

    public String getGamesAllowed() {
        return gamesAllowed;
    }

    public void setGamesAllowed(String gamesAllowed) {
        this.gamesAllowed = gamesAllowed;
    }

    public String getHasPadelFields() {
        return hasPadelFields;
    }

    public void setHasPadelFields(String hasPadelFields) {
        this.hasPadelFields = hasPadelFields;
    }

    public String getClubPaymentMethod() {
        return clubPaymentMethod;
    }

    public void setClubPaymentMethod(String clubPaymentMethod) {
        this.clubPaymentMethod = clubPaymentMethod;
    }

    public String getAvailableFields() {
        return availableFields;
    }

    public void setAvailableFields(String availableFields) {
        this.availableFields = availableFields;
    }

    public String getClubExpiryDate() {
        return clubExpiryDate;
    }

    public void setClubExpiryDate(String clubExpiryDate) {
        this.clubExpiryDate = clubExpiryDate;
    }

    public String getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(String isExpired) {
        this.isExpired = isExpired;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getNewPlayersCount() {
        return newPlayersCount;
    }

    public void setNewPlayersCount(String newPlayersCount) {
        this.newPlayersCount = newPlayersCount;
    }

    public String getClubType() {
        return clubType;
    }

    public void setClubType(String clubType) {
        this.clubType = clubType;
    }
}
