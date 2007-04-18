package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.*;
import org.jbpm.JbpmContext;
import org.jbpm.configuration.ObjectFactory;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class RoleConfirmation extends AbstractConfirmation {
    @Basic
    private boolean accepted = false;
    @OneToOne(cascade = CascadeType.ALL)
    private Role role;

    protected RoleConfirmation() {}

    public RoleConfirmation(Role role) {
        this.role = role;
    }

    public boolean isAcceptableBy(RZMUser user) {
        return user.isInRole(role, new ConfirmationRoleComparator());
    }

    public boolean accept(RZMUser user) throws AlreadyAcceptedByUser, NotAcceptableByUser {
        if (!isAcceptableBy(user))
            throw new NotAcceptableByUser();
        if (accepted)
            throw new AlreadyAcceptedByUser();
        accepted = true;
        return isReceived();
    }

    public boolean isReceived() {
        return accepted;
    }

    public Set<RZMUser> getUsersAbleToAccept() {
        ObjectFactory of = JbpmContext.getCurrentJbpmContext().getObjectFactory();
        UserManager um = (UserManager) of.createObject("userManager");
        if (role.isAdmin()) {
            AdminRole adminRole = (AdminRole) role;
            return new HashSet<RZMUser>(um.findUsersInAdminRole(adminRole.getType()));
        } else {
            SystemRole systemRole = (SystemRole) role;
            return new HashSet<RZMUser>(um.findUsersInSystemRole(systemRole.getName(), systemRole.getType(), true, false));
        }
    }

    class ConfirmationRoleComparator implements Comparator<Role> {
        public int compare(Role o1, Role o2) {
            if (o1 == o2) return 0;

            if (o1 == null) return 1;

            if (o1.isAdmin() != o2.isAdmin()) return 1;

            if (o1.isAdmin()) {
                AdminRole ar1 = (AdminRole) o1;
                AdminRole ar2 = (AdminRole) o2;

                if (ar1.getType().equals(ar2.getType())) return 0;
            } else {
                SystemRole sr1 = (SystemRole) o1;
                SystemRole sr2 = (SystemRole) o2;

                if (!sr1.getName().equals(sr2.getName()) || !sr1.getType().equals(sr2.getType()))
                    return 1;

                if (sr1.isAcceptFrom() != sr2.isAcceptFrom()) return 1;

                return 0;
            }
            return 1;
        }
    }
}
