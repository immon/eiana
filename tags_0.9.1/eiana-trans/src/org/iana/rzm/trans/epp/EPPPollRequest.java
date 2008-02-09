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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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

        if (pollResponse.getMessageCount() > 0) {
            if (pollResponse.getMessageId() == null)
                throw new EPPException("message id is null");
            AckRequest ackRequest = operationFactory.getAckRequest(generateTrId(), pollResponse.getMessageId());
            AckResponse ackResponse = client.ack(ackRequest);
            checkResponse(ackResponse);
        }

        return new EppChangeRequestPollRsp(pollResponse.getChangeStatus(),
                pollResponse.getChangeRequestId(),
                pollResponse.getMessageCount() > 0,
                pollResponse.getMessage(),
                toStringList(pollResponse.getErrors()));
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
        return encode("" + new UID());
    }

    private String encode(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte encoded[] = md.digest(input.getBytes());
            StringBuffer output = new StringBuffer();
            for (byte anEncoded : encoded)
                output.append(Integer.toHexString((0xff & anEncoded)));
            return output.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private List<String> toStringList(List<Problem> problems) {
        List<String> result = new ArrayList<String>();
        for (Problem prob : problems) {
            StringBuffer probString = new StringBuffer();
            probString.append(prob.getCode()).append(" ").append(prob.getMessage());
            for (String errString : prob.getErrors())
                probString.append("\n").append(errString);
        }
        return result;
    }
}
