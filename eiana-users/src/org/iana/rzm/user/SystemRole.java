package org.iana.rzm.user;

import org.iana.rzm.common.Name;
import org.iana.rzm.common.exceptions.InvalidNameException;

import javax.persistence.Basic;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;

/**
 * <p>
 * This class represents a role, which is owned by 'regular' (not administrator) user of the system.
 * Those users embrace individuals acting on behalf of supporing organizations, administrator
 * or technical contacts of the top level domains.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 * @author Marcin Zajaczkowski
 */
@Entity
public class SystemRole extends Role {
    public enum SystemType implements Role.Type {
        AC, //Administrative Contact
        TC, //Technical Contact
        SO  //Supporting Organization
    }

    @Embedded
    private Name name;
    @Basic
    private boolean notify;
    @Basic
    private boolean acceptFrom;
    @Basic
    private boolean mustAccept;

    public SystemRole() {}

    public SystemRole(SystemType type) {
        super(type);
    }

    @Enumerated
    private SystemType getSystemType() {
        return (SystemType) super.getType();
    }

    private void setSystemType(SystemType type) {
        super.setType(type);
    }

    final public SystemType getType() {
        return (SystemType) super.getType();
    }

    final public void setType(SystemType type) {
        super.setType(type);
    }

    final public String getName() {
        return name == null ? null : name.getName();
    }

    final public void setName(String name) throws InvalidNameException {
        this.name = new Name(name);
    }

    final public boolean isNotify() {
        return notify;
    }

    final public void setNotify(boolean notify) {
        this.notify = notify;
    }

    final public boolean isAcceptFrom() {
        return acceptFrom;
    }

    final public void setAcceptFrom(boolean acceptFrom) {
        this.acceptFrom = acceptFrom;
    }

    final public boolean isMustAccept() {
        return mustAccept;
    }

    final public void setMustAccept(boolean mustAccept) {
        this.mustAccept = mustAccept;
    }

    final public boolean isAdmin() {
        return false;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SystemRole that = (SystemRole) o;

        if (acceptFrom != that.acceptFrom) return false;
        if (mustAccept != that.mustAccept) return false;
        if (notify != that.notify) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (notify ? 1 : 0);
        result = 31 * result + (acceptFrom ? 1 : 0);
        result = 31 * result + (mustAccept ? 1 : 0);
        return result;
    }
}
