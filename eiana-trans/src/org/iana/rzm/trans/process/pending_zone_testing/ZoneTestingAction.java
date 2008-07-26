package org.iana.rzm.trans.process.pending_zone_testing;

import org.apache.log4j.*;
import org.iana.dns.*;
import org.iana.dns.check.*;
import org.iana.dns.obj.*;
import org.iana.objectdiff.*;
import org.iana.rzm.domain.*;
import org.iana.rzm.trans.*;
import org.iana.rzm.trans.dns.*;
import org.iana.rzm.trans.process.general.handlers.*;
import org.jbpm.configuration.*;
import org.jbpm.graph.exe.*;

import java.util.*;

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
        List<DNSHost> rootServers = (List) objectFactory.createObject("rootServersList");

        Domain retrievedDomain = domainManager.get(td.getCurrentDomain().getName()).clone();
        ObjectChange change = td.getDomainChange();
        if (change != null)
            ChangeApplicator.applyChange(retrievedDomain, change, diffConfig);

        DNSDomain dnsDomain = DNSConverter.toDNSDomain(retrievedDomain);

        long serialNumber = -1;
        for (DNSHost rootServer : rootServers) {
            DNSNameServer dnsNameServer = new DNSNameServer(new DNSDomainImpl("."), rootServer, 1);
            serialNumber = dnsNameServer.getSerialNumber();
            if (serialNumber > -1) break;
        }

        try {
            zoneCheck.check(dnsDomain);
            td.setSerialNumber(String.valueOf(serialNumber));
            executionContext.leaveNode("accept");
        } catch (DNSTechnicalCheckException e) {
            Logger.getLogger(ZoneTestingAction.class).warn(e.getMessage());
        }
    }
}
