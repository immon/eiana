package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class MandatoryContactConfirmations implements Confirmation {

    private Set<UserConfirmation> receivedConfirmation;

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
