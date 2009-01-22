package org.iana.rzm.trans.epp;

import org.iana.epp.EPPOperationFactory;
import org.iana.epp.request.ChangeRequest;
import org.iana.epp.request.EPPChange;
import org.iana.objectdiff.Change;
import org.iana.objectdiff.CollectionChange;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.HostManager;
import org.iana.rzm.trans.Transaction;

/**
 * Generates epp delete host requests.
 */
public class EPPDeleteHosts extends EPPCommand {

    public EPPDeleteHosts(Transaction transaction, HostManager hostManager, EPPOperationFactory operationFactory) {
        super(transaction, hostManager, operationFactory);
    }

    public void collectChanges(ChangeRequest req) {
        CollectionChange nsChange = getNameServerChange();
        for (Change change : nsChange.getRemoved()) {
            ObjectChange hostChange = (ObjectChange) change;
            String hostName = hostChange.getId();
            Host host = getHost(hostName);
            if (host != null && !host.isShared()) {
                EPPChange delete = getOperationFactory().getDeleteHostRequest(getId(req), hostName);
                req.addChange(delete);
            }
        }
    }
}
