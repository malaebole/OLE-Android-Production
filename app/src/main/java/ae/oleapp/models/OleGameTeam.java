package ae.oleapp.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleGameTeam {

    @SerializedName("team_a_id")
    @Expose
    private String teamAId;
    @SerializedName("team_a_name")
    @Expose
    private String teamAName;
    @SerializedName("team_a_color")
    @Expose
    private String teamAColor;
    @SerializedName("team_a_players")
    @Expose
    private List<OlePlayerInfo> teamAPlayers = null;
    @SerializedName("team_b_id")
    @Expose
    private String teamBId;
    @SerializedName("team_b_name")
    @Expose
    private String teamBName;
    @SerializedName("team_b_color")
    @Expose
    private String teamBColor;
    @SerializedName("team_b_players")
    @Expose
    private List<OlePlayerInfo> teamBPlayers = null;
    @SerializedName("team_a_image")
    @Expose
    private String teamAImage;
    @SerializedName("team_b_image")
    @Expose
    private String teamBImage;

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

    public String getTeamAColor() {
        return teamAColor;
    }

    public void setTeamAColor(String teamAColor) {
        this.teamAColor = teamAColor;
    }

    public List<OlePlayerInfo> getTeamAPlayers() {
        return teamAPlayers;
    }

    public void setTeamAPlayers(List<OlePlayerInfo> teamAPlayers) {
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

    public String getTeamBColor() {
        return teamBColor;
    }

    public void setTeamBColor(String teamBColor) {
        this.teamBColor = teamBColor;
    }

    public List<OlePlayerInfo> getTeamBPlayers() {
        return teamBPlayers;
    }

    public void setTeamBPlayers(List<OlePlayerInfo> teamBPlayers) {
        this.teamBPlayers = teamBPlayers;
    }

    public String getTeamAImage() {
        return teamAImage;
    }

    public void setTeamAImage(String teamAImage) {
        this.teamAImage = teamAImage;
    }

    public String getTeamBImage() {
        return teamBImage;
    }

    public void setTeamBImage(String teamBImage) {
        this.teamBImage = teamBImage;
    }

    public boolean isEmpty() {
        return teamAId == null || teamBId == null;
    }

}
