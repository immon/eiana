package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Confirmation {

    public boolean isAcceptableBy(RZMUser user);
    
    public boolean accept(RZMUser user);

    public boolean isReceived();
}
