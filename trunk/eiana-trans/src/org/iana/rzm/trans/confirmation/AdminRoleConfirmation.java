package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.Role;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.Entity;
import javax.persistence.Embedded;

/**
 * @author Jakub Laszkiewicz
 */
@Entity
public class AdminRoleConfirmation extends RoleConfirmation {
    @Embedded
    private AdminRole.AdminType adminType;

    public AdminRoleConfirmation(String name, AdminRole.AdminType adminType) {
        super(name, adminType);
    }

    public Role.Type getType() {
        return adminType;
    }

    public void setType(Role.Type type) {
        CheckTool.checkNull(type, "type");
        if (!(type instanceof AdminRole.AdminType))
            throw new IllegalArgumentException("type");
        adminType = (AdminRole.AdminType) type;
    }
}
