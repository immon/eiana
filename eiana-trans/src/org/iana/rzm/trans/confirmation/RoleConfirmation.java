package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;

import javax.persistence.Basic;
import javax.persistence.Entity;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class RoleConfirmation extends AbstractConfirmation {
    @Basic
    private boolean accepted = false;

    public RoleConfirmation() {}

    public RoleConfirmation(String name, SystemRole.SystemType type) {
        super(name, type);
    }

    public boolean isAcceptableBy(RZMUser user) {
        return user.isEligibleToAccept(getName(), getType());
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
}
