package org.iana.rzm.trans.confirmation;

import org.iana.rzm.auth.Identity;

import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Confirmation {

    /**
     * Determines whether a given <code>identity</code> is eligible to accept this <code>Confirmation</code>.
     * @param identity the identity to be checked.
     * @return <code>true</code> when the <code>identity</code> is eligible to accept this <code>Confirmation</code>
     * or <code>false</code> otherwise.
     */
    public boolean isAcceptableBy(Identity identity);

    /**
     * Accepts this <code>Confirmation</code> on behalf of the <code>user</code>.
     * @param user who accepts this <code>Confirmation</code>.
     * @return <code>true</code> when this <code>Confirmation</code> is accepted
     * or <code>false</code> when there are still users, who have to accept this <code>Confirmation</code>.
     * @throws AlreadyAcceptedByUser when the <code>user</code> already accepted this <code>Confirmation</code>.
     * @throws NotAcceptableByUser when the <code>user</code> is not eligible to accept this <code>Confirmation</code>.
     */
    public boolean accept(Identity user) throws AlreadyAcceptedByUser, NotAcceptableByUser;

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
    public Set<Identity> getUsersAbleToAccept();
}
