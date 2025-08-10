package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleMatchScore {

    @SerializedName("football_team_a")
    @Expose
    private String footballTeamA;
    @SerializedName("football_team_b")
    @Expose
    private String footballTeamB;
    @SerializedName("padel_team_a")
    @Expose
    private OlePadelScore padelTeamA;
    @SerializedName("padel_team_b")
    @Expose
    private OlePadelScore padelTeamB;

    public String getFootballTeamA() {
        return footballTeamA;
    }

    public void setFootballTeamA(String footballTeamA) {
        this.footballTeamA = footballTeamA;
    }

    public String getFootballTeamB() {
        return footballTeamB;
    }

    public void setFootballTeamB(String footballTeamB) {
        this.footballTeamB = footballTeamB;
    }

    public OlePadelScore getPadelTeamA() {
        return padelTeamA;
    }

    public void setPadelTeamA(OlePadelScore padelTeamA) {
        this.padelTeamA = padelTeamA;
    }

    public OlePadelScore getPadelTeamB() {
        return padelTeamB;
    }

    public void setPadelTeamB(OlePadelScore padelTeamB) {
        this.padelTeamB = padelTeamB;
    }

}
