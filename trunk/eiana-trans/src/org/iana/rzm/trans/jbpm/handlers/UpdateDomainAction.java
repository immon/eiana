/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.change.ObjectChange;
import org.iana.rzm.trans.change.ChangeApplicator;
import org.iana.rzm.trans.change.DomainDiffConfiguration;

public class UpdateDomainAction implements ActionHandler {
    public void execute(org.jbpm.graph.exe.ExecutionContext executionContext) throws java.lang.Exception {

        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        if(td != null) {
            DomainManager domainManager = (DomainManager) executionContext.getJbpmContext().getObjectFactory().createObject("domainManager");

            Domain retrievedDomain = domainManager.get(td.getCurrentDomain().getObjId());

            ObjectChange change = td.getDomainChange();
            ChangeApplicator.applyChange(retrievedDomain, change, DomainDiffConfiguration.getInstance());
            domainManager.update(retrievedDomain);
        }
    }
}
