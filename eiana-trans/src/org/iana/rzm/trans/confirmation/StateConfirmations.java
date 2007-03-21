package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;
import java.util.Set;

/**
 * This class represents all confirmations required to obtain in a process state.
 *
 * @author Patrycja Wegrzynowicz
 */
public class StateConfirmations implements Confirmation {

    public Set<Confirmation> receivedConfirmations;
    public Set<Confirmation> pendingConfirmations;

    public boolean isAcceptableBy(RZMUser user) {
        return false;
    }

    public boolean accept(RZMUser user) {
        boolean ret = false;
/*
        for (Confirmation c : pendingConfirmations) {
            if (c.accept(user)) {
                pendingConfirmations.remove(c);
                receivedConfirmations.add(c);
                ret = true;
            }
        }
*/
        return ret;
    }

    public boolean isReceived() {
        return pendingConfirmations != null && !pendingConfirmations.isEmpty();
    }

}
