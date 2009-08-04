package org.iana.rzm.trans.epp;

import org.iana.epp.EPPClient;
import org.iana.epp.EPPOperationFactory;
import org.iana.epp.exceptions.EPPFrameworkException;
import org.iana.epp.request.ChangeRef;
import org.iana.epp.request.DeleteChange;
import org.iana.epp.response.Response;
import org.iana.rzm.trans.Transaction;

/**
 * @author Piotr Tkaczyk
 */
public class EPPDeleteChange {

    private EPPClient eppClient;

    private String eppRequestId;

    public EPPDeleteChange(EPPClient eppClient, Transaction transaction) {
        this.eppClient = eppClient;
        this.eppRequestId =  new EPPChangeReqId(transaction).id();
    }

    public void execute() throws EPPException {
        try {
            EPPOperationFactory operationFactory = eppClient.getEppOperationFactory();
            ChangeRef ref = operationFactory.getChangeRef(eppRequestId, true);
            DeleteChange del = operationFactory.getDeleteChangeRequest(eppRequestId, ref);
            
            Response rsp = eppClient.delete(del);
            if (rsp == null || !rsp.isSuccessful()) throw new EPPNoSuccessfulRspException();
        } catch (EPPFrameworkException e) {
            throw new EPPException(e);
        }
    }
}
