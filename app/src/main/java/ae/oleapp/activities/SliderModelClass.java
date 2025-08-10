package ae.oleapp.activities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SliderModelClass {

    @SerializedName("friend_id")
    @Expose
    private String friendId;
    @SerializedName("game_id")
    @Expose
    private String gameId;
    @SerializedName("in_game")
    @Expose
    private String inGame;
    @SerializedName("group_name")
    @Expose
    private String groupName;

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getInGame() {
        return inGame;
    }

    public void setInGame(String inGame) {
        this.inGame = inGame;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}