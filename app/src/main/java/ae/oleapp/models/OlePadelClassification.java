package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OlePadelClassification {

    @SerializedName("player_data")
    @Expose
    private OlePlayerInfo playerData;
    @SerializedName("document")
    @Expose
    private String document;
    @SerializedName("class")
    @Expose
    private String classRank;

    public OlePlayerInfo getPlayerData() {
        return playerData;
    }

    public void setPlayerData(OlePlayerInfo playerData) {
        this.playerData = playerData;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getClassRank() {
        return classRank;
    }

    public void s(String classRank) {
        this.classRank = classRank;
    }

}