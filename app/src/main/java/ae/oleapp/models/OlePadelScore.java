package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OlePadelScore {

    @SerializedName("set_one")
    @Expose
    private Integer setOne;
    @SerializedName("set_two")
    @Expose
    private Integer setTwo;
    @SerializedName("set_three")
    @Expose
    private Integer setThree;

    public Integer getSetOne() {
        return setOne;
    }

    public void setSetOne(Integer setOne) {
        this.setOne = setOne;
    }

    public Integer getSetTwo() {
        return setTwo;
    }

    public void setSetTwo(Integer setTwo) {
        this.setTwo = setTwo;
    }

    public Integer getSetThree() {
        return setThree;
    }

    public void setSetThree(Integer setThree) {
        this.setThree = setThree;
    }

}