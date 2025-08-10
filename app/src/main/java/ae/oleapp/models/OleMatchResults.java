package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleMatchResults {

    @SerializedName("match_date")
    @Expose
    private String matchDate;
    @SerializedName("player_one")
    @Expose
    private OlePlayerInfo playerOne;
    @SerializedName("player_two")
    @Expose
    private OlePlayerInfo playerTwo;
    @SerializedName("last_played")
    @Expose
    private String lastPlayed;
    @SerializedName("total_played")
    @Expose
    private String totalPlayed;
    @SerializedName("player_one_win")
    @Expose
    private String playerOneWin;
    @SerializedName("player_two_win")
    @Expose
    private String playerTwoWin;
    @SerializedName("draw_matches")
    @Expose
    private String drawMatches;
    @SerializedName("match_time")
    @Expose
    private String matchTime;
    @SerializedName("club_name")
    @Expose
    private String clubName;
    @SerializedName("club_city")
    @Expose
    private String clubCity;
    @SerializedName("distance")
    @Expose
    private String distance;

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public OlePlayerInfo getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(OlePlayerInfo playerOne) {
        this.playerOne = playerOne;
    }

    public OlePlayerInfo getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(OlePlayerInfo playerTwo) {
        this.playerTwo = playerTwo;
    }

    public String getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(String lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public String getTotalPlayed() {
        return totalPlayed;
    }

    public void setTotalPlayed(String totalPlayed) {
        this.totalPlayed = totalPlayed;
    }

    public String getPlayerOneWin() {
        return playerOneWin;
    }

    public void setPlayerOneWin(String playerOneWin) {
        this.playerOneWin = playerOneWin;
    }

    public String getPlayerTwoWin() {
        return playerTwoWin;
    }

    public void setPlayerTwoWin(String playerTwoWin) {
        this.playerTwoWin = playerTwoWin;
    }

    public String getDrawMatches() {
        return drawMatches;
    }

    public void setDrawMatches(String drawMatches) {
        this.drawMatches = drawMatches;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubCity() {
        return clubCity;
    }

    public void setClubCity(String clubCity) {
        this.clubCity = clubCity;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
