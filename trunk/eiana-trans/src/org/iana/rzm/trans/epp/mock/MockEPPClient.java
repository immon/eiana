package org.iana.rzm.trans.epp.mock;

import org.iana.epp.EPPClient;
import org.iana.epp.EPPOperationFactory;
import org.iana.epp.exceptions.EPPFrameworkException;
import org.iana.epp.request.*;
import org.iana.epp.response.*;
import org.iana.rzm.trans.epp.mock.response.*;

/**
 * @author Jakub Laszkiewicz
 */
public class MockEPPClient implements EPPClient {
    private EPPOperationFactory of = new MockEPPOperationFactory();

    public EPPOperationFactory getEppOperationFactory() {
        return of;
    }

    public ChangeResponse submit(ChangeRequest changeRequest) throws EPPFrameworkException {
        return new MockChangeResponse();
    }

    public Response delete(DeleteChange deleteChange) throws EPPFrameworkException {
        return new MockResponse();
    }

    public ChangeCheckResponse check(ChangeCheckRequest changeCheckRequest) throws EPPFrameworkException {
        return new MockChangeCheckResponse();
    }

    public PollResponse poll(PollRequest pollRequest) throws EPPFrameworkException {
        return new MockPollResponse();
    }

    public AckResponse ack(AckRequest ackRequest) throws EPPFrameworkException {
        return new MockAckResponse();
    }

    public ChangeInfoResponse info(ChangeInfoRequest changeInfoRequest) throws EPPFrameworkException {
        return new MockChangeInfoResponse();
    }
}
