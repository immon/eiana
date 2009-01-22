package org.iana.rzm.trans.dns;

import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Piotr Tkaczyk
 */
public interface CheckHelper {

    public boolean check(ExecutionContext executionContext, String period) throws Exception;

}
