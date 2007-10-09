package org.iana.rzm.trans.epp;

import org.iana.epp.EPPHostAddress;
import org.iana.epp.EPPOperationFactory;
import org.iana.epp.request.ChangeRequest;
import org.iana.epp.request.EPPChange;
import org.iana.objectdiff.Change;
import org.iana.objectdiff.CollectionChange;
import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.SimpleChange;
import org.iana.rzm.domain.HostManager;
import org.iana.rzm.trans.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates epp update hosts requests.
 */
public class EPPUpdateHosts extends EPPCommand {

    public EPPUpdateHosts(Transaction transaction, HostManager hostManager, EPPOperationFactory operationFactory) {
        super(transaction, hostManager, operationFactory);
    }

    public void collectChanges(ChangeRequest req) {
        CollectionChange nsChange = getNameServerChange();
        for (Change change : nsChange.getModified()) {
            ObjectChange hostChange = (ObjectChange) change;
            String hostName = hostChange.getId();
            List<EPPHostAddress> toAdd = new ArrayList<EPPHostAddress>();
            List<EPPHostAddress> toRemove = new ArrayList<EPPHostAddress>();
            CollectionChange addrChange = (CollectionChange) hostChange.getFieldChanges().get("addresses");
            if (addrChange != null) {
                for (Change ipChange : addrChange.getAdded()) {
                    SimpleChange simpleChange = (SimpleChange) ipChange;
                    toAdd.add(toAddress(simpleChange.getNewValue()));
                }
                for (Change ipChange : addrChange.getRemoved()) {
                    SimpleChange simpleChange = (SimpleChange) ipChange;
                    toAdd.add(toAddress(simpleChange.getNewValue()));
                }
            }
            EPPChange update = getOperationFactory().getUpdateHost(getId(req), hostName, toAdd, toRemove);
            req.addChange(update);
        }
    }

}
