package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SocketGameTeam {

    @SerializedName("field_image")
    @Expose
    private String fieldImage;
    @SerializedName("bg_image")
    @Expose
    private String bgImage;
    @SerializedName("share_image")
    @Expose
    private String shareImage;
    @SerializedName("game_id")
    @Expose
    private String gameId;
    @SerializedName("game_date")
    @Expose
    private String gameDate;
    @SerializedName("game_time")
    @Expose
    private String gameTime;
    @SerializedName("game_players_capacity")
    @Expose
    private String gamePlayersCount;
    @SerializedName("added_players_count")
    @Expose
    private String gamePlayers;
    @SerializedName("is_game_on")
    @Expose
    private String isGameOn;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("club_name")
    @Expose
    private String clubName;
    @SerializedName("city_name")
    @Expose
    private String cityName;

    public String getFieldImage() {
        return fieldImage;
    }

    public void setFieldImage(String fieldImage) {
        this.fieldImage = fieldImage;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public String getShareImage() {
        return shareImage;
    }

    public void setShareImage(String shareImage) {
        this.shareImage = shareImage;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameDate() {
        return gameDate;
    }

    public void setGameDate(String gameDate) {
        this.gameDate = gameDate;
    }

    public String getGameTime() {
        return gameTime;
    }

    public void setGameTime(String gameTime) {
        this.gameTime = gameTime;
    }


    public String getIsGameOn() {
        return isGameOn;
    }

    public void setIsGameOn(String isGameOn) {
        this.isGameOn = isGameOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getGamePlayersCount() {
        return gamePlayersCount;
    }

    public void setGamePlayersCount(String gamePlayersCount) {
        this.gamePlayersCount = gamePlayersCount;
    }

    public String getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(String gamePlayers) {this.gamePlayers = gamePlayers;}
}
