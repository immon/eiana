package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class MandatoryRoleConfirmations implements Confirmation {

    private Set<UserConfirmation> receivedConfirmations;
    // transient -> ustawiane leniwe
    private Set<RZMUser> requiredConfirmations;

    public boolean isAcceptableBy(RZMUser user) {
        // mandatoryUsers contains user
        return false;
    }

    public boolean accept(RZMUser user) {
        // isAcceptableBy(user)
        // receivedConfirmation
        // return isReceived()
        // return receivedConfirmation >= mandatoryUsers;
        return false;
    }

    public boolean isReceived() {
        //return receivedConfirmation >= mandatoryUsers;
        return false;
    }

    private Set<RZMUser> getRequiredConfirmations() {
        // return
        return null;
    }
}
