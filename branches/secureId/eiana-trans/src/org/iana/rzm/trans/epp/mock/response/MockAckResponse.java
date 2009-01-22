package org.iana.rzm.trans.epp.mock.response;

import org.iana.epp.response.AckResponse;

/**
 * @author Jakub Laszkiewicz
 */
public class MockAckResponse extends MockResponse implements AckResponse {
    public String getNextMessageId() {
        return null;
    }

    public long getMessageCount() {
        return 0;
    }
}
