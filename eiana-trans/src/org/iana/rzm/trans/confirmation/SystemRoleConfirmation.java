package org.iana.rzm.trans.confirmation;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;
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
public class SystemRoleConfirmation extends RoleConfirmation {
    @Enumerated
    private SystemRole.SystemType systemType;

    private SystemRoleConfirmation() {}

    public SystemRoleConfirmation(String name, Role.Type type) {
        super(name, type);
    }

    protected Role.Type getType() {
        return systemType;
    }

    protected void setType(Role.Type type) {
        CheckTool.checkNull(type, "type");
        if (!(type instanceof SystemRole.SystemType))
            throw new IllegalArgumentException("type");
        systemType = (SystemRole.SystemType) type;
    }

    public Set<RZMUser> getUsersAbleToAccept() {
        ObjectFactory of = JbpmContext.getCurrentJbpmContext().getObjectFactory();
        UserManager um = (UserManager) of.createObject("userManager");
        return new HashSet<RZMUser>(um.findUsersInSystemRole(getName(), systemType, true, false));
    }
}
