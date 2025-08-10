package ae.oleapp.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class FollowRequestModel {

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
    @SerializedName("added_date")
    @Expose
    private String addedDate;
    @SerializedName("in_game")
    @Expose
    private String inGame;
    @SerializedName("is_link")
    @Expose
    private String isLink;
    @SerializedName("win_percentage")
    @Expose
    private String winPercentage;
    @SerializedName("match_played")
    @Expose
    private String matchPlayed;
    @SerializedName("match_loss")
    @Expose
    private String matchLoss;
    @SerializedName("match_won")
    @Expose
    private String matchWon;
    @SerializedName("match_drawn")
    @Expose
    private String matchDrawn;
    @SerializedName("player_status")
    @Expose
    private PlayerStatus playerStatus;
    @SerializedName("is_requested")
    @Expose
    private String isRequested;

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

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getInGame() {
        return inGame;
    }

    public void setInGame(String inGame) {
        this.inGame = inGame;
    }

    public String getIsLink() {
        return isLink;
    }

    public void setIsLink(String isLink) {
        this.isLink = isLink;
    }

    public String getWinPercentage() {
        return winPercentage;
    }

    public void setWinPercentage(String winPercentage) {
        this.winPercentage = winPercentage;
    }

    public String getMatchPlayed() {
        return matchPlayed;
    }

    public void setMatchPlayed(String matchPlayed) {
        this.matchPlayed = matchPlayed;
    }

    public String getMatchLoss() {
        return matchLoss;
    }

    public void setMatchLoss(String matchLoss) {
        this.matchLoss = matchLoss;
    }

    public String getMatchWon() {
        return matchWon;
    }

    public void setMatchWon(String matchWon) {
        this.matchWon = matchWon;
    }

    public String getMatchDrawn() {
        return matchDrawn;
    }

    public void setMatchDrawn(String matchDrawn) {
        this.matchDrawn = matchDrawn;
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }

    public String getIsRequested() {
        return isRequested;
    }

    public void setIsRequested(String isRequested) {
        this.isRequested = isRequested;
    }

}