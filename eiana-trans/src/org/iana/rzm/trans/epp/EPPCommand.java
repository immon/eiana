package org.iana.rzm.trans.epp;

import org.iana.epp.EPPHostAddress;
import org.iana.epp.EPPOperationFactory;
import org.iana.epp.request.ChangeRequest;
import org.iana.objectdiff.CollectionChange;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.HostManager;
import org.iana.rzm.domain.IPAddress;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.Transaction;

import java.util.List;
import java.util.ArrayList;

/**
 * An abstract command to generate epp command based on a transaction data.
 */
public abstract class EPPCommand {

    private Transaction transaction;

    private HostManager hostManager;

    private EPPOperationFactory operationFactory;

    protected EPPCommand(Transaction transaction, HostManager hostManager, EPPOperationFactory operationFactory) {
        this.transaction = transaction;
        this.hostManager = hostManager;
        this.operationFactory = operationFactory;
    }

    protected Host getHost(String name) {
        return hostManager.get(name);
    }

    protected EPPOperationFactory getOperationFactory() {
        return operationFactory;
    }

    public abstract void collectChanges(ChangeRequest req);

    protected String getId(ChangeRequest req) {
        String id = req.getRequestId();
        int size = req.getChanges().size();
        return id + "/" + size;
    }

    protected String getClientId() {
        EPPChangeReqId id = new EPPChangeReqId(transaction);
        return id.id();
    }

    protected String getTransactionId() {
        return getClientId();
    }

    protected String getDomainName() {
        String domainName = transaction.getCurrentDomain().getName();
        return domainName.toUpperCase();
    }

    protected List<String> getEffectedDomains() {
        List<String> ret = new ArrayList<String>();
        ret.add(getDomainName());
        for (Domain impacted : transaction.getImpactedDomains()) {
            ret.add(impacted.getName().toUpperCase());
        }
        return ret;
    }

    protected CollectionChange getNameServerChange() {
        ObjectChange domainChange = transaction.getDomainChange();
        return (CollectionChange) domainChange.getFieldChanges().get("nameServers");
    }

    protected EPPHostAddress toAddress(String addrValue) {
        IPAddress addr = IPAddress.createIPAddress(addrValue);
        return new EPPHostAddress(addr.getAddress(), addr.isIPv4() ? EPPHostAddress.Type.IP4 : EPPHostAddress.Type.IP6);
    }

    protected void setEppRequestId(String eppRequestId) {
        transaction.setEppRequestId(eppRequestId);
    }

}
