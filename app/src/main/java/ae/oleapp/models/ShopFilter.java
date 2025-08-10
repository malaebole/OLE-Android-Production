package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShopFilter {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("value")
    @Expose
    private List<String> values = null;

    private String selectedValue = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> value) {
        this.values = value;
    }

    public String getValue() {
        return selectedValue;
    }

    public void setValue(String value) {
        this.selectedValue = value;
    }
}
