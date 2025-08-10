package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LineupRealGameTeam {
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("team")
    @Expose
    private String team;
    @SerializedName("shirt")
    @Expose
    private String shirt;
    @SerializedName("chair")
    @Expose
    private String chair;
    @SerializedName("background")
    @Expose
    private String background;
    @SerializedName("goalkeeper")
    @Expose
    private String goalkeeper;
    @SerializedName("chair_url")
    @Expose
    private String chairUrl;
    @SerializedName("field_image_url")
    @Expose
    private String fieldImageUrl;
    @SerializedName("bg_image_url")
    @Expose
    private String bgImageUrl;
    @SerializedName("share_image_url")
    @Expose
    private String shareImageUrl;
    @SerializedName("team_shirt")
    @Expose
    private String teamShirt;
    @SerializedName("team_gk_shirt")
    @Expose
    private String teamGkShirt;
    @SerializedName("match_id")
    @Expose
    private String matchId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("match_status")
    @Expose
    private String matchStatus;
    @SerializedName("is_canceled")
    @Expose
    private Boolean isCanceled;
    @SerializedName("players")
    @Expose
    private List<LineupGlobalPlayers> players;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getShirt() {
        return shirt;
    }

    public void setShirt(String shirt) {
        this.shirt = shirt;
    }

    public String getChair() {
        return chair;
    }

    public void setChair(String chair) {
        this.chair = chair;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getGoalkeeper() {
        return goalkeeper;
    }

    public void setGoalkeeper(String goalkeeper) {
        this.goalkeeper = goalkeeper;
    }

    public String getChairUrl() {
        return chairUrl;
    }

    public void setChairUrl(String chairUrl) {
        this.chairUrl = chairUrl;
    }

    public String getFieldImageUrl() {
        return fieldImageUrl;
    }

    public void setFieldImageUrl(String fieldImageUrl) {
        this.fieldImageUrl = fieldImageUrl;
    }

    public String getBgImageUrl() {
        return bgImageUrl;
    }

    public void setBgImageUrl(String bgImageUrl) {
        this.bgImageUrl = bgImageUrl;
    }

    public String getShareImageUrl() {
        return shareImageUrl;
    }

    public void setShareImageUrl(String shareImageUrl) {
        this.shareImageUrl = shareImageUrl;
    }

    public String getTeamShirt() {
        return teamShirt;
    }

    public void setTeamShirt(String teamShirt) {
        this.teamShirt = teamShirt;
    }

    public String getTeamGkShirt() {
        return teamGkShirt;
    }

    public void setTeamGkShirt(String teamGkShirt) {
        this.teamGkShirt = teamGkShirt;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public Boolean getIsCanceled() {
        return isCanceled;
    }

    public void setIsCanceled(Boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    public List<LineupGlobalPlayers> getPlayers() {
        return players;
    }

    public void setPlayers(List<LineupGlobalPlayers> players) {
        this.players = players;
    }
}
