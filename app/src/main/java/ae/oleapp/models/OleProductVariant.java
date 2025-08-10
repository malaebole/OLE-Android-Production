package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleProductVariant implements Comparable {

    private int index;
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("value")
    @Expose
    private String value;

    public OleProductVariant(int index, String id, String title, String value) {
        this.index = index;
        this.id = id;
        this.title = title;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int compareTo(Object o) {
        int compareIndex = ((OleProductVariant)o).getIndex();
        /* For Ascending order*/
        return this.index-compareIndex;
    }
}
