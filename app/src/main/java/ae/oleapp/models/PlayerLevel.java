package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlayerLevel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("targets")
    @Expose
    private List<LevelsTarget> targets;
    @SerializedName("prev_level_value")
    @Expose
    private String prevLevelValue;
    @SerializedName("next_level_value")
    @Expose
    private String nextLevelValue;
    @SerializedName("next_level_reward")
    @Expose
    private String nextLevelReward;
    @SerializedName("completion_percentage")
    @Expose
    private Float completionPercentage;
    @SerializedName("reward_type")
    @Expose
    private String rewardType;
    @SerializedName("reward_collected")
    @Expose
    private String rewardCollected;
    @SerializedName("collection_msg")
    @Expose
    private String collectionMsg;
    @SerializedName("reward_amount")
    @Expose
    private String rewardAmount;
    @SerializedName("dismiss_id")
    @Expose
    private String dismissId;
    @SerializedName("message")
    @Expose
    private String message;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<LevelsTarget> getTargets() {
        return targets;
    }

    public void setTargets(List<LevelsTarget> targets) {
        this.targets = targets;
    }

    public String getPrevLevelValue() {
        return prevLevelValue;
    }

    public void setPrevLevelValue(String prevLevelValue) {
        this.prevLevelValue = prevLevelValue;
    }

    public String getNextLevelValue() {
        return nextLevelValue;
    }

    public void setNextLevelValue(String nextLevelValue) {
        this.nextLevelValue = nextLevelValue;
    }

    public String getNextLevelReward() {
        return nextLevelReward;
    }

    public void setNextLevelReward(String nextLevelReward) {
        this.nextLevelReward = nextLevelReward;
    }

    public Float getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Float completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
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

    public String getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(String rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public String getDismissId() {
        return dismissId;
    }

    public void setDismissId(String dismissId) {
        this.dismissId = dismissId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isEmpty() {
        return value == null;
    }

}