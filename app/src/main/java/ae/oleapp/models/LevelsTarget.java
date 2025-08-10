package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LevelsTarget {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("dates")
    @Expose
    private String dates;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("active_icon")
    @Expose
    private String activeIcon;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("remaining")
    @Expose
    private Integer remaining;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getActiveIcon() {
        return activeIcon;
    }

    public void setActiveIcon(String activeIcon) {
        this.activeIcon = activeIcon;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }

}
