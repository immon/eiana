package org.iana.rzm.trans.epp.mock.request;

import org.iana.epp.request.DomainUpdate;

/**
 * @author Jakub Laszkiewicz
 */
public class MockDomainUpdate extends MockRequest implements DomainUpdate {
    public String getDomainName() {
        return "";
    }

    public void removeHost(String string) {
    }

    public void addHost(String string) {
    }
}
