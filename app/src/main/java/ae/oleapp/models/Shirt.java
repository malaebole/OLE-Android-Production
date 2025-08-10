package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Shirt {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("photo_url")
    @Expose
    private String photoUrl;
    @SerializedName("is_selected")
    @Expose
    private String isSelected;
    @SerializedName("type")
    @Expose
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getIsSelected() {
        return isSelected;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setIsSelected(String isSelected) {
        this.isSelected = isSelected;
    }
}
