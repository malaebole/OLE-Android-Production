package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeliveryCity {

    @SerializedName("city_name")
    @Expose
    private String cityName;
    @SerializedName("order_in")
    @Expose
    private String orderIn;
    @SerializedName("delivery_date")
    @Expose
    private String deliveryDate;
    @SerializedName("defualt")
    @Expose
    private String defualt;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getOrderIn() {
        return orderIn;
    }

    public void setOrderIn(String orderIn) {
        this.orderIn = orderIn;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDefualt() {
        return defualt;
    }

    public void setDefualt(String defualt) {
        this.defualt = defualt;
    }

}