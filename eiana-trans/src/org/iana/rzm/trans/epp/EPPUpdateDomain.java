package org.iana.rzm.trans.epp;

import org.iana.epp.EPPOperationFactory;
import org.iana.epp.request.ChangeRequest;
import org.iana.epp.request.DomainUpdate;
import org.iana.objectdiff.Change;
import org.iana.objectdiff.CollectionChange;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.domain.HostManager;
import org.iana.rzm.trans.Transaction;

/**
 * Generates epp update domain request.
 */
public class EPPUpdateDomain extends EPPCommand {

    public EPPUpdateDomain(Transaction transaction, HostManager hostManager, EPPOperationFactory operationFactory) {
        super(transaction, hostManager, operationFactory);
    }

    public void collectChanges(ChangeRequest req) {
        CollectionChange nsChange = getNameServerChange();
        if (nsChange.getAdded().isEmpty() && nsChange.getRemoved().isEmpty()) return;
        DomainUpdate update = getOperationFactory().getDomainUpdateRequest(getId(req), getDomainName());
        for (Change change : nsChange.getAdded()) {
            ObjectChange hostChange = (ObjectChange) change;
            update.addHost(hostChange.getId());
        }
        for (Change change : nsChange.getRemoved()) {
            ObjectChange hostChange = (ObjectChange) change;
            update.removeHost(hostChange.getId());
        }
        req.addChange(update);
    }

}
