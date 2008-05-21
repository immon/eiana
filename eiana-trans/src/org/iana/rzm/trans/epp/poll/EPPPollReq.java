package org.iana.rzm.trans.epp.poll;

import org.iana.epp.EPPClient;
import org.iana.epp.EPPOperationFactory;
import org.iana.epp.exceptions.EPPFrameworkException;
import org.iana.epp.request.AckRequest;
import org.iana.epp.request.PollRequest;
import org.iana.epp.response.AckResponse;
import org.iana.epp.response.PollResponse;
import org.iana.rzm.trans.epp.EPPException;
import org.iana.rzm.trans.epp.EPPIdGenerator;
import org.iana.rzm.trans.epp.EPPNoSuccessfulRspException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EPPPollReq {

    private EPPClient eppClient;

    private EPPIdGenerator generator;

    public EPPPollReq(EPPClient eppClient, EPPIdGenerator generator) {
        this.eppClient = eppClient;
        this.generator = generator;
    }

    public PollResponse query() throws EPPFrameworkException, EPPException {
        EPPOperationFactory operationFactory = eppClient.getEppOperationFactory();
        PollRequest req = operationFactory.getPollRequest(generator.id());
        PollResponse rsp = eppClient.poll(req);
        if (rsp == null || !rsp.isSuccessful()) throw new EPPNoSuccessfulRspException();
        if (rsp.getMessageId() == null) throw new EPPNoMsgException();
        return rsp;
    }

    public void ack(PollResponse pollRsp) throws EPPFrameworkException, EPPException {
        EPPOperationFactory operationFactory = eppClient.getEppOperationFactory();
        AckRequest req = operationFactory.getAckRequest(generator.id(), pollRsp.getMessageId());
        AckResponse rsp = eppClient.ack(req);
        if (rsp == null || !rsp.isSuccessful()) throw new EPPNoSuccessfulRspException();
    }
}
