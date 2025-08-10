package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OlePlayerRank {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("photo_url")
    @Expose
    private String photoUrl;
    @SerializedName("birth_date")
    @Expose
    private String birthDate;
    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("goals")
    @Expose
    private String goals;
    @SerializedName("points")
    @Expose
    private String points;
    @SerializedName("level")
    @Expose
    private OlePlayerLevel level;
    @SerializedName("total_bookings")
    @Expose
    private String totalBookings;
    @SerializedName("win_percentage")
    @Expose
    private String winPercentage;
    @SerializedName("games_ranking")
    @Expose
    private String gamesRanking;
    @SerializedName("total_hours")
    @Expose
    private String totalHours;
    @SerializedName("nick_name")
    @Expose
    private String nickName;

    int rank;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
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

    public OlePlayerLevel getLevel() {
        return level;
    }

    public void setLevel(OlePlayerLevel level) {
        this.level = level;
    }

    public String getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(String totalBookings) {
        this.totalBookings = totalBookings;
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

    public String getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(String totalHours) {
        this.totalHours = totalHours;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}