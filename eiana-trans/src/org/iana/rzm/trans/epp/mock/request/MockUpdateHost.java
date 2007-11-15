package org.iana.rzm.trans.epp.mock.request;

import org.iana.epp.EPPHostAddress;
import org.iana.epp.request.UpdateHost;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class MockUpdateHost extends MockRequest implements UpdateHost {
    public String getHostName() {
        return null;
    }

    public List<EPPHostAddress> getAddressesToRemove() {
        return null;
    }

    public List<EPPHostAddress> getAddressToAdd() {
        return null;
    }
}
