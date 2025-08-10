package ae.oleapp.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Field {

    @SerializedName("field_type")
    @Expose
    private OleFieldDataChild fieldType;
    @SerializedName("grass_type")
    @Expose
    private OleFieldDataChild grassType;
    @SerializedName("field_size")
    @Expose
    private OleFieldDataChild fieldSize;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image")
    @Expose
    private OleImageData image;
    @SerializedName("one_hour")
    @Expose
    private String oneHour;
    @SerializedName("one_half_hours")
    @Expose
    private String oneHalfHours;
    @SerializedName("two_hours")
    @Expose
    private String twoHours;
    @SerializedName("images")
    @Expose
    private List<OleImageData> images = null;
    @SerializedName("is_block")
    @Expose
    private String isBlock;
    @SerializedName("is_disable")
    @Expose
    private String isDisable;
    @SerializedName("offer_data")
    @Expose
    private OleOfferData oleOfferData;
    @SerializedName("is_offer")
    @Expose
    private String isOffer;
    @SerializedName("field_color")
    @Expose
    private String fieldColor;
    @SerializedName("offers")
    @Expose
    private List<OleOfferData> offers = null;
    @SerializedName("country")
    @Expose
    private OleCountry oleCountry;
    @SerializedName("city")
    @Expose
    private OleCountry city;
    @SerializedName("is_merge")
    @Expose
    private String isMerge;
    @SerializedName("field1_id")
    @Expose
    private String field1Id;
    @SerializedName("field2_id")
    @Expose
    private String field2Id;
    @SerializedName("field3_id")
    @Expose
    private String field3Id;
    @SerializedName("days_price")
    @Expose
    private List<OleFieldPrice> daysPrice = null;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("match_allowed")
    @Expose
    private String matchAllowed;
    @SerializedName("games_allowed")
    @Expose
    private String gamesAllowed;
    @SerializedName("slots")
    @Expose
    private List<OleBookingSlot> slotList;
    @SerializedName("continuous_allowed")
    @Expose
    private String continuousAllowed;

    public OleFieldDataChild getFieldType() {
        return fieldType;
    }

    public void setFieldType(OleFieldDataChild fieldType) {
        this.fieldType = fieldType;
    }

    public OleFieldDataChild getGrassType() {
        return grassType;
    }

    public void setGrassType(OleFieldDataChild grassType) {
        this.grassType = grassType;
    }

    public OleFieldDataChild getFieldSize() {
        return fieldSize;
    }

    public void setFieldSize(OleFieldDataChild fieldSize) {
        this.fieldSize = fieldSize;
    }

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

    public OleImageData getImage() {
        return image;
    }

    public void setImage(OleImageData image) {
        this.image = image;
    }

    public String getOneHour() {
        return oneHour;
    }

    public void setOneHour(String oneHour) {
        this.oneHour = oneHour;
    }

    public String getOneHalfHours() {
        return oneHalfHours;
    }

    public void setOneHalfHours(String oneHalfHours) {
        this.oneHalfHours = oneHalfHours;
    }

    public String getTwoHours() {
        return twoHours;
    }

    public void setTwoHours(String twoHours) {
        this.twoHours = twoHours;
    }

    public List<OleImageData> getImages() {
        return images;
    }

    public void setImages(List<OleImageData> images) {
        this.images = images;
    }

    public String getIsBlock() {
        return isBlock;
    }

    public void setIsBlock(String isBlock) {
        this.isBlock = isBlock;
    }

    public String getIsDisable() {
        return isDisable;
    }

    public void setIsDisable(String isDisable) {
        this.isDisable = isDisable;
    }

    public OleOfferData getOfferData() {
        return oleOfferData;
    }

    public void setOfferData(OleOfferData oleOfferData) {
        this.oleOfferData = oleOfferData;
    }

    public String getIsOffer() {
        return isOffer;
    }

    public void setIsOffer(String isOffer) {
        this.isOffer = isOffer;
    }

    public String getFieldColor() {
        return fieldColor;
    }

    public void setFieldColor(String fieldColor) {
        this.fieldColor = fieldColor;
    }

    public List<OleOfferData> getOffers() {
        return offers;
    }

    public void setOffers(List<OleOfferData> offers) {
        this.offers = offers;
    }

    public OleCountry getCountry() {
        return oleCountry;
    }

    public void setCountry(OleCountry oleCountry) {
        this.oleCountry = oleCountry;
    }

    public OleCountry getCity() {
        return city;
    }

    public void setCity(OleCountry city) {
        this.city = city;
    }

    public String getIsMerge() {
        return isMerge;
    }

    public void setIsMerge(String isMerge) {
        this.isMerge = isMerge;
    }

    public String getField1Id() {
        return field1Id;
    }

    public void setField1Id(String field1Id) {
        this.field1Id = field1Id;
    }

    public String getField2Id() {
        return field2Id;
    }

    public void setField2Id(String field2Id) {
        this.field2Id = field2Id;
    }

    public String getField3Id() {
        return field3Id;
    }

    public void setField3Id(String field3Id) {
        this.field3Id = field3Id;
    }

    public List<OleFieldPrice> getDaysPrice() {
        return daysPrice;
    }

    public void setDaysPrice(List<OleFieldPrice> daysPrice) {
        this.daysPrice = daysPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMatchAllowed() {
        return matchAllowed;
    }

    public void setMatchAllowed(String matchAllowed) {
        this.matchAllowed = matchAllowed;
    }

    public String getGamesAllowed() {
        return gamesAllowed;
    }

    public void setGamesAllowed(String gamesAllowed) {
        this.gamesAllowed = gamesAllowed;
    }

    public List<OleBookingSlot> getSlotList() {
        return slotList;
    }

    public void setSlotList(List<OleBookingSlot> slotList) {
        this.slotList = slotList;
    }

    public String getContinuousAllowed() {
        return continuousAllowed;
    }

    public void setContinuousAllowed(String continuousAllowed) {
        this.continuousAllowed = continuousAllowed;
    }
}
