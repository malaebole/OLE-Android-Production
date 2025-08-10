package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SelectionModel {

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("team")
    @Expose
    private String team;
    @SerializedName("shirt")
    @Expose
    private String shirt;
    @SerializedName("background")
    @Expose
    private String background;
    @SerializedName("chair")
    @Expose
    private String chair;
    @SerializedName("goalkeeper")
    @Expose
    private String goalkeeper;

    @SerializedName("field_image_url")
    @Expose
    private String fieldImageUrl;
    @SerializedName("bg_image_url")
    @Expose
    private String bgImageUrl;
    @SerializedName("share_image_url")
    @Expose
    private String shareImageUrl;
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getShirt() {
        return shirt;
    }

    public void setShirt(String shirt) {
        this.shirt = shirt;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getChair() {
        return chair;
    }

    public void setChair(String chair) {
        this.chair = chair;
    }

    public String getGoalkeeper() {
        return goalkeeper;
    }

    public void setGoalkeeper(String goalkeeper) {
        this.goalkeeper = goalkeeper;
    }

    public String getFieldImageUrl() {
        return fieldImageUrl;
    }

    public void setFieldImageUrl(String fieldImageUrl) {
        this.fieldImageUrl = fieldImageUrl;
    }

    public String getBgImageUrl() {
        return bgImageUrl;
    }

    public void setBgImageUrl(String bgImageUrl) {
        this.bgImageUrl = bgImageUrl;
    }

    public String getShareImageUrl() {
        return shareImageUrl;
    }

    public void setShareImageUrl(String shareImageUrl) {
        this.shareImageUrl = shareImageUrl;
    }


























}
