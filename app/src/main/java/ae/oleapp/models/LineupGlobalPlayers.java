package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LineupGlobalPlayers {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("nick_name")
    @Expose
    private String nickName;
    @SerializedName("arabic_name")
    @Expose
    private String arabicName;
    @SerializedName("emoji_url")
    @Expose
    private String emojiUrl;
    @SerializedName("bib_url")
    @Expose
    private String bibUrl;
    @SerializedName("added_by")
    @Expose
    private String addedBy;
    @SerializedName("bib_id")
    @Expose
    private String bibId;
    @SerializedName("x_coordinate")
    @Expose
    private String xCoordinate;
    @SerializedName("y_coordinate")
    @Expose
    private String yCoordinate;
    @SerializedName("is_goalkeeper")
    @Expose
    private String isGoalkeeper;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getArabicName() {
        return arabicName;
    }
    public void setArabicName(String arabicName) {
        this.arabicName = arabicName;
    }

    public String getEmojiUrl() {
        return emojiUrl;
    }

    public void setEmojiUrl(String emojiUrl) {
        this.emojiUrl = emojiUrl;
    }

    public String getBibUrl() {
        return bibUrl;
    }

    public void setBibUrl(String bibUrl) {
        this.bibUrl = bibUrl;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getBibId() {
        return bibId;
    }

    public void setBibId(String bibId) {
        this.bibId = bibId;
    }


    public String getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(String xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public String getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(String yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public String getIsGoalkeeper() {
        return isGoalkeeper;
    }

    public void setIsGoalkeeper(String isGoalkeeper) {
        this.isGoalkeeper = isGoalkeeper;
    }
}
