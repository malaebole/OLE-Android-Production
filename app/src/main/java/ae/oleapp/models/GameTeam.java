package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GameTeam {

    @SerializedName("game_id")
    @Expose
    private String gameId;
    @SerializedName("team_a_id")
    @Expose
    private String teamAId;
    @SerializedName("team_a_name")
    @Expose
    private String teamAName;
    @SerializedName("team_a_shirt")
    @Expose
    private String teamAShirt;
    @SerializedName("team_a_gk_shirt")
    @Expose
    private String teamAgkShirt;
    @SerializedName("team_a_image")
    @Expose
    private String teamAImage;
    @SerializedName("team_a_players")
    @Expose
    private List<PlayerInfo> teamAPlayers = new ArrayList<>();
    @SerializedName("team_b_id")
    @Expose
    private String teamBId;
    @SerializedName("team_b_name")
    @Expose
    private String teamBName;
    @SerializedName("team_b_shirt")
    @Expose
    private String teamBShirt;
    @SerializedName("team_b_gk_shirt")
    @Expose
    private String teamBgkShirt;
    @SerializedName("team_b_image")
    @Expose
    private String teamBImage;
    @SerializedName("team_b_players")
    @Expose
    private List<PlayerInfo> teamBPlayers = null;
    @SerializedName("game_date")
    @Expose
    private String gameDate;
    @SerializedName("game_time")
    @Expose
    private String gameTime;
    @SerializedName("game_players_capacity")
    @Expose
    private String gamePlayers;
    @SerializedName("is_game_on")
    @Expose
    private String isGameOn;
    @SerializedName("added_players_count")
    @Expose
    private String gamePlayersCount;
    @SerializedName("group_name")
    @Expose
    private String groupName;

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("room_id")
    @Expose
    private String roomId;
    @SerializedName("zego_token")
    @Expose
    private String zegoToken;
    @SerializedName("created_by")
    @Expose
    private String createdBy;

    @SerializedName("club_name")
    @Expose
    private String clubName;

    @SerializedName("city_name")
    @Expose
    private String cityName;

    @SerializedName("field_image")
    @Expose
    private String fieldImage;

    @SerializedName("bg_image")
    @Expose
    private String bgImage;

    @SerializedName("share_image")
    @Expose
    private String shareImage;

    @SerializedName("game_type")
    @Expose
    private String gameType;

    @SerializedName("captains")
    @Expose
    private List<String> captains;

    @SerializedName("invite_url")
    @Expose
    private String inviteUrl;


    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getTeamAId() {
        return teamAId;
    }

    public void setTeamAId(String teamAId) {
        this.teamAId = teamAId;
    }

    public String getTeamAName() {
        return teamAName;
    }

    public void setTeamAName(String teamAName) {
        this.teamAName = teamAName;
    }

    public String getTeamAShirt() {
        return teamAShirt;
    }

    public void setTeamAShirt(String teamAShirt) {
        this.teamAShirt = teamAShirt;
    }

    public String getTeamAgkShirt() {
        return teamAgkShirt;
    }

    public void setTeamAgkShirt(String teamAgkShirt) {
        this.teamAgkShirt = teamAgkShirt;
    }

    public String getTeamAImage() {
        return teamAImage;
    }

    public void setTeamAImage(String teamAImage) {
        this.teamAImage = teamAImage;
    }

    public List<PlayerInfo> getTeamAPlayers() {
        return teamAPlayers;
    }

    public void setTeamAPlayers(List<PlayerInfo> teamAPlayers) {
        this.teamAPlayers = teamAPlayers;
    }

    public String getTeamBId() {
        return teamBId;
    }

    public void setTeamBId(String teamBId) {
        this.teamBId = teamBId;
    }

    public String getTeamBName() {
        return teamBName;
    }

    public void setTeamBName(String teamBName) {
        this.teamBName = teamBName;
    }

    public String getTeamBShirt() {
        return teamBShirt;
    }

    public void setTeamBShirt(String teamBShirt) {
        this.teamBShirt = teamBShirt;
    }

    public String getTeamBgkShirt() {
        return teamBgkShirt;
    }

    public void setTeamBgkShirt(String teamBgkShirt) {
        this.teamBgkShirt = teamBgkShirt;
    }

    public String getTeamBImage() {
        return teamBImage;
    }

    public void setTeamBImage(String teamBImage) {
        this.teamBImage = teamBImage;
    }

    public List<PlayerInfo> getTeamBPlayers() {
        return teamBPlayers;
    }

    public void setTeamBPlayers(List<PlayerInfo> teamBPlayers) {
        this.teamBPlayers = teamBPlayers;
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

    public String getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(String gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public String getIsGameOn() {
        return isGameOn;
    }

    public void setIsGameOn(String isGameOn) {
        this.isGameOn = isGameOn;
    }

    public String getGamePlayersCount() {
        return gamePlayersCount;
    }

    public void setGamePlayersCount(String gamePlayersCount) {
        this.gamePlayersCount = gamePlayersCount;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getZegoToken() {

        return zegoToken;

    }

    public void setZegoToken(String zegoToken) {
        this.zegoToken = zegoToken;
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

    public String getGameType() {
        return gameType;
    }
    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public void setCaptains(List<String> captains) {
        this.captains = captains;
    }

    public List<String> getCaptains() {
        return captains;
    }

    public String getInviteUrl() {
        return inviteUrl;
    }
    public void setInviteUrl(String inviteUrl) {
        this.inviteUrl = inviteUrl;
    }


}
