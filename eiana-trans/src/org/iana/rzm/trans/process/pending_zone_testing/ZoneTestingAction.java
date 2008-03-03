package org.iana.rzm.trans.process.pending_zone_testing;

import org.apache.log4j.Logger;
import org.iana.dns.DNSDomain;
import org.iana.dns.check.DNSTechnicalCheck;
import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.objectdiff.ChangeApplicator;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.dns.DNSConverter;
import org.iana.rzm.trans.process.general.handlers.ActionExceptionHandler;
import org.jbpm.configuration.ObjectFactory;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Jakub Laszkiewicz
 */

public class ZoneTestingAction extends ActionExceptionHandler {

    protected void doExecute(ExecutionContext executionContext) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        ObjectFactory objectFactory = executionContext.getJbpmContext().getObjectFactory();
        DomainManager domainManager = (DomainManager) objectFactory.createObject("domainManager");
        DiffConfiguration diffConfig = (DiffConfiguration) objectFactory.createObject("diffConfig");
        DNSTechnicalCheck zoneCheck = (DNSTechnicalCheck) objectFactory.createObject("zoneTestingCheck"); 

        Domain retrievedDomain = domainManager.get(td.getCurrentDomain().getName()).clone();
        ObjectChange change = td.getDomainChange();
        if (change != null)
            ChangeApplicator.applyChange(retrievedDomain, change, diffConfig);

        DNSDomain dnsDomain = DNSConverter.toDNSDomain(retrievedDomain);

        try {
            zoneCheck.check(dnsDomain);
            executionContext.leaveNode("accept");
        } catch (DNSTechnicalCheckException e) {
            Logger.getLogger(ZoneTestingAction.class).warn(e.getMessage());
        }
    }
}
