package org.iana.rzm.trans.epp;

import org.iana.epp.ChangePriority;
import org.iana.epp.EPPClient;
import org.iana.epp.EPPOperationFactory;
import org.iana.epp.exceptions.EPPFrameworkException;
import org.iana.epp.internal.verisign.VerisignEPPClient;
import org.iana.epp.request.ChangeRequest;
import org.iana.rzm.domain.HostManager;
import org.iana.rzm.trans.Transaction;

import java.util.Arrays;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 * @author Patrycja Wegrzynowicz
 */
public class EPPChangeRequest extends EPPCommand {

    private static EPPClient client = VerisignEPPClient.getEPPClient("conf/epp/epp.rootzone.config");

    private List<EPPCommand> collectors;

    public EPPChangeRequest(Transaction transaction, HostManager hostManager) {
        this(transaction, hostManager, client.getEppOperationFactory());
    }

    public EPPChangeRequest(Transaction transaction, HostManager hostManager, EPPOperationFactory operationFactory) {
        super(transaction, hostManager, operationFactory);
        collectors = Arrays.asList(
                new EPPCreateHosts(transaction, hostManager, operationFactory),
                new EPPUpdateHosts(transaction, hostManager, operationFactory),
                new EPPUpdateDomain(transaction, hostManager, operationFactory),
                new EPPDeleteHosts(transaction, hostManager, operationFactory)
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
