package ae.oleapp.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FinanceHome {

    @SerializedName("bank_earnings")
    @Expose
    private List<BankEarning> bankEarnings;
    @SerializedName("partner_earnings")
    @Expose
    private List<PartnerEarning> partnerEarnings;
    @SerializedName("employee_salaries")
    @Expose
    private List<EmployeeSalary> employeeSalaries;
    @SerializedName("available_earnings")
    @Expose
    private String availableEarnings;
    @SerializedName("total_incomes")
    @Expose
    private String totalIncomes;
    @SerializedName("total_expenses")
    @Expose
    private String totalExpenses;
    @SerializedName("upcoming_expenses")
    @Expose
    private String upcomingExpenses;
    @SerializedName("currency")
    @Expose
    private String currency;

    public List<BankEarning> getBankEarnings() {
        return bankEarnings;
    }

    public void setBankEarnings(List<BankEarning> bankEarnings) {
        this.bankEarnings = bankEarnings;
    }

    public List<PartnerEarning> getPartnerEarnings() {
        return partnerEarnings;
    }

    public void setPartnerEarnings(List<PartnerEarning> partnerEarnings) {
        this.partnerEarnings = partnerEarnings;
    }

    public List<EmployeeSalary> getEmployeeSalaries() {
        return employeeSalaries;
    }

    public void setEmployeeSalaries(List<EmployeeSalary> employeeSalaries) {
        this.employeeSalaries = employeeSalaries;
    }

    public String getAvailableEarnings() {
        return availableEarnings;
    }

    public void setAvailableEarnings(String availableEarnings) {
        this.availableEarnings = availableEarnings;
    }

    public String getTotalIncomes() {
        return totalIncomes;
    }

    public void setTotalIncomes(String totalIncomes) {
        this.totalIncomes = totalIncomes;
    }

    public String getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(String totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public String getUpcomingExpenses() {
        return upcomingExpenses;
    }

    public void setUpcomingExpenses(String upcomingExpenses) {
        this.upcomingExpenses = upcomingExpenses;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
