package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleMatchHistory {

    @SerializedName("user_data")
    @Expose
    private OlePlayerInfo userData;
    @SerializedName("player_two")
    @Expose
    private OlePlayerInfo playerTwo;
    @SerializedName("last_played")
    @Expose
    private String lastPlayed;
    @SerializedName("match_draw")
    @Expose
    private String matchDraw;

    public OlePlayerInfo getUserData() {
        return userData;
    }

    public void setUserData(OlePlayerInfo userData) {
        this.userData = userData;
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

    public String getMatchDraw() {
        return matchDraw;
    }

    public void setMatchDraw(String matchDraw) {
        this.matchDraw = matchDraw;
    }

}