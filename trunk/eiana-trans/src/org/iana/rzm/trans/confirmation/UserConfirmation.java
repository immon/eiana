package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;

import javax.persistence.*;

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
}
