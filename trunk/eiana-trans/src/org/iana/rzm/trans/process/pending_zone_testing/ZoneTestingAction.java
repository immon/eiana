package org.iana.rzm.trans.process.pending_zone_testing;

import org.iana.dns.DNSDomain;
import org.iana.dns.check.DNSNameServer;
import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.dns.obj.DNSHostImpl;
import org.iana.objectdiff.ChangeApplicator;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.dns.DNSConverter;
import org.iana.rzm.trans.dns.DNSTechnicalCheckFactory;
import org.iana.rzm.trans.process.general.handlers.ActionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */

public class ZoneTestingAction extends ActionExceptionHandler {
    List<String> rootServers;

    protected void doExecute(ExecutionContext executionContext) throws Exception {
        if (rootServers == null || rootServers.isEmpty()) {
            executionContext.leaveNode("accept");
            return;
        }

        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        DomainManager domainManager = (DomainManager) executionContext.getJbpmContext().getObjectFactory().createObject("domainManager");
        DiffConfiguration diffConfig = (DiffConfiguration) executionContext.getJbpmContext().getObjectFactory().createObject("diffConfig");

        Domain retrievedDomain = domainManager.get(td.getCurrentDomain().getName()).clone();
        ObjectChange change = td.getDomainChange();
        if (change != null)
            ChangeApplicator.applyChange(retrievedDomain, change, diffConfig);

        DNSDomain dnsDomain = DNSConverter.toDNSDomain(retrievedDomain);

        Set<DNSNameServer> nameServers = new HashSet<DNSNameServer>();
        for (String ns : rootServers)
            nameServers.add(new DNSNameServer(dnsDomain, new DNSHostImpl(ns)));

        try {
            DNSTechnicalCheckFactory.getZoneCheck().check(dnsDomain, nameServers);
            executionContext.leaveNode("accept");
        } catch (DNSTechnicalCheckException e) {
        }
    }
}
