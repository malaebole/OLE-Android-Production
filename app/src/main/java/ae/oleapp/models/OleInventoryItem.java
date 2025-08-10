package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OleInventoryItem {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("purchased_price")
    @Expose
    private String purchasedPrice;
    @SerializedName("sale_price")
    @Expose
    private String salePrice;
    @SerializedName("total_sold")
    @Expose
    private String totalSold;
    @SerializedName("current_stock")
    @Expose
    private String currentStock;
    @SerializedName("added_date")
    @Expose
    private String addedDate;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("profit_amount")
    @Expose
    private String profitAmount;
    @SerializedName("stock_history")
    @Expose
    private List<OleStockHistory> oleStockHistory = null;

    private int qty = 0;

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

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(String profitAmount) {
        this.profitAmount = profitAmount;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public List<OleStockHistory> getStockHistory() {
        return oleStockHistory;
    }

    public void setStockHistory(List<OleStockHistory> oleStockHistory) {
        this.oleStockHistory = oleStockHistory;
    }
}
