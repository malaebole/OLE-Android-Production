package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RatingData {

    @SerializedName("reviews")
    @Expose
    private String reviews;
    @SerializedName("playing_level")
    @Expose
    private String playingLevel;
    @SerializedName("befor_time")
    @Expose
    private String beforTime;
    @SerializedName("on_time")
    @Expose
    private String onTime;
    @SerializedName("late")
    @Expose
    private String late;
    @SerializedName("not_come")
    @Expose
    private String notCome;
    @SerializedName("speed")
    @Expose
    private String speed;
    @SerializedName("shooting")
    @Expose
    private String shooting;
    @SerializedName("dribble")
    @Expose
    private String dribble;
    @SerializedName("fitness")
    @Expose
    private String fitness;
    @SerializedName("defence")
    @Expose
    private String defence;
    @SerializedName("iq_level")
    @Expose
    private String iqLevel;
    @SerializedName("is_card")
    @Expose
    private String isCard;
    @SerializedName("reason")
    @Expose
    private String reason;

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getPlayingLevel() {
        return playingLevel;
    }

    public void setPlayingLevel(String playingLevel) {
        this.playingLevel = playingLevel;
    }

    public String getBeforTime() {
        return beforTime;
    }

    public void setBeforTime(String beforTime) {
        this.beforTime = beforTime;
    }

    public String getOnTime() {
        return onTime;
    }

    public void setOnTime(String onTime) {
        this.onTime = onTime;
    }

    public String getLate() {
        return late;
    }

    public void setLate(String late) {
        this.late = late;
    }

    public String getNotCome() {
        return notCome;
    }

    public void setNotCome(String notCome) {
        this.notCome = notCome;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getShooting() {
        return shooting;
    }

    public void setShooting(String shooting) {
        this.shooting = shooting;
    }

    public String getDribble() {
        return dribble;
    }

    public void setDribble(String dribble) {
        this.dribble = dribble;
    }

    public String getFitness() {
        return fitness;
    }

    public void setFitness(String fitness) {
        this.fitness = fitness;
    }

    public String getDefence() {
        return defence;
    }

    public void setDefence(String defence) {
        this.defence = defence;
    }

    public String getIqLevel() {
        return iqLevel;
    }

    public void setIqLevel(String iqLevel) {
        this.iqLevel = iqLevel;
    }

    public String getIsCard() {
        return isCard;
    }

    public void setIsCard(String isCard) {
        this.isCard = isCard;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
