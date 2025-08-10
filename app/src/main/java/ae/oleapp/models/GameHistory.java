package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GameHistory {

    @SerializedName("group_name")
    @Expose
    private String groupName;
    @SerializedName("game_id")
    @Expose
    private String gameId;
    @SerializedName("game_date")
    @Expose
    private String gameDate;
    @SerializedName("game_time")
    @Expose
    private String gameTime;
    @SerializedName("game_players")
    @Expose
    private String gamePlayers;
    @SerializedName("is_game_on")
    @Expose
    private String isGameOn;
    @SerializedName("team_a")
    @Expose
    private GameTeam teamA;
    @SerializedName("team_b")
    @Expose
    private GameTeam teamB;
    @SerializedName("team_a_players")
    @Expose
    private PlayerInfo teamAPlayer;
    @SerializedName("team_b_players")
    @Expose
    private PlayerInfo teamBPlayer;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public GameTeam getTeamA() {
        return teamA;
    }

    public void setTeamA(GameTeam teamA) {
        this.teamA = teamA;
    }

    public GameTeam getTeamB() {
        return teamB;
    }

    public void setTeamB(GameTeam teamB) {
        this.teamB = teamB;
    }

    public PlayerInfo getTeamAPlayer() {
        return teamAPlayer;
    }

    public void setTeamAPlayers(PlayerInfo teamAPlayer) {
        this.teamAPlayer = teamAPlayer;
    }

    public PlayerInfo getTeamBPlayer() {
        return teamBPlayer;
    }

    public void setTeamBPlayers(PlayerInfo teamBPlayers) {
        this.teamBPlayer = teamBPlayer;
    }

}