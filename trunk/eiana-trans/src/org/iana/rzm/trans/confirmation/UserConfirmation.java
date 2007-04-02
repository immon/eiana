package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;

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
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "RZMUser_objId")
    private RZMUser user;

    private UserConfirmation() {}

    public UserConfirmation(RZMUser user) {
        this.user = user;
    }

    public boolean isAcceptableBy(RZMUser user) {
        return this.user.equals(user);
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
        Set<RZMUser> result = new HashSet<RZMUser>();
        if (!accepted) result.add(user);
        return result;
    }
}
