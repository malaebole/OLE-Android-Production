package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleGameHistory {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("photo_url")
    @Expose
    private String photoUrl;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("win_percentage")
    @Expose
    private String winPercentage;
    @SerializedName("games_ranking")
    @Expose
    private String gamesRanking;
    @SerializedName("rating_data")
    @Expose
    private OleRatingData oleRatingData;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("level")
    @Expose
    private OlePlayerLevel level;

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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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

    public OleRatingData getRatingData() {
        return oleRatingData;
    }

    public void setRatingData(OleRatingData oleRatingData) {
        this.oleRatingData = oleRatingData;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public OlePlayerLevel getLevel() {
        return level;
    }

    public void setLevel(OlePlayerLevel level) {
        this.level = level;
    }
}
