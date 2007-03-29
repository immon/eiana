package org.iana.rzm.trans.confirmation;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;

import javax.persistence.Entity;
import javax.persistence.Embedded;

/**
 * @author Jakub Laszkiewicz
 */
@Entity
public class SystemRoleConfirmation extends RoleConfirmation {
    @Embedded
    private SystemRole.SystemType systemType;

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
}
