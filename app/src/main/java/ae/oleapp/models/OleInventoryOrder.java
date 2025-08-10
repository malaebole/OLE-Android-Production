package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OleInventoryOrder {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("sub_total")
    @Expose
    private String subTotal;
    @SerializedName("grand_total")
    @Expose
    private String grandTotal;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("total_qty")
    @Expose
    private String totalQty;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("receipt_photo")
    @Expose
    private String receiptPhoto;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("employee_name")
    @Expose
    private String employeeName;
    @SerializedName("sold_by")
    @Expose
    private String soldBy;
    @SerializedName("added_date")
    @Expose
    private String addedDate;
    @SerializedName("order_items")
    @Expose
    private List<OleInventoryItem> orderItems = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(String totalQty) {
        this.totalQty = totalQty;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReceiptPhoto() {
        return receiptPhoto;
    }

    public void setReceiptPhoto(String receiptPhoto) {
        this.receiptPhoto = receiptPhoto;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getSoldBy() {
        return soldBy;
    }

    public void setSoldBy(String soldBy) {
        this.soldBy = soldBy;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public List<OleInventoryItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OleInventoryItem> orderItems) {
        this.orderItems = orderItems;
    }

}
