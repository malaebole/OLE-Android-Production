package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OlePlayerReview {

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
    @SerializedName("photo_url")
    @Expose
    private String photoUrl;
    @SerializedName("games_ranking")
    @Expose
    private String gamesRanking;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("reach_time")
    @Expose
    private String reachTime;
    @SerializedName("feedback")
    @Expose
    private String feedback;
    @SerializedName("playing_level")
    @Expose
    private String playingLevel;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getGamesRanking() {
        return gamesRanking;
    }

    public void setGamesRanking(String gamesRanking) {
        this.gamesRanking = gamesRanking;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReachTime() {
        return reachTime;
    }

    public void setReachTime(String reachTime) {
        this.reachTime = reachTime;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getPlayingLevel() {
        return playingLevel;
    }

    public void setPlayingLevel(String playingLevel) {
        this.playingLevel = playingLevel;
    }

    public OlePlayerLevel getLevel() {
        return level;
    }

    public void setLevel(OlePlayerLevel level) {
        this.level = level;
    }
}
