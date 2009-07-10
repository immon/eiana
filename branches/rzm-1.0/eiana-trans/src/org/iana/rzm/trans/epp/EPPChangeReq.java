package org.iana.rzm.trans.epp;

import org.iana.epp.ChangePriority;
import org.iana.epp.EPPClient;
import org.iana.epp.Problem;
import org.iana.epp.exceptions.EPPFrameworkException;
import org.iana.epp.request.ChangeRequest;
import org.iana.epp.response.ChangeResponse;
import org.iana.rzm.domain.HostManager;
import org.iana.rzm.trans.Transaction;

import java.util.Arrays;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class EPPChangeReq extends EPPCommand {

    private List<EPPCommand> collectors;

    private EPPClient client;

    public EPPChangeReq(Transaction transaction, HostManager hostManager, EPPClient client) {
        super(transaction, hostManager, client.getEppOperationFactory());
        this.client = client;
        collectors = Arrays.asList(
                new EPPCreateHosts(transaction, hostManager, getOperationFactory()),
                new EPPUpdateHosts(transaction, hostManager, getOperationFactory()),
                new EPPUpdateDomain(transaction, hostManager, getOperationFactory()),
                new EPPDeleteHosts(transaction, hostManager, getOperationFactory())
        );
    }

    public String[] send() throws EPPFrameworkException, EPPException {
        String clientID = getClientId();
        String transactionID = clientID;
        ChangeRequest request = getOperationFactory().getChangeRequest(clientID, transactionID, ChangePriority.NORMAL, getEffectedDomains());
        collectChanges(request);
        ChangeResponse rsp = client.submit(request);
        if (!rsp.isSuccessful()) {
            EPPCompositeException ex = new EPPCompositeException();
            for (Problem problem : rsp.getErrors())
                ex.addException(new EPPException(problem.getCode(), problem.getMessage(),
                        problem.getErrors()));
            throw ex;
        }
        setEppRequestId(clientID);
        return new String[]{clientID, rsp.getReceipt()};
    }

    public void collectChanges(ChangeRequest req) {
        for (EPPCommand collector : collectors) {
            collector.collectChanges(req);
        }
    }

}
