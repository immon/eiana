package org.iana.rzm.trans;

import org.iana.rzm.trans.dns.CheckHelper;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Piotr Tkaczyk
 */
public class MockTechnicalCheckHelper implements CheckHelper {

    public boolean check(ExecutionContext executionContext, String period) throws Exception {
        return false;
    }
}
