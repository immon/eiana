package org.iana.rzm.trans.confirmation;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.jbpm.configuration.ObjectFactory;
import org.jbpm.JbpmContext;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Jakub Laszkiewicz
 */
@Entity
public class AdminRoleConfirmation extends RoleConfirmation {
    @Enumerated
    private AdminRole.AdminType adminType;

    private AdminRoleConfirmation() {}

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

    public Set<RZMUser> getUsersAbleToAccept() {
        ObjectFactory of = JbpmContext.getCurrentJbpmContext().getObjectFactory();
        UserManager um = (UserManager) of.createObject("userManager");
        return new HashSet<RZMUser>(um.findUsersInAdminRole(adminType));
    }
}
