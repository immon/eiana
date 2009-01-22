package org.iana.rzm.trans.epp.mock.request;

import org.iana.epp.request.DeleteHost;

/**
 * @author Jakub Laszkiewicz
 */
public class MockDeleteHost extends MockRequest implements DeleteHost {
    public String getHostToDelete() {
        return "";
    }
}
