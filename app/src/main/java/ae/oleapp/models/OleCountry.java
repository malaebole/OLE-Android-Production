package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OleCountry {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("iso_code")
    @Expose
    private String isoCode;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("cities")
    @Expose
    private List<OleCountry> cities = null;

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

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<OleCountry> getCities() {
        return cities;
    }

    public void setCities(List<OleCountry> cities) {
        this.cities = cities;
    }

}
