package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * This class calculates parties that need to consent upon a process proceeds to the next state
 * from the PENDING_CONTACT_CONFIRMATION state.
 *
 * @author Patrycja Wegrzynowicz
 */
public class ContactConfirmationCalculator implements ActionHandler {

    public void execute(ExecutionContext executionContext) throws Exception {
        
    }
}
