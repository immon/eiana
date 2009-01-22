package org.iana.rzm.trans.epp.poll;

import org.apache.log4j.*;
import org.iana.epp.*;
import org.iana.epp.exceptions.*;
import org.iana.epp.request.*;
import org.iana.epp.response.*;
import org.iana.rzm.trans.epp.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EPPPollReq {

    private EPPClient eppClient;

    private EPPIdGenerator generator;

    private static Logger logger = Logger.getLogger(EPPPollReq.class);

    public EPPPollReq(EPPClient eppClient, EPPIdGenerator generator) {
        this.eppClient = eppClient;
        this.generator = generator;
    }

    public PollResponse query() throws EPPFrameworkException, EPPException {
        EPPOperationFactory operationFactory = eppClient.getEppOperationFactory();
        PollRequest req = operationFactory.getPollRequest(generator.id());
        PollResponse rsp = eppClient.poll(req);

        if (rsp == null || !rsp.isSuccessful()) {
            logger.error(rsp == null ?
                         "Polling failed Got null response for poll" :
                         "Polling faled Got an error from Verisign EPP Server");
            throw new EPPNoSuccessfulRspException();
        }

        logger.debug("Change ID: " + rsp.getChangeRequestId());
        logger.debug("Message: " + rsp.getMessage());
        logger.debug("Message Count: " + rsp.getMessageCount());

        if (rsp.getMessageId() == null) {
            throw new EPPNoMsgException();
        }

        return rsp;
    }

    public void ack(PollResponse pollRsp) throws EPPFrameworkException, EPPException {
        EPPOperationFactory operationFactory = eppClient.getEppOperationFactory();
        AckRequest req = operationFactory.getAckRequest(generator.id(), pollRsp.getMessageId());
        AckResponse rsp = eppClient.ack(req);
        if (rsp == null || !rsp.isSuccessful()) {
            logger.error(rsp == null ?
                         " Acking failed: Got null response for poll" :
                         "Acking failed: Got an error from Verisign EPP Server");
            throw new EPPNoSuccessfulRspException();
        }
    }
}
