package org.iana.rzm.trans.epp.mock.response;

import org.iana.epp.response.ChangeCheckResponse;

/**
 * @author Jakub Laszkiewicz
 */
public class MockChangeCheckResponse extends MockResponse implements ChangeCheckResponse {
    public boolean isExist() {
        return false;
    }

    public String getRequestId() {
        return null;
    }
}
