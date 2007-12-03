package org.iana.rzm.trans.epp;

import org.iana.epp.EPPClient;
import org.iana.epp.EPPOperationFactory;
import org.iana.epp.Problem;
import org.iana.epp.exceptions.EPPFrameworkException;
import org.iana.epp.internal.verisign.VerisignEPPClient;
import org.iana.epp.request.AckRequest;
import org.iana.epp.request.PollRequest;
import org.iana.epp.response.AckResponse;
import org.iana.epp.response.PollResponse;
import org.iana.epp.response.Response;

import java.rmi.server.UID;

/**
 * @author Jakub Laszkiewicz
 */
public class EPPPollRequest {
    private EPPClient client;
    private EPPOperationFactory operationFactory;

    public EPPPollRequest() {
        this(VerisignEPPClient.getEPPClient("../conf/epp/epp.rootzone.config"));
    }

    public EPPPollRequest(EPPClient client) {
        this.client = client;
        operationFactory = client.getEppOperationFactory();
    }

    public EppChangeRequestPollRsp send() throws EPPFrameworkException, EPPException {
        PollRequest pollRequest = operationFactory.getPollRequest(generateTrId());
        PollResponse pollResponse = client.poll(pollRequest);
        checkResponse(pollResponse);

        if (pollResponse.getMessageId() == null)
            throw new EPPException("message id is null");

        AckRequest ackRequest = operationFactory.getAckRequest(generateTrId(), pollResponse.getMessageId());
        AckResponse ackResponse = client.ack(ackRequest);
        checkResponse(ackResponse);

        return new EppChangeRequestPollRsp(pollResponse.getChangeStatus(), pollResponse.getChangeRequestId());
    }

    private void checkResponse(Response response) throws EPPException {
        if (response == null)
            throw new EPPException("response is null");
        if (!response.isSuccessful()) {
            EPPCompositeException ex = new EPPCompositeException();
            for (Problem problem : response.getErrors())
                ex.addException(new EPPException(problem.getCode(), problem.getMessage(),
                        problem.getErrors()));
            throw ex;
        }
    }

    private String generateTrId() {
        return "" + new UID();
    }
}
