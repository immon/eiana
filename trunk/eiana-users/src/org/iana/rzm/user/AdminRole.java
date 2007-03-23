package org.iana.rzm.user;

import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.Enumerated;
import javax.persistence.Entity;

/**
 * <p>
 * This class represents a role, which is owned by administrator of the system. An administrator
 * may be one of the following types: IANA, government (currently represented by USDoC)
 * and zone publisher (currently represented by VeriSign).
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class AdminRole extends Role {
    public enum AdminType implements Role.Type {
        IANA,
        GOV_OVERSIGHT,
        ZONE_PUBLISHER
    }

    @Enumerated
    private AdminType type;

    public AdminRole() {}

    public AdminRole(AdminType type) {
        super(type);
    }

    final public AdminType getType() {
        return type;
    }

    final public void setType(Type type) {
        CheckTool.checkNull(type, "type");
        if (!(type instanceof AdminType))
            throw new IllegalArgumentException("type");
        this.type = (AdminType) type;
    }

    final public boolean isAdmin() {
        return true;
    }

    public boolean equals(Object object) {
        return object instanceof AdminRole && super.equals(object);
    }
}
