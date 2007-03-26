package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Confirmation {

    public boolean isAcceptableBy(RZMUser user);
    
    public boolean accept(RZMUser user) throws AlreadyAcceptedByUser, NotAcceptableByUser;

    public boolean isReceived();
}
