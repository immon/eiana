package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Enumerated;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public abstract class RoleConfirmation extends AbstractConfirmation {
    @Basic
    private boolean accepted = false;
    @Basic
    private String name;

    private RoleConfirmation() {}

    public RoleConfirmation(String name, Role.Type type) {
        this.name = name;
        setType(type);
    }

    protected abstract Role.Type getType();
    protected abstract void setType(Role.Type type);

    public boolean isAcceptableBy(RZMUser user) {
        return user.isEligibleToAccept(name, getType());
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

    public String getName() {
        return name;
    }
}
