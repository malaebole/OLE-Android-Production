package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FieldImage {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("field_img")
    @Expose
    private String fieldImg;
    @SerializedName("bg_img")
    @Expose
    private String bgImg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFieldImg() {
        return fieldImg;
    }

    public void setFieldImg(String fieldImg) {
        this.fieldImg = fieldImg;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }
}
