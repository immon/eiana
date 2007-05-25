package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.auth.Identity;

import javax.persistence.*;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class UserConfirmation extends AbstractConfirmation {
    @Basic
    private boolean accepted = false;
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "RZMUser_objId")
    private RZMUser user;

    private UserConfirmation() {}

    public UserConfirmation(RZMUser user) {
        this.user = user;
    }

    public boolean isAcceptableBy(Identity user) {
        if (user instanceof RZMUser) {
            return this.user.equals(user);
        }
        return false;
    }

    public boolean accept(Identity id) throws AlreadyAcceptedByUser, NotAcceptableByUser {
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

    public Set<Identity> getUsersAbleToAccept() {
        Set<Identity> result = new HashSet<Identity>();
        if (!accepted) result.add(user);
        return result;
    }
}
