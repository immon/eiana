package org.iana.rzm.trans.jbpm.handlers;

import org.iana.objectdiff.ChangeApplicator;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.techcheck.TechChecker;
import org.iana.rzm.techcheck.exceptions.DomainCheckException;
import org.iana.rzm.trans.TransactionData;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;

/**
 * @author: Piotr Tkaczyk
 */
public class SuppTechnicalCheck implements DecisionHandler {

    boolean doTest;

    public String decide(ExecutionContext executionContext) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        if (td != null && doTest) {
            DomainManager domainManager = (DomainManager) executionContext.getJbpmContext().getObjectFactory().createObject("domainManager");
            DiffConfiguration diffConfig = (DiffConfiguration) executionContext.getJbpmContext().getObjectFactory().createObject("diffConfig");

            Domain retrievedDomain = domainManager.get(td.getCurrentDomain().getName()).clone();

            ObjectChange change = td.getDomainChange();

            if (change != null) {
                ChangeApplicator.applyChange(retrievedDomain, change, diffConfig);
                try {
                    //todo
                    TechChecker.checkDomain(retrievedDomain);
                } catch (DomainCheckException e) {
                    return "error";
                }
            }
        }
        return "test-ok";
    }
}
