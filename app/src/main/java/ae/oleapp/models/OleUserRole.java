package ae.oleapp.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleUserRole {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("role_id")
    @Expose
    private String roleId;
    @SerializedName("role_name")
    @Expose
    private String roleName;
    @SerializedName("permissions")
    @Expose
    private List<OleUserPermission> permissions = null;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public List<OleUserPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<OleUserPermission> permissions) {
        this.permissions = permissions;
    }

}
