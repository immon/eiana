package org.iana.rzm.trans.epp.mock.response;

import org.iana.epp.response.PollResponse;

/**
 * @author Jakub Laszkiewicz
 */
public class MockPollResponse extends MockResponse implements PollResponse {
    public static final String POLL_MESSAGE_USDOC_APPROVED = "The DoC has approved this Change Request.";

    public String getMessage() {
        return POLL_MESSAGE_USDOC_APPROVED;
    }

    public String getMessageId() {
        return null;
    }

    public String getChangeStatus() {
        return null;
    }

    public long getMessageCount() {
        return 0;
    }

    public String getChangeRequestId() {
        return null;
    }
}
