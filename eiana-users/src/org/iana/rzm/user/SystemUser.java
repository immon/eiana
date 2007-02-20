package org.iana.rzm.user;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class SystemUser extends User {

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
}
