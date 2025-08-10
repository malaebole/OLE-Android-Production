package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Day {

    @SerializedName("day_id")
    @Expose
    private String dayId;
    @SerializedName("day_name")
    @Expose
    private String dayName;
    @SerializedName("shifting")
    @Expose
    private List<OleShiftTime> shifting = new ArrayList<>();

    private boolean is24Hours = false;

    public String getDayId() {
        return dayId;
    }

    public void setDayId(String dayId) {
        this.dayId = dayId;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public List<OleShiftTime> getShifting() {
        return shifting;
    }

    public void setShifting(List<OleShiftTime> shifting) {
        this.shifting = shifting;
    }

    public boolean is24Hours() {
        return is24Hours;
    }

    public void setIs24Hours(boolean is24Hours) {
        this.is24Hours = is24Hours;
    }
}
