package org.iana.rzm.trans.epp.mock.response;

import org.iana.epp.request.ChangeRef;
import org.iana.epp.response.ChangeResponse;

/**
 * @author Jakub Laszkiewicz
 */
public class MockChangeResponse extends MockResponse implements ChangeResponse {
    public ChangeRef getReference() {
        return null;
    }

    public boolean isSubmitted() {
        return true;
    }

    public String getReceipt() {
        return null;
    }
}
