package ae.oleapp.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleFieldData {

    @SerializedName("field_sizes")
    @Expose
    private List<OleFieldDataChild> fieldSizes = null;
    @SerializedName("filed_types")
    @Expose
    private List<OleFieldDataChild> filedTypes = null;
    @SerializedName("grass_type")
    @Expose
    private List<OleFieldDataChild> grassType = null;

    public List<OleFieldDataChild> getFieldSizes() {
        return fieldSizes;
    }

    public void setFieldSizes(List<OleFieldDataChild> fieldSizes) {
        this.fieldSizes = fieldSizes;
    }

    public List<OleFieldDataChild> getFiledTypes() {
        return filedTypes;
    }

    public void setFiledTypes(List<OleFieldDataChild> filedTypes) {
        this.filedTypes = filedTypes;
    }

    public List<OleFieldDataChild> getGrassType() {
        return grassType;
    }

    public void setGrassType(List<OleFieldDataChild> grassType) {
        this.grassType = grassType;
    }

}