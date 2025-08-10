package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("sale_price")
    @Expose
    private String salePrice;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("discount_type")
    @Expose
    private String discountType;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("is_favorite")
    @Expose
    private String isFavorite;
    @SerializedName("total_in_offer")
    @Expose
    private String totalInOffer;
    @SerializedName("sold")
    @Expose
    private String sold;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("brand_id")
    @Expose
    private String brandId;
    @SerializedName("photos")
    @Expose
    private List<String> photos = null;
    @SerializedName("detail_img")
    @Expose
    private String detailImg;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("unit_price")
    @Expose
    private String unitPrice;
    @SerializedName("variant_product")
    @Expose
    private String variantProduct;
    @SerializedName("choice_options")
    @Expose
    private List<OleChoiceOption> oleChoiceOptions = null;
    @SerializedName("colors")
    @Expose
    private List<OleProductColor> colors = null;
    @SerializedName("todays_deal")
    @Expose
    private String todaysDeal;
    @SerializedName("current_stock")
    @Expose
    private String currentStock;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("min_qty")
    @Expose
    private String minQty;
    @SerializedName("tax")
    @Expose
    private String tax;
    @SerializedName("tax_type")
    @Expose
    private String taxType;
    @SerializedName("shipping_type")
    @Expose
    private String shippingType;
    @SerializedName("shipping_cost")
    @Expose
    private String shippingCost;
    @SerializedName("num_of_sale")
    @Expose
    private String numOfSale;
    @SerializedName("product_sku")
    @Expose
    private String productSku;
    @SerializedName("specifications")
    @Expose
    private List<OleProductSpecs> specifications = null;
    @SerializedName("share_link")
    @Expose
    private String shareLink;
    @SerializedName("attribute_combinations")
    @Expose
    private List<OleAttributeCombination> oleAttributeCombinations = null;
    @SerializedName("category")
    @Expose
    private OleProductCategory category;
    @SerializedName("brand")
    @Expose
    private OleProductBrand brand;
    @SerializedName("related")
    @Expose
    private List<Product> related = null;
    @SerializedName("reviews")
    @Expose
    private List<OleProductReview> reviews = null;
    @SerializedName("reviews_count")
    @Expose
    private String reviewsCount;
    @SerializedName("product_number")
    @Expose
    private String productNumber;
    @SerializedName("delivery_data")
    @Expose
    private List<OleDeliveryCity> deliveryData = null;
    @SerializedName("free_return")
    @Expose
    private String freeReturn;
    @SerializedName("leave_at_door")
    @Expose
    private String leaveAtDoor;
    @SerializedName("free_shipping_limit")
    @Expose
    private String freeShippingLimit;
    @SerializedName("delivery_status")
    @Expose
    private String deliveryStatus;
    @SerializedName("qty")
    @Expose
    private String qty;
    @SerializedName("color_code")
    @Expose
    private String colorCode;
    @SerializedName("is_reviewed")
    @Expose
    private String isReviewed;
    @SerializedName("attributes")
    @Expose
    private List<OleProductVariant> attributes;
    @SerializedName("fast_delivery")
    @Expose
    private String fastDelivery;

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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(String isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getTotalInOffer() {
        return totalInOffer;
    }

    public void setTotalInOffer(String totalInOffer) {
        this.totalInOffer = totalInOffer;
    }

    public String getSold() {
        return sold;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public String getDetailImg() {
        return detailImg;
    }

    public void setDetailImg(String detailImg) {
        this.detailImg = detailImg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getVariantProduct() {
        return variantProduct;
    }

    public void setVariantProduct(String variantProduct) {
        this.variantProduct = variantProduct;
    }

    public List<OleChoiceOption> getChoiceOptions() {
        return oleChoiceOptions;
    }

    public void setChoiceOptions(List<OleChoiceOption> oleChoiceOptions) {
        this.oleChoiceOptions = oleChoiceOptions;
    }

    public List<OleProductColor> getColors() {
        return colors;
    }

    public void setColors(List<OleProductColor> colors) {
        this.colors = colors;
    }

    public String getTodaysDeal() {
        return todaysDeal;
    }

    public void setTodaysDeal(String todaysDeal) {
        this.todaysDeal = todaysDeal;
    }

    public String getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(String currentStock) {
        this.currentStock = currentStock;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMinQty() {
        return minQty;
    }

    public void setMinQty(String minQty) {
        this.minQty = minQty;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getShippingType() {
        return shippingType;
    }

    public void setShippingType(String shippingType) {
        this.shippingType = shippingType;
    }

    public String getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(String shippingCost) {
        this.shippingCost = shippingCost;
    }

    public String getNumOfSale() {
        return numOfSale;
    }

    public void setNumOfSale(String numOfSale) {
        this.numOfSale = numOfSale;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public List<OleProductSpecs> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<OleProductSpecs> specifications) {
        this.specifications = specifications;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public List<OleAttributeCombination> getAttributeCombinations() {
        return oleAttributeCombinations;
    }

    public void setAttributeCombinations(List<OleAttributeCombination> oleAttributeCombinations) {
        this.oleAttributeCombinations = oleAttributeCombinations;
    }

    public OleProductCategory getCategory() {
        return category;
    }

    public void setCategory(OleProductCategory category) {
        this.category = category;
    }

    public OleProductBrand getBrand() {
        return brand;
    }

    public void setBrand(OleProductBrand brand) {
        this.brand = brand;
    }

    public List<Product> getRelated() {
        return related;
    }

    public void setRelated(List<Product> related) {
        this.related = related;
    }

    public List<OleProductReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<OleProductReview> reviews) {
        this.reviews = reviews;
    }

    public String getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(String reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public List<OleDeliveryCity> getDeliveryData() {
        return deliveryData;
    }

    public void setDeliveryData(List<OleDeliveryCity> deliveryData) {
        this.deliveryData = deliveryData;
    }

    public String getFreeReturn() {
        return freeReturn;
    }

    public void setFreeReturn(String freeReturn) {
        this.freeReturn = freeReturn;
    }

    public String getLeaveAtDoor() {
        return leaveAtDoor;
    }

    public void setLeaveAtDoor(String leaveAtDoor) {
        this.leaveAtDoor = leaveAtDoor;
    }

    public String getFreeShippingLimit() {
        return freeShippingLimit;
    }

    public void setFreeShippingLimit(String freeShippingLimit) {
        this.freeShippingLimit = freeShippingLimit;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public List<OleProductVariant> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<OleProductVariant> attributes) {
        this.attributes = attributes;
    }

    public String getIsReviewed() {
        return isReviewed;
    }

    public void setIsReviewed(String isReviewed) {
        this.isReviewed = isReviewed;
    }

    public String getFastDelivery() {
        return fastDelivery;
    }

    public void setFastDelivery(String fastDelivery) {
        this.fastDelivery = fastDelivery;
    }
}
