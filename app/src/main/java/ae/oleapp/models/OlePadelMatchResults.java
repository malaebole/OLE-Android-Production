package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OlePadelMatchResults {

    @SerializedName("match_date")
    @Expose
    private String matchDate;
    @SerializedName("match_time")
    @Expose
    private String matchTime;
    @SerializedName("created_by")
    @Expose
    private OlePlayerInfo createdBy;
    @SerializedName("creator_partner")
    @Expose
    private OlePlayerInfo creatorPartner;
    @SerializedName("player_two")
    @Expose
    private OlePlayerInfo playerTwo;
    @SerializedName("player_two_partner")
    @Expose
    private OlePlayerInfo playerTwoPartner;
    @SerializedName("creator_score")
    @Expose
    private OlePadelScore creatorScore;
    @SerializedName("player_two_score")
    @Expose
    private OlePadelScore playerTwoScore;
    @SerializedName("creator_win")
    @Expose
    private String creatorWin;
    @SerializedName("player_two_win")
    @Expose
    private String playerTwoWin;
    @SerializedName("club_name")
    @Expose
    private String clubName;

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public OlePlayerInfo getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(OlePlayerInfo createdBy) {
        this.createdBy = createdBy;
    }

    public OlePlayerInfo getCreatorPartner() {
        return creatorPartner;
    }

    public void setCreatorPartner(OlePlayerInfo creatorPartner) {
        this.creatorPartner = creatorPartner;
    }

    public OlePlayerInfo getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(OlePlayerInfo playerTwo) {
        this.playerTwo = playerTwo;
    }

    public OlePlayerInfo getPlayerTwoPartner() {
        return playerTwoPartner;
    }

    public void setPlayerTwoPartner(OlePlayerInfo playerTwoPartner) {
        this.playerTwoPartner = playerTwoPartner;
    }

    public OlePadelScore getCreatorScore() {
        return creatorScore;
    }

    public void setCreatorScore(OlePadelScore creatorScore) {
        this.creatorScore = creatorScore;
    }

    public OlePadelScore getPlayerTwoScore() {
        return playerTwoScore;
    }

    public void setPlayerTwoScore(OlePadelScore playerTwoScore) {
        this.playerTwoScore = playerTwoScore;
    }

    public String getCreatorWin() {
        return creatorWin;
    }

    public void setCreatorWin(String creatorWin) {
        this.creatorWin = creatorWin;
    }

    public String getPlayerTwoWin() {
        return playerTwoWin;
    }

    public void setPlayerTwoWin(String playerTwoWin) {
        this.playerTwoWin = playerTwoWin;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }
}
