package org.iana.rzm.trans.epp.mock.request;

import org.iana.epp.request.ChangeRef;
import org.iana.epp.request.DeleteChange;

/**
 * @author Jakub Laszkiewicz
 */
public class MockDeleteChange extends MockRequest implements DeleteChange {
    public ChangeRef getReference() {
        return new MockChangeRef();
    }
}
