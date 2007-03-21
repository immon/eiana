package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * This class notifies all required contacts that a domain transaction needs to be confirmed.
 *
 * @author Patrycja Wegrzynowicz
 */
public class ContactNotifier extends ProcessStateNotifier implements ActionHandler {

    public void execute(ExecutionContext executionContext) throws Exception {

    }
}
