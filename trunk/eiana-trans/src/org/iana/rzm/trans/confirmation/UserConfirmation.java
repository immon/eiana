package org.iana.rzm.trans.confirmation;

import org.jbpm.graph.exe.ProcessInstance;
import org.iana.rzm.user.RZMUser;

/**
 * @author Patrycja Wegrzynowicz
 */
public class UserConfirmation implements Confirmation {

    public boolean isAcceptableBy(RZMUser user) {
        return false;
    }

    public boolean accept(RZMUser user) {
        return false;
    }

    public boolean isReceived() {
        return false;
    }
}
