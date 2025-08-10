package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInfo {

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
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("company_ar")
    @Expose
    private String companyAr;
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("nationality")
    @Expose
    private String nationality;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("user_role")
    @Expose
    private String userRole;
    @SerializedName("photo_name")
    @Expose
    private String photoName;
    @SerializedName("photo_url")
    @Expose
    private String photoUrl;
    @SerializedName("added_date")
    @Expose
    private String addedDate;
    @SerializedName("phone_verified")
    @Expose
    private String phoneVerified;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("win_percentage")
    @Expose
    private String winPercentage;
    @SerializedName("player_position")
    @Expose
    private String playerPosition;
    @SerializedName("games_ranking")
    @Expose
    private String gamesRanking;
    @SerializedName("favorite")
    @Expose
    private String favorite;
    @SerializedName("user_age")
    @Expose
    private String userAge;
    @SerializedName("match_played")
    @Expose
    private String matchPlayed;
    @SerializedName("lifetime_bookings")
    @Expose
    private String lifetimeBookings;
    @SerializedName("this_month_bookings")
    @Expose
    private String thisMonthBookings;
    @SerializedName("canceled_times")
    @Expose
    private String canceledTimes;
    @SerializedName("card_discount_value")
    @Expose
    private String cardDiscountValue;
    @SerializedName("discount_booking_remaining")
    @Expose
    private String discountBookingRemaining;
    @SerializedName("discount_booking_target")
    @Expose
    private String discountBookingTarget;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("confirm_percentage")
    @Expose
    private String confirmPercentage;
    @SerializedName("level")
    @Expose
    private OlePlayerLevel level;
    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;
    @SerializedName("skill_level")
    @Expose
    private String skillLevel;
    @SerializedName("skill_level_id")
    @Expose
    private String skillLevelId;
    @SerializedName("is_employee")
    @Expose
    private String isEmployee;
    @SerializedName("emoji_url")
    @Expose
    private String emojiUrl;
    @SerializedName("bib_url")
    @Expose
    private String bibUrl;
    @SerializedName("bib_id")
    @Expose
    private String bibId;
    @SerializedName("country_name")
    @Expose
    private String countryName;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("country_iso")
    @Expose
    private String countryIso;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("global_lineup")
    @Expose
    private String globalLineup;

    @SerializedName("friendship_id")
    @Expose
    private String friendShipId;
    @SerializedName("friend_request_count")
    @Expose
    private String friendRequestCount;

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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
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

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(String phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWinPercentage() {
        return winPercentage;
    }

    public void setWinPercentage(String winPercentage) {
        this.winPercentage = winPercentage;
    }

    public String getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(String playerPosition) {
        this.playerPosition = playerPosition;
    }

    public String getGamesRanking() {
        return gamesRanking;
    }

    public void setGamesRanking(String gamesRanking) {
        this.gamesRanking = gamesRanking;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getMatchPlayed() {
        return matchPlayed;
    }

    public void setMatchPlayed(String matchPlayed) {
        this.matchPlayed = matchPlayed;
    }

    public String getCompanyAr() {
        return companyAr;
    }

    public void setCompanyAr(String companyAr) {
        this.companyAr = companyAr;
    }

    public String getLifetimeBookings() {
        return lifetimeBookings;
    }

    public void setLifetimeBookings(String lifetimeBookings) {
        this.lifetimeBookings = lifetimeBookings;
    }

    public String getThisMonthBookings() {
        return thisMonthBookings;
    }

    public void setThisMonthBookings(String thisMonthBookings) {
        this.thisMonthBookings = thisMonthBookings;
    }

    public String getCanceledTimes() {
        return canceledTimes;
    }

    public void setCanceledTimes(String canceledTimes) {
        this.canceledTimes = canceledTimes;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getConfirmPercentage() {
        return confirmPercentage;
    }

    public void setConfirmPercentage(String confirmPercentage) {
        this.confirmPercentage = confirmPercentage;
    }

    public OlePlayerLevel getLevel() {
        return level;
    }

    public void setLevel(OlePlayerLevel level) {
        this.level = level;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDiscountBookingTarget() {
        return discountBookingTarget;
    }

    public void setDiscountBookingTarget(String discountBookingTarget) {
        this.discountBookingTarget = discountBookingTarget;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public String getSkillLevelId() {
        return skillLevelId;
    }

    public void setSkillLevelId(String skillLevelId) {
        this.skillLevelId = skillLevelId;
    }

    public String getIsEmployee() {
        return isEmployee;
    }

    public void setIsEmployee(String isEmployee) {
        this.isEmployee = isEmployee;
    }

    public String getEmojiUrl() {
        return emojiUrl;
    }

    public void setEmojiUrl(String emojiUrl) {
        this.emojiUrl = emojiUrl;
    }

    public String getBibUrl() {
        return bibUrl;
    }

    public void setBibUrl(String bibUrl) {
        this.bibUrl = bibUrl;
    }

    public String getBibId() {
        return bibId;
    }

    public void setBibId(String bibId) {
        this.bibId = bibId;
    }

    public boolean isEmpty() {
        return id == null;
    }

    public String getCountryName() {
            return countryName;
        }
        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getCountryIso() {
            return countryIso;
        }

        public void setCountryIso(String countryIso) {
            this.countryIso = countryIso;
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

        public String getGlobalLineup() {
            return globalLineup;
        }

        public void setGlobalLineup(String globalLineup) {
            this.globalLineup = globalLineup;
        }
        public String getFriendShipId() {
            return friendShipId;
        }

        public void setFriendShipId(String friendShipId) {
            this.friendShipId = friendShipId;
        }

        public String getFriendRequestCount() {
            return friendRequestCount;
        }

        public void setFriendRequestCount(String friendRequestCount) {
            this.friendRequestCount = friendRequestCount;
        }

}


