package org.iana.rzm.trans.epp.mock.request;

import org.iana.epp.request.ChangeRef;

/**
 * @author Jakub Laszkiewicz
 */
public class MockChangeRef implements ChangeRef {
    public String getRequestId() {
        return "";
    }

    public boolean isSubmited() {
        return false;
    }
}
