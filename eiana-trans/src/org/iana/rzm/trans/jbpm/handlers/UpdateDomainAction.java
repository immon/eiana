/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.TransactionAction;
import org.iana.rzm.trans.jbpm.handlers.update.WhoisUpdate;
import org.iana.rzm.trans.jbpm.handlers.update.RegistryURLUpdate;


import java.util.List;

public class UpdateDomainAction implements ActionHandler {
    public void execute(org.jbpm.graph.exe.ExecutionContext executionContext) throws java.lang.Exception {

        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        DomainManager domainManager = (DomainManager) executionContext.getJbpmContext().getObjectFactory().createObject("domainManager");

        Domain retrivedDomain = domainManager.get(td.getCurrentDomain().getName());

        List<TransactionAction> trActions = td.getActions();

        for (TransactionAction ta : trActions ) {
            if (ta.getName().equals(TransactionAction.Name.MODIFY_WHOIS_SERVER))
                new WhoisUpdate(ta.getChange(),  retrivedDomain);
            else
            if (ta.getName().equals(TransactionAction.Name.MODIFY_REGISTRATION_URL))
                new RegistryURLUpdate(ta.getChange(), retrivedDomain);
        }
        domainManager.update(retrivedDomain);
    }
}
