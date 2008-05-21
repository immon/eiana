package org.iana.rzm.trans.epp.info;

import org.iana.epp.EPPClient;
import org.iana.epp.EPPOperationFactory;
import org.iana.epp.exceptions.EPPFrameworkException;
import org.iana.epp.response.ChangeInfoResponse;
import org.iana.epp.request.ChangeRef;
import org.iana.epp.request.ChangeInfoRequest;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.epp.EPPIdGenerator;
import org.iana.rzm.trans.epp.EPPNoSuccessfulRspException;
import org.iana.rzm.trans.epp.EPPException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EPPChangeInfoReq {

    private EPPClient eppClient;

    private EPPIdGenerator generator;

    public EPPChangeInfoReq(EPPClient eppClient, EPPIdGenerator generator) {
        this.eppClient = eppClient;
        this.generator = generator;
    }

    public EPPChangeStatus queryStatus(Transaction trans) throws EPPException {
        try {
            EPPOperationFactory operationFactory = eppClient.getEppOperationFactory();
            ChangeRef ref = operationFactory.getChangeRef(trans.getEppRequestId(), true);
            ChangeInfoRequest req = operationFactory.getChangeInfoRequest(generator.id(), ref);
            ChangeInfoResponse rsp = eppClient.info(req);
            if (rsp == null || !rsp.isSuccessful()) throw new EPPNoSuccessfulRspException();
            return EPPChangeStatus.statusOf(rsp.getStatus());
        } catch (EPPFrameworkException e) {
            throw new EPPException(e);
        }
    }

}
