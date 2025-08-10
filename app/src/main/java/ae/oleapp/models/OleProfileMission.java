package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OleProfileMission {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("reward_name")
    @Expose
    private String rewardName;
    @SerializedName("reward_photo")
    @Expose
    private String rewardPhoto;
    @SerializedName("reward_desc")
    @Expose
    private String rewardDesc;
    @SerializedName("reward_collected")
    @Expose
    private String rewardCollected;
    @SerializedName("collection_msg")
    @Expose
    private String collectionMsg;
    @SerializedName("targets")
    @Expose
    private List<OleLevelsTarget> targets = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public String getRewardPhoto() {
        return rewardPhoto;
    }

    public void setRewardPhoto(String rewardPhoto) {
        this.rewardPhoto = rewardPhoto;
    }

    public String getRewardDesc() {
        return rewardDesc;
    }

    public void setRewardDesc(String rewardDesc) {
        this.rewardDesc = rewardDesc;
    }

    public String getRewardCollected() {
        return rewardCollected;
    }

    public void setRewardCollected(String rewardCollected) {
        this.rewardCollected = rewardCollected;
    }

    public String getCollectionMsg() {
        return collectionMsg;
    }

    public void setCollectionMsg(String collectionMsg) {
        this.collectionMsg = collectionMsg;
    }

    public List<OleLevelsTarget> getTargets() {
        return targets;
    }

    public void setTargets(List<OleLevelsTarget> targets) {
        this.targets = targets;
    }

}