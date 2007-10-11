package org.iana.rzm.trans.epp;

import org.iana.epp.EPPClient;
import org.iana.epp.EPPOperationFactory;
import org.iana.epp.ChangePriority;
import org.iana.epp.response.PollResponse;
import org.iana.epp.response.ChangeResponse;
import org.iana.epp.request.PollRequest;
import org.iana.epp.request.ChangeRequest;
import org.iana.epp.request.DomainUpdate;
import org.iana.epp.request.EPPChange;
import org.iana.epp.exceptions.EPPFrameworkException;
import org.iana.epp.internal.verisign.VerisignEPPClient;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.domain.HostManager;
import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.Change;
import org.iana.objectdiff.CollectionChange;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 * @author Patrycja Wegrzynowicz
 */
public class EPPChangeRequest extends EPPCommand {

    private List<EPPCommand> collectors;

    private EPPClient client;

    public EPPChangeRequest(Transaction transaction, HostManager hostManager) {
        this(transaction, hostManager, VerisignEPPClient.getEPPClient("conf/epp.rootzone.config"));
    }

    public EPPChangeRequest(Transaction transaction, HostManager hostManager, EPPClient client) {
        super(transaction, hostManager, client.getEppOperationFactory());
        this.client = client;
        collectors = Arrays.asList(
                new EPPCreateHosts(transaction, hostManager, getOperationFactory()),
                new EPPUpdateHosts(transaction, hostManager, getOperationFactory()),
                new EPPUpdateDomain(transaction, hostManager, getOperationFactory()),
                new EPPDeleteHosts(transaction, hostManager, getOperationFactory())
        );
    }

    public void send() throws EPPFrameworkException {
        ChangeRequest request = getOperationFactory().getChangeRequest(getClientId(), getTransactionId(), ChangePriority.NORMAL, Arrays.asList(getDomainName()));
        collectChanges(request);
        client.submit(request);
    }

    public void collectChanges(ChangeRequest req) {
        for (EPPCommand collector : collectors) {
            collector.collectChanges(req);
        }
    }

}
