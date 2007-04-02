package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;

import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Confirmation {

    /**
     * Checks whether <code>user</code> is eligible to accept this <code>Confirmation</code>.
     * @param user to be checked.
     * @return <code>true</code> when <code>user</code> is eligible to accept this <code>Confirmation</code>
     * or <code>false</code> otherwise.
     */
    public boolean isAcceptableBy(RZMUser user);

    /**
     * Accepts this <code>Confirmation</code> on behalf of the <code>user</code>.
     * @param user who accepts this <code>Confirmation</code>.
     * @return <code>true</code> when this <code>Confirmation</code> is accepted
     * or <code>false</code> when there are still users, who have to accept this <code>Confirmation</code>.
     * @throws AlreadyAcceptedByUser when the <code>user</code> already accepted this <code>Confirmation</code>.
     * @throws NotAcceptableByUser when the <code>user</code> is not eligible to accept this <code>Confirmation</code>.
     */
    public boolean accept(RZMUser user) throws AlreadyAcceptedByUser, NotAcceptableByUser;

    /**
     * Checks whether this <code>Confirmation</code> is accepted.
     * @return <code>true</code> when this <code>Confirmation</code> is accepted
     * or <code>false</code> when there are still users, who have to accept this <code>Confirmation</code>.
     */
    public boolean isReceived();

    /**
     * Provides set of <code>RZMUser</code>s required or eligible to accept this <code>Confirmation</code>.
     * @return <code>Set</code> of <code>RZMUser</code>s required or eligible to accept this <code>Confirmation</code>.
     */
    public Set<RZMUser> getUsersAbleToAccept();
}
