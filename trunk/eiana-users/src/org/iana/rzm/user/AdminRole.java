package org.iana.rzm.user;

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

    public AdminRole() {}

    public AdminRole(AdminType type) {
        super(type);
    }

    @Enumerated
    private AdminType getSystemType() {
        return (AdminType) super.getType();
    }

    private void setSystemType(AdminType type) {
        super.setType(type);
    }

    final public AdminType getType() {
        return (AdminType) super.getType();
    }

    final public void setType(AdminType type) {
        super.setType(type);
    }

    final public boolean isAdmin() {
        return true;
    }

    public boolean equals(Object object) {
        return object instanceof AdminRole && super.equals(object);
    }
}
