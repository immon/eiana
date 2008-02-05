package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.exe.ExecutionContext;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.confirmation.contact.ContactConfirmations;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.user.SystemRole;

import java.util.Set;
import java.util.HashSet;
import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public class NSSharedGlueChangeDecision extends DecisionExceptionHandler {

    public String doDecide(ExecutionContext executionContext) throws Exception {
        Transaction trans = new Transaction(executionContext.getProcessInstance());
        Set<ContactIdentity> contacts = new HashSet<ContactIdentity>();
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
