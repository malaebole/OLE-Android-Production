package ae.oleapp.models;

public class SelectionList {
    String id;
    String value;
    String imgUrl;

    public SelectionList(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public SelectionList(String id, String value, String imgUrl) {
        this.id = id;
        this.value = value;
        this.imgUrl = imgUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
