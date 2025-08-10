package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleStockHistory {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("stock")
    @Expose
    private String stock;
    @SerializedName("old_stock")
    @Expose
    private String oldStock;
    @SerializedName("purchased_price")
    @Expose
    private String purchasedPrice;
    @SerializedName("sale_price")
    @Expose
    private String salePrice;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("added_by")
    @Expose
    private String addedBy;
    @SerializedName("added_date")
    @Expose
    private String addedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getOldStock() {
        return oldStock;
    }

    public void setOldStock(String oldStock) {
        this.oldStock = oldStock;
    }

    public String getPurchasedPrice() {
        return purchasedPrice;
    }

    public void setPurchasedPrice(String purchasedPrice) {
        this.purchasedPrice = purchasedPrice;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

}
