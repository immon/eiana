package org.iana.rzm.trans.epp;

import org.iana.epp.EPPClient;
import org.iana.epp.EPPOperationFactory;
import org.iana.epp.Problem;
import org.iana.epp.exceptions.EPPFrameworkException;
import org.iana.epp.internal.verisign.VerisignEPPClient;
import org.iana.epp.request.PollRequest;
import org.iana.epp.response.PollResponse;

/**
 * @author Jakub Laszkiewicz
 */
public class EPPPollRequest {
    private EPPClient client;
    private Long requestId;
    private EPPOperationFactory operationFactory;

    public EPPPollRequest(Long requestId) {
        this(requestId, VerisignEPPClient.getEPPClient("../conf/epp/epp.rootzone.config"));
    }

    public EPPPollRequest(Long requestId, EPPClient client) {
        this.requestId = requestId;
        this.client = client;
        operationFactory = client.getEppOperationFactory();
    }

    public String send() throws EPPFrameworkException, EPPException {
        PollRequest request = operationFactory.getPollRequest(requestId.toString());
        PollResponse rsp = client.poll(request);
        if (!rsp.isSuccessful()) {
            EPPCompositeException ex = new EPPCompositeException();
            for (Problem problem : rsp.getErrors())
                ex.addException(new EPPException(problem.getCode(), problem.getMessage(),
                        problem.getErrors()));
            throw ex;
        }
        return rsp.getMessage();
    }
}
