package org.iana.rzm.trans.epp.mock.request;

import org.iana.epp.request.AckRequest;

/**
 * @author Jakub Laszkiewicz
 */
public class MockAckRequest extends MockRequest implements AckRequest {
    public String getMessageId() {
        return "";
    }
}
