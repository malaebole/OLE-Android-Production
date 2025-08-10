package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlayerRate {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("nick_name")
    @Expose
    private String nickName;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("emoji_url")
    @Expose
    private String emojiUrl;
    @SerializedName("bib_id")
    @Expose
    private String bibId;
    @SerializedName("bib_url")
    @Expose
    private String bibUrl;
    @SerializedName("reach_time")
    @Expose
    private String reachTime;
    @SerializedName("feedback")
    @Expose
    private String feedback;
    @SerializedName("playing_level")
    @Expose
    private String playingLevel;
    @SerializedName("rate_added")
    @Expose
    private String rateAdded;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmojiUrl() {
        return emojiUrl;
    }

    public void setEmojiUrl(String emojiUrl) {
        this.emojiUrl = emojiUrl;
    }

    public String getBibId() {
        return bibId;
    }

    public void setBibId(String bibId) {
        this.bibId = bibId;
    }

    public String getBibUrl() {
        return bibUrl;
    }

    public void setBibUrl(String bibUrl) {
        this.bibUrl = bibUrl;
    }

    public String getReachTime() {
        return reachTime;
    }

    public void setReachTime(String reachTime) {
        this.reachTime = reachTime;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getPlayingLevel() {
        return playingLevel;
    }

    public void setPlayingLevel(String playingLevel) {
        this.playingLevel = playingLevel;
    }

    public String getRateAdded() {
        return rateAdded;
    }

    public void setRateAdded(String rateAdded) {
        this.rateAdded = rateAdded;
    }

}
