package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupData {

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
    @SerializedName("added")
    @Expose
    private String added;
    @SerializedName("is_rated")
    @Expose
    private String isRated;
    @SerializedName("group_name")
    @Expose
    private String groupName;
    @SerializedName("game_id")
    @Expose
    private String gameId;
    @SerializedName("game_date")
    @Expose
    private String gameDate;
    @SerializedName("game_time")
    @Expose
    private String gameTime;
    @SerializedName("game_players")
    @Expose
    private String gamePlayers;
    @SerializedName("is_game_on")
    @Expose
    private String isGameOn;
    @SerializedName("user_captain")
    @Expose
    private String userCaptain;

    @SerializedName("in_game")
    @Expose
    private String inGame;

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

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public String getIsRated() {
        return isRated;
    }

    public void setIsRated(String isRated) {
        this.isRated = isRated;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameDate() {
        return gameDate;
    }

    public void setGameDate(String gameDate) {
        this.gameDate = gameDate;
    }

    public String getGameTime() {
        return gameTime;
    }

    public void setGameTime(String gameTime) {
        this.gameTime = gameTime;
    }

    public String getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(String gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public String getIsGameOn() {
        return isGameOn;
    }

    public void setIsGameOn(String isGameOn) {
        this.isGameOn = isGameOn;
    }

    public String getUserCaptain() {
        return userCaptain;
    }

    public void setUserCaptain(String userCaptain) {
        this.userCaptain = userCaptain;
    }

    public String getInGame() {
        return inGame;
    }

    public void setInGame(String inGame) {
        this.inGame = inGame;
    }
}
