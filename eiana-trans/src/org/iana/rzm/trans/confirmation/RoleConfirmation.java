package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * @author Patrycja Wegrzynowicz
 */
public class RoleConfirmation implements Confirmation {

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
