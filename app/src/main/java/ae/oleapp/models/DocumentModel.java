package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DocumentModel {

    @SerializedName("partners")
    @Expose
    private List<PartnerDocModel> partners;
    @SerializedName("employees")
    @Expose
    private List<EmployeeDocModel> employees;
    @SerializedName("assets")
    @Expose
    private List<DocModel> assets;

    public List<PartnerDocModel> getPartners() {
        return partners;
    }

    public void setPartners(List<PartnerDocModel> partners) {
        this.partners = partners;
    }

    public List<EmployeeDocModel> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeDocModel> employees) {
        this.employees = employees;
    }

    public List<DocModel> getAssets() {
        return assets;
    }

    public void setAssets(List<DocModel> assets) {
        this.assets = assets;
    }
}
