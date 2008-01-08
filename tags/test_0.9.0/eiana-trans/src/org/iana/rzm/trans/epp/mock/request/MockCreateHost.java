package org.iana.rzm.trans.epp.mock.request;

import org.iana.epp.EPPHost;
import org.iana.epp.request.CreateHost;

/**
 * @author Jakub Laszkiewicz
 */
public class MockCreateHost extends MockRequest implements CreateHost {
    public EPPHost getHost() {
        return null;
    }
}
