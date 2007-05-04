/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.change.DomainDiffConfiguration;
import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.ChangeApplicator;
import org.iana.objectdiff.DiffConfiguration;

public class UpdateDomainAction implements ActionHandler {
    public void execute(org.jbpm.graph.exe.ExecutionContext executionContext) throws java.lang.Exception {

        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        if(td != null) {
            DomainManager domainManager = (DomainManager) executionContext.getJbpmContext().getObjectFactory().createObject("domainManager");
            DiffConfiguration diffConfig = (DiffConfiguration) executionContext.getJbpmContext().getObjectFactory().createObject("diffConfiguration");

            Domain retrievedDomain = domainManager.get(td.getCurrentDomain().getName());

            ObjectChange change = td.getDomainChange();
            ChangeApplicator.applyChange(retrievedDomain, change, diffConfig);
            domainManager.update(retrievedDomain);
        }
    }
}
