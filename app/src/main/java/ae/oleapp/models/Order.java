package ae.oleapp.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("order_number")
    @Expose
    private String orderNumber;
    @SerializedName("order_date")
    @Expose
    private String orderDate;
    @SerializedName("cod")
    @Expose
    private String cod;
    @SerializedName("shipping_cost")
    @Expose
    private String shippingCost;
    @SerializedName("grand_total")
    @Expose
    private String grandTotal;
    @SerializedName("coupon_discount")
    @Expose
    private String couponDiscount;
    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;
    @SerializedName("payment_type")
    @Expose
    private String paymentType;
    @SerializedName("card_number")
    @Expose
    private String cardNumber;
    @SerializedName("delivery_status")
    @Expose
    private String deliveryStatus;
    @SerializedName("user_address")
    @Expose
    private OleShopAddress userAddress;
    @SerializedName("products")
    @Expose
    private List<Product> products = null;
    @SerializedName("is_reviewed")
    @Expose
    private String isReviewed;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("is_confirmed")
    @Expose
    private String isConfirmed;
    @SerializedName("pickup_address")
    @Expose
    private String pickupAddress;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(String shippingCost) {
        this.shippingCost = shippingCost;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(String couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public OleShopAddress getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(OleShopAddress userAddress) {
        this.userAddress = userAddress;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getIsReviewed() {
        return isReviewed;
    }

    public void setIsReviewed(String isReviewed) {
        this.isReviewed = isReviewed;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(String isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }
}
