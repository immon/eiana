package org.iana.rzm.user;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * <p>
 * This class represents a 'regular' (not administrator) user of the system. Those users embrace individuals acting
 * on behalf of supporing organizations, administrator or technical contacts of the top level domains.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 * @author Marcin Zajaczkowski
 */
@Entity
public class SystemUser extends RZMUser {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "SystemUser_Roles",
            inverseJoinColumns = @JoinColumn(name = "Role_objId"))
    private List<Role> roles;

    public SystemUser() {
        this.roles = new ArrayList<Role>();
    }

    final public List<Role> getRoles() {
        return Collections.unmodifiableList(roles);
    }

    final public void setRoles(List<Role> roles) {
        this.roles.clear();
        if (roles != null) this.roles.addAll(roles);
    }

    final public void addRole(Role role) {
        this.roles.add(role);
    }

    final public boolean removeRole(Role role) {
        return this.roles.remove(role);
    }

    final public void clearRoles() {
        this.roles.clear();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SystemUser that = (SystemUser) o;

        if (roles != null ? !roles.equals(that.roles) : that.roles != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        return result;
    }
}
