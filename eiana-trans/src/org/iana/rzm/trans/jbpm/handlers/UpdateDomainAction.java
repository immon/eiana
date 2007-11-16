package org.iana.rzm.trans.jbpm.handlers;

import org.iana.objectdiff.ChangeApplicator;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.TransactionData;

/**
 * @author Piotr Tkaczyk
 */
public class UpdateDomainAction extends ActionExceptionHandler {
    public void doExecute(org.jbpm.graph.exe.ExecutionContext executionContext) throws java.lang.Exception {

        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        if (td != null) {
            DomainManager domainManager = (DomainManager) executionContext.getJbpmContext().getObjectFactory().createObject("domainManager");
            DiffConfiguration diffConfig = (DiffConfiguration) executionContext.getJbpmContext().getObjectFactory().createObject("diffConfig");

            Domain retrievedDomain = domainManager.get(td.getCurrentDomain().getName());

            ObjectChange change = td.getDomainChange();
            if (change != null) {
                String requestor = td.getCreatedBy();
                long now = System.currentTimeMillis();
                ChangeApplicator.applyChange(retrievedDomain, change, diffConfig);
                retrievedDomain.modify(now, requestor);
                domainManager.update(retrievedDomain);
            }
        }
    }
}
