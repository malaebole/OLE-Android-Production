package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OlePurchaseHistory {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("total_sold")
    @Expose
    private String totalSold;
    @SerializedName("current_stock")
    @Expose
    private String currentStock;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("purchase_id")
    @Expose
    private String purchaseId;
    @SerializedName("purchase_new_stock")
    @Expose
    private String purchaseNewStock;
    @SerializedName("purchase_old_stock")
    @Expose
    private String purchaseOldStock;
    @SerializedName("purchase_price")
    @Expose
    private String purchasePrice;
    @SerializedName("purchase_sale_price")
    @Expose
    private String purchaseSalePrice;
    @SerializedName("purchased_by")
    @Expose
    private String purchasedBy;
    @SerializedName("purchased_date")
    @Expose
    private String purchasedDate;

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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(String totalSold) {
        this.totalSold = totalSold;
    }

    public String getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(String currentStock) {
        this.currentStock = currentStock;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getPurchaseNewStock() {
        return purchaseNewStock;
    }

    public void setPurchaseNewStock(String purchaseNewStock) {
        this.purchaseNewStock = purchaseNewStock;
    }

    public String getPurchaseOldStock() {
        return purchaseOldStock;
    }

    public void setPurchaseOldStock(String purchaseOldStock) {
        this.purchaseOldStock = purchaseOldStock;
    }

    public String getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getPurchaseSalePrice() {
        return purchaseSalePrice;
    }

    public void setPurchaseSalePrice(String purchaseSalePrice) {
        this.purchaseSalePrice = purchaseSalePrice;
    }

    public String getPurchasedBy() {
        return purchasedBy;
    }

    public void setPurchasedBy(String purchasedBy) {
        this.purchasedBy = purchasedBy;
    }

    public String getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(String purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

}
