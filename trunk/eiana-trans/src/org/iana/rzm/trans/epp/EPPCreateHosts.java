package org.iana.rzm.trans.epp;

import org.iana.epp.EPPHostAddress;
import org.iana.epp.EPPOperationFactory;
import org.iana.epp.request.ChangeRequest;
import org.iana.epp.request.EPPChange;
import org.iana.objectdiff.Change;
import org.iana.objectdiff.CollectionChange;
import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.SimpleChange;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.HostManager;
import org.iana.rzm.trans.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates epp create host requests.
 *
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */
public class EPPCreateHosts extends EPPCommand {

    public EPPCreateHosts(Transaction transaction, HostManager hostManager, EPPOperationFactory operationFactory) {
        super(transaction, hostManager, operationFactory);
    }

    public void collectChanges(ChangeRequest req) {
        CollectionChange nsChange = getNameServerChange();
        for (Change change : nsChange.getAdded()) {
            ObjectChange hostChange = (ObjectChange) change;
            String hostName = hostChange.getId();
            Host host = getHost(hostName);
            if (host == null) {
                EPPChange create = getOperationFactory().getCreateHostRequest(getId(req), hostName, toAddresses(hostChange));
                req.addChange(create);
            }
        }
    }

    private List<EPPHostAddress> toAddresses(ObjectChange hostChange) {
        List<EPPHostAddress> ret = new ArrayList<EPPHostAddress>();
        CollectionChange addressChange = (CollectionChange) hostChange.getFieldChanges().get("addresses");
        for (Change change : addressChange.getAdded()) {
            ObjectChange addrListChange = (ObjectChange) change;
            SimpleChange addrChange = (SimpleChange) addrListChange.getFieldChanges().get("address");
            String addrValue = addrChange.getNewValue();
            ret.add(toAddress(addrValue));
        }
        return ret;
    }
}
