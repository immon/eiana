package org.iana.rzm.trans.process.pending_impacted_parties;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.process.general.handlers.DecisionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.List;
import java.util.Set;

/**
 * @author: Piotr Tkaczyk
 */
public class NSSharedGlueChangeDecision extends DecisionExceptionHandler {

    public String doDecide(ExecutionContext executionContext) throws Exception {
        Transaction trans = new Transaction(executionContext.getProcessInstance());
        if (trans.isNameServerChange()) {
            // check if shared...
            DomainManager domainManager = (DomainManager) executionContext.getJbpmContext().getObjectFactory().createObject("domainManager");
            Set<String> updatedNameServers = trans.getAddedOrUpdatedNameServers();
            if (!updatedNameServers.isEmpty()) {
                List<Domain> domains = domainManager.findDelegatedTo(updatedNameServers);
                return domains.isEmpty() ? "no" : "yes";
            }
        }
        return "no";
    }
}
