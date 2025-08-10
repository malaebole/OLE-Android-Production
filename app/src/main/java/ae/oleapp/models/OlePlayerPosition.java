package ae.oleapp.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OlePlayerPosition {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("position_id")
    @Expose
    private String positionId;
    @SerializedName("selected_number")
    @Expose
    private String selectedNumber;
    @SerializedName("position_number")
    @Expose
    private List<String> positionNumber = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getSelectedNumber() {
        return selectedNumber;
    }

    public void setSelectedNumber(String selectedNumber) {
        this.selectedNumber = selectedNumber;
    }

    public List<String> getPositionNumber() {
        return positionNumber;
    }

    public void setPositionNumber(List<String> positionNumber) {
        this.positionNumber = positionNumber;
    }

    public boolean isEmpty() {
        return name == null || name.isEmpty();
    }
}
