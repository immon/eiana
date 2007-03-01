package org.iana.rzm.user;

import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Enumerated;

/**
 * <p>
 * It represents an administrator of the system. An administrator may be one of the following types:
 * IANA, government (currently represented by USDoC) and zone publisher (currently represented by VeriSign).
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class AdminUser extends User {

    public static enum Type implements UserType {
        IANA_STAFF,
        GOV_OVERSIGHT,
        ZONE_PUBLISHER
    }

    @Enumerated
    @Column(name = "adminUserType")
    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        CheckTool.checkNull(type, "type");
        this.type = type;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AdminUser adminUser = (AdminUser) o;

        if (type != adminUser.type) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
