package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleRatingData {

    @SerializedName("total_games")
    @Expose
    private String totalGames;
    @SerializedName("playing_level")
    @Expose
    private String playingLevel;
    @SerializedName("befor_time")
    @Expose
    private String beforTime;
    @SerializedName("on_time")
    @Expose
    private String onTime;
    @SerializedName("late")
    @Expose
    private String late;
    @SerializedName("not_come")
    @Expose
    private String notCome;

    public String getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(String totalGames) {
        this.totalGames = totalGames;
    }

    public String getPlayingLevel() {
        return playingLevel;
    }

    public void setPlayingLevel(String playingLevel) {
        this.playingLevel = playingLevel;
    }

    public String getBeforTime() {
        return beforTime;
    }

    public void setBeforTime(String beforTime) {
        this.beforTime = beforTime;
    }

    public String getOnTime() {
        return onTime;
    }

    public void setOnTime(String onTime) {
        this.onTime = onTime;
    }

    public String getLate() {
        return late;
    }

    public void setLate(String late) {
        this.late = late;
    }

    public String getNotCome() {
        return notCome;
    }

    public void setNotCome(String notCome) {
        this.notCome = notCome;
    }

}
