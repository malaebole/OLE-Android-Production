package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Employee {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("employee_id")
    @Expose
    private String employeeId;
    @SerializedName("role_id")
    @Expose
    private String roleId;
    @SerializedName("role_name")
    @Expose
    private String roleName;
    @SerializedName("employee_photo")
    @Expose
    private String employeePhoto;
    @SerializedName("club_id")
    @Expose
    private String clubId;

    @SerializedName("salary")
    @Expose
    private String salary;
    @SerializedName("club_name")
    @Expose
    private String clubName;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("reviews_count")
    @Expose
    private String reviewsCount;
    @SerializedName("total_tip")
    @Expose
    private String totalTip;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("payable")
    @Expose
    private String payable;
    @SerializedName("today_tip")
    @Expose
    private String todayTip;
    @SerializedName("last_week_tip")
    @Expose
    private String lastWeekTip;
    @SerializedName("last_month_tip")
    @Expose
    private String lastMonthTip;
    @SerializedName("last_month_title")
    @Expose
    private String lastMonthTitle;
    @SerializedName("lifetime_tip")
    @Expose
    private String lifetimeTip;
    @SerializedName("paid_tips")
    @Expose
    private List<OleTipPayment> paidTips = null;
    @SerializedName("employee_type")
    @Expose
    private String employeeType;
    @SerializedName("assigned_countries")
    @Expose
    private String assignedCountries;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getEmployeePhoto() {
        return employeePhoto;
    }

    public void setEmployeePhoto(String employeePhoto) {
        this.employeePhoto = employeePhoto;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(String reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public String getTotalTip() {
        return totalTip;
    }

    public void setTotalTip(String totalTip) {
        this.totalTip = totalTip;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPayable() {
        return payable;
    }

    public void setPayable(String payable) {
        this.payable = payable;
    }

    public String getTodayTip() {
        return todayTip;
    }

    public void setTodayTip(String todayTip) {
        this.todayTip = todayTip;
    }

    public String getLastWeekTip() {
        return lastWeekTip;
    }

    public void setLastWeekTip(String lastWeekTip) {
        this.lastWeekTip = lastWeekTip;
    }

    public String getLastMonthTip() {
        return lastMonthTip;
    }

    public void setLastMonthTip(String lastMonthTip) {
        this.lastMonthTip = lastMonthTip;
    }

    public String getLastMonthTitle() {
        return lastMonthTitle;
    }

    public void setLastMonthTitle(String lastMonthTitle) {
        this.lastMonthTitle = lastMonthTitle;
    }

    public String getLifetimeTip() {
        return lifetimeTip;
    }

    public void setLifetimeTip(String lifetimeTip) {
        this.lifetimeTip = lifetimeTip;
    }

    public List<OleTipPayment> getPaidTips() {
        return paidTips;
    }

    public void setPaidTips(List<OleTipPayment> paidTips) {
        this.paidTips = paidTips;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public String getAssignedCountries() {
        return assignedCountries;
    }

    public void setAssignedCountries(String assignedCountries) {
        this.assignedCountries = assignedCountries;
    }

}
